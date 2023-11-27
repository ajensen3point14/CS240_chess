package clientUI;

import commands.*;
import ui.EscapeSequences;

public class ChessClient {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade();
        CommandDispatcher commandDispatcher = new CommandDispatcher(serverFacade);

        displayWelcomeMessage();
        String userInput = null;

        do {
            System.out.print(">>>> ");
            userInput = ConsoleInputReader.readInput();

        } while (commandDispatcher.dispatch(userInput));
    }

    private static void displayWelcomeMessage() {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println("Welcome to 240 Chess!");
        System.out.println("Type 'help' for a list of commands.");
    }
}
