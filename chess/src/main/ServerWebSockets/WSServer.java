package ServerWebSockets;

import WSShared.OldGameCommand;
import WSShared.GameCommand;
import WSShared.GameMessage;
import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.AuthToken;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.JoinRequest;
import server.DAO.AuthTokenDAO;
import server.DAO.GameDAO;
import server.MyServerException;
import server.handlers.JoinHandler;
import server.services.LoginService;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@WebSocket
public class WSServer {
    private Gson gson = null;

    // Registry of "gameID_username" to sessions
    private HashMap<String, Session> registry = new HashMap<>();

    public WSServer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessMove.class, new ChessMoveDeserializer());
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceDeserializer());
        gson = builder.create();
    }

    // Notify all clients in the registry with the given message
    private void notifyClients(String message, String gameID) {
        for (Map.Entry<String, Session> entry : registry.entrySet()) {
            if (entry.getKey().startsWith(gameID + "_")) {
                try {
                    entry.getValue().getRemote().sendString(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyOtherClients(String message, String gameID, Session session) {
        for (Map.Entry<String, Session> entry : registry.entrySet()) {
            if (entry.getValue() == session) { continue; }
            if (entry.getKey().startsWith(gameID + "_")) {
                try {
                    entry.getValue().getRemote().sendString(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        GameCommand command = gson.fromJson(msg, GameCommand.class);

        System.out.println("We heard from the client!\n" + msg);
        // var conn = getConnection(command.authToken, session);
        try {
            if (session != null) {
                switch (command.getCommandType()) {
                    case JOIN_PLAYER, JOIN_OBSERVER -> join(session, command);
                    case MAKE_MOVE -> move(session, command);
                    case LEAVE -> leave(session, command);
                    case RESIGN -> resign(session, command);
                }
            } else {
                session.getRemote().sendString("error_message_here");
            }
        } catch(Throwable e) {
            e.printStackTrace();
            GameMessage s = new GameMessage(ServerMessage.ServerMessageType.ERROR);
            s.setErrorMessage(e.toString());
            session.getRemote().sendString(gson.toJson(s));
        }
    }

    private void move(Session session, GameCommand cmd) throws IOException, InvalidMoveException {
        String gameID = String.valueOf(cmd.getGameID());

        GameDAO gameDAO = GameDAO.getInstance();
        Game game = gameDAO.find(cmd.getGameID());
        if (game.getGame().isGameOver()) { throw new MyServerException("Game is over", 403); }

        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());

        ChessGame.TeamColor playerColor = null;
        if (Objects.equals(userID, game.getWhiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(userID, game.getBlackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new MyServerException("Observers cannot make moves", 400);
        }

        if (game.getGame().getTeamTurn() != playerColor) {
                throw new MyServerException("Not your turn to move!", 400);
        }

        game.getGame().makeMove(cmd.getMove());

        System.out.println(gson.toJson(game.getGame()));
        // Save move in the database
        gameDAO.update(game);

        // Notify clients and send them a new game state (to redraw the board)
        GameMessage serverMessage = new GameMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        serverMessage.setGame(game);
        notifyClients(gson.toJson(serverMessage), gameID);

        // Notify players of the move
        GameMessage moveMessage = new GameMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        moveMessage.setMessage("player made move: ");
        notifyOtherClients(gson.toJson(moveMessage), gameID, session);
    }

    private void join(Session session, GameCommand cmd) throws IOException {
        System.out.println("Joining...");

        GameDAO gameDAO = GameDAO.getInstance();
        Game game = gameDAO.find(cmd.getGameID());
        if (game == null) { throw new MyServerException("No game found", 404); }

        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());

        // Allow observers
        if (cmd.getPlayerColor() != null) {
            if (cmd.getPlayerColor() == ChessGame.TeamColor.WHITE && Objects.equals(game.getBlackUsername(), userID)) {
                throw new MyServerException("Already joined as black!", 400);
            }
            if (cmd.getPlayerColor() == ChessGame.TeamColor.BLACK && Objects.equals(game.getWhiteUsername(), userID)) {
                throw new MyServerException("Already joined as white!", 400);
            }
            if (!Objects.equals(userID, game.getWhiteUsername())
                    && !Objects.equals(userID, game.getBlackUsername())
                    && !game.getObservers().contains(userID)) {
                throw new MyServerException("Must join a game", 400);
            }
        }

        String gameID = String.format("%d", cmd.getGameID());
        // Add the session to the registry with the player ID as the key
        registry.put(gameID + "_" + userID, session);

        // Send the response back to the client
        GameMessage message = new GameMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);

        session.getRemote().sendString(gson.toJson(message));

        // Notify all clients in the registry about the join except the joiner
        GameMessage notif = new GameMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notif.setMessage("Player " + userID + " has joined!");

        notifyOtherClients(gson.toJson(notif), gameID, session);
    }

    private void leave(Session session, GameCommand cmd) {
        // Returns user to post-login, but yanks from game DB
        JoinHandler handler = new JoinHandler();
        JoinRequest request = new JoinRequest();

        GameDAO gameDAO = GameDAO.getInstance();
        Game game = gameDAO.find(cmd.getGameID());
        if (game == null) { throw new MyServerException("No game found", 404); }

        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());
        String gameID = String.format("%d", cmd.getGameID());

        if (Objects.equals(userID, game.getWhiteUsername())) {
            request.setPlayerColor("WHITE");
        } else if (Objects.equals(userID, game.getBlackUsername())) {
            request.setPlayerColor("BLACK");
        } else {
            if (!registry.containsKey(gameID + "_" + userID)) {
                throw new MyServerException("Observer not in game", 403);
            }
        }

        request.setAuthToken(cmd.getAuthString());
        request.setGameID(cmd.getGameID());

        if (request.getPlayerColor() != null) {
            handler.handleLeaveRequest(request);
        }

        // Notify clients
        GameMessage message = new GameMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage("Player " + userID + " has left the game");
        notifyClients(gson.toJson(message), gameID);

        // Remove the user from the registry
        registry.remove(gameID + "_" + userID);
    }

    public void resign(Session session, GameCommand cmd) {
        // FIXME: I use a joinrequest to get the info I need. Should I be using something else?
        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());

        GameDAO gameDAO = GameDAO.getInstance();
        Game game = gameDAO.find(cmd.getGameID());

        if (!Objects.equals(userID, game.getWhiteUsername()) && !Objects.equals(userID, game.getBlackUsername())) { throw new MyServerException("Observers cannot resign", 400); }

        // Mark the game as over
        if (game.getGame().isGameOver()) { throw new MyServerException("Game has already ended", 400); }
        game.getGame().setGameOver(true);

        gameDAO.update(game);

        // Notify all clients that the user has resigned
        GameMessage notif = new GameMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notif.setMessage("Player " + userID + " has resigned!");

        notifyClients(gson.toJson(notif), String.format("%d", game.getGameID()));

        // Clean up registry
        // registry.remove(gameID + "_" + userID);
    }

    @OnWebSocketClose
    public void onWebSocketClose(Session session, int code, String reason) {
        for (Map.Entry<String, Session> entry : registry.entrySet()) {
            if (entry.getValue() == session) {
                registry.remove(entry.getKey());
                break;
            }
        }
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
