package clientUI;

import ClientWebSockets.WSClient;
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



        // TESTCODE: Communicate with server
        // System.out.print(">>>> ");
        // Scanner scanner = new Scanner(System.in);

        // while (true) ws.send(scanner.nextLine());


        displayWelcomeMessage();
        String userInput = null;

        do {
            System.out.print(">>>> ");
            userInput = ConsoleInputReader.readInput();


            // get(userInput);

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
