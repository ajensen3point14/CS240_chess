package commands;

import ui.EscapeSequences;

public class HelpCommand implements CommandInterface {
    private boolean loggedIn = false;
    public HelpCommand(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public void execute(String[] commandParts) {
        // Display help information
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println("Available Commands:");
        if (!loggedIn) {
            System.out.println("Note: You are currently logged OUT.");
            System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - Register a new account and log in");
            System.out.println("  login <USERNAME> <PASSWORD> - Log in to the Chess server to play");
        }
        if (loggedIn) {
            System.out.println("Note: You are currently logged IN.");
            System.out.println("  logout - Log out of the server");
            System.out.println("  create <GAME_NAME> - Create a new chess game");
            System.out.println("  list - List current chess games to join");
            System.out.println("  join <ID> [WHITE | BLACK ] - Join a specific game as a player, or leave empty to observe");
            System.out.println("  observe <ID> - Watch a chess game");
        }
        System.out.println("  quit - Exit the clientUI.ChessClient");
        System.out.println("  help - Display available user actions");
    }
}
