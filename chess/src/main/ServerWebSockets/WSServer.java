package ServerWebSockets;

import WSShared.GameCommand;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.JoinRequest;
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
//                case MAKE_MOVE -> move(conn, msg);
                case LEAVE -> leave(session, command);
//                case RESIGN -> resign(conn, msg);
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

        // Notify all clients in the registry about the join
        notifyClients("Player " + userID + " has joined!", gameID);

        // Send the response back to the client
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);

        serverMessage.setSerializedResult(serverResponse.getBody());
        session.getRemote().sendString(gson.toJson(serverMessage));
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
