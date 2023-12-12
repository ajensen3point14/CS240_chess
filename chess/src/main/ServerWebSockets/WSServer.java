package ServerWebSockets;

import WSShared.GameCommand;
import chess.InvalidMoveException;
import chess.MyMove;
import chess.MyPosition;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import models.Game;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.JoinRequest;
import requests.MoveRequest;
import server.DAO.GameDAO;
import server.MyServerException;
import server.ServerResponse;
import server.handlers.JoinHandler;
import server.services.LoginService;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebSocket
public class WSServer {
    private Gson gson;

    // Registry of "gameID_username" to sessions
    private HashMap<String, Session> registry = new HashMap<>();

    public WSServer() {
        gson = new Gson();
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

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        GameCommand command = gson.fromJson(msg, WSShared.GameCommand.class);

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
            ServerMessage s = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            s.setSerializedResult(e.toString());
            session.getRemote().sendString(gson.toJson(s));
        }
    }

    private void move(Session session, GameCommand cmd) throws IOException, InvalidMoveException {
        MoveRequest request = gson.fromJson(cmd.getSerializedRequest(), MoveRequest.class);
        request.setAuthToken(cmd.getAuthString());
        String gameID = String.valueOf(request.getGameID());

        LoginService login = new LoginService();

        String move = request.getMove();

        // Update the game in the database
        // TODO: handle piece promotion
        int startCol = move.charAt(0) - 'a' + 1;
        int startRow = move.charAt(1) - '0';
        int endCol = move.charAt(2) - 'a' + 1;
        int endRow = move.charAt(3) - '0';

        GameDAO gameDAO = GameDAO.getInstance();
        Game game = gameDAO.find(request.getGameID());
        MyMove myMove = new MyMove(new MyPosition(startRow, startCol), new MyPosition(endRow, endCol), null);
        game.getGame().makeMove(myMove);

        System.out.println(gson.toJson(game.getGame()));
        // Save move in the database
        gameDAO.update(game);

        // Notify clients and send them a new game state (to redraw the board)
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        serverMessage.setSerializedResult(gson.toJson(game.getGame()));

        notifyClients(gson.toJson(serverMessage), gameID);
        notifyClients(move, gameID);
    }

    private void join(Session session, GameCommand cmd) throws IOException {
        System.out.println("Joining...");
        // New up appropriate handler and make the respective call
        JoinRequest request = gson.fromJson(cmd.getSerializedRequest(), JoinRequest.class);
        request.setAuthToken(cmd.getAuthString());
        JoinHandler joinHandler = new JoinHandler();

        ServerResponse serverResponse = joinHandler.handleRequest(request);

        String gameID = String.format("%d", request.getGameID());
        // Add the session to the registry with the player ID as the key
        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());
        registry.put(gameID + "_" + userID, session);

        // Send the response back to the client
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);

        serverMessage.setSerializedResult(serverResponse.getBody());
        session.getRemote().sendString(gson.toJson(serverMessage));

        // Notify all clients in the registry about the join
        notifyClients("Player " + userID + " has joined!", gameID);
    }

    private void leave(Session session, GameCommand cmd) {
        // Returns user to post-login, but yanks from game DB
        JoinHandler handler = new JoinHandler();
        JoinRequest request = gson.fromJson(cmd.getSerializedRequest(), JoinRequest.class);
        request.setAuthToken(cmd.getAuthString());

        handler.handleLeaveRequest(request);

        String gameID = String.format("%d", request.getGameID());
        // Add the session to the registry with the player ID as the key
        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());
        registry.remove(gameID + "_" + userID);

        notifyClients("Player " + userID + " has left the game", gameID);
    }

    public void resign(Session session, GameCommand cmd) {
        // Send notif to all clients involved
        // FIXME: I use a joinrequest to get the info I need. Should I be using something else?
        JoinRequest request = gson.fromJson(cmd.getSerializedRequest(), JoinRequest.class);
        request.setAuthToken(cmd.getAuthString());
        String gameID = String.format("%d", request.getGameID());

        LoginService login = new LoginService();
        String userID = login.resolveAuthTokenToID(cmd.getAuthString());

        notifyClients("Player " + userID + " has resigned", gameID);

        // Remove the game from the database
        GameDAO.getInstance().removeGame(request.getGameID());

        // Clean up registry
        registry.remove(gameID + "_" + userID);
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
