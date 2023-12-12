package ClientWebSockets;

import chess.*;
import clientUI.PrintBoard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private Gson gson = null;
    private MyGame myGame;
    private String playerColor;

    public WSClient() throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessMove.class, new ChessMoveDeserializer());
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceDeserializer());
        gson = builder.create();

        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(this);
    }

    public MyGame getMyGame() { return myGame; }
    public String getPlayerColor() { return playerColor; }
    public void setPlayerColor(String color) { this.playerColor = color; }

    public void onMessage(String message) {
        System.out.println("We heard from the server!\n" + message);

        ServerMessage sm = gson.fromJson(message, ServerMessage.class);
        switch (sm.getServerMessageType()) {

            case LOAD_GAME:
                System.out.println("Loading game...");
                myGame = gson.fromJson(sm.getSerializedResult(), MyGame.class);
                PrintBoard chessboard_display =  new PrintBoard();
                chessboard_display.displayBoard(playerColor, getMyGame());
                break;

            case NOTIFICATION:
                System.out.println("Notification!");
                break;

            case ERROR:
                System.out.println("Error!");
                break;
            default:
                System.out.println("Unrecognized message from server:\n" + message);
                break;
        }
    }


    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}

