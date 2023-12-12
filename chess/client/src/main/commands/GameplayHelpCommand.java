package commands;

import ui.EscapeSequences;

public class GameplayHelpCommand implements CommandInterface{

    @Override
    public void execute(String[] commandParts) {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println("Available Commands:");
        System.out.println("  redraw - Redraws the chess board");
        System.out.println("  leave - Leaves the current game");
        System.out.println("  MAKE_MOVE - Input a move you want to make.");
        System.out.println("  resign - Input a move you want to make.");
        System.out.println("  HIGHLIGHT - Highlight all legal moves for the given piece.");
        System.out.println("  help - Display available gameplay actions.");
    }
}
