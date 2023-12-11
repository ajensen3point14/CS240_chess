package ClientWebSockets;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private Gson gson;

    public WSClient() throws Exception {
        gson = new Gson();
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(this);
    }

    public void onMessage(String message) {
        System.out.println("We heard from the server!\n" + message);

        ServerMessage sm = gson.fromJson(message, ServerMessage.class);
        switch (sm.getServerMessageType()) {

            case LOAD_GAME:
                System.out.println("Loading game...");
                // responseHandler.updateBoard(readJson(message, LoadMessage.class).game);
                break;

            case NOTIFICATION:
                System.out.println("Notification!");
                // responseHandler.message(readJson(message, NotificationMessage.class).message);
                break;

            case ERROR:
                System.out.println("Error!");
                // responseHandler.error(readJson(message, ErrorMessage.class).errorMessage);
                break;
            default:
                System.out.println("Unrecognized message from server:\n" + message);
                break;
        }
    }


    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}

