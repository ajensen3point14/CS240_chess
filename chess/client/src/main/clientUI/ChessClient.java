package clientUI;

import ClientWebSockets.WSClient;
import chess.ChessPiece;
import chess.ChessPieceDeserializer;
import chess.MyGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commands.*;
import ui.EscapeSequences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class ChessClient {
    public static void main(String[] args) throws Exception {
        var ws = new WSClient();
        ServerFacade serverFacade = new ServerFacade(ws);
        CommandDispatcher commandDispatcher = new CommandDispatcher(serverFacade);

        // Connect to the server
        System.out.println("Connected to server");

        displayWelcomeMessage();
        String userInput = null;

        /*
        TODO:
        Server:
        - Server get game state from DB, send to client (make_move)
        - Client makes move, sends to server. Server validates move (make_move)
        - Server applies move update state in DB and resend to clients (make_move)

        Client:
        - Draw game state on client (local)
        - Highlight legal moves (local)

        - Pass test cases
         */

        do {
            System.out.print(">>>> ");
            userInput = ConsoleInputReader.readInput();

        } while (commandDispatcher.dispatch(userInput));
    }

    public static void get(String msg) throws IOException {
        var url = new URL("http://localhost:8080/echo/" + msg);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.connect();
        try (InputStream respBody = conn.getInputStream()) {
            byte[] bytes = new byte[respBody.available()];
            respBody.read(bytes);
            System.out.println(new String(bytes));
        }
    }


    private static void displayWelcomeMessage() {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println("Welcome to 240 Chess!");
        System.out.println("Type 'help' for a list of commands.");
    }
}
