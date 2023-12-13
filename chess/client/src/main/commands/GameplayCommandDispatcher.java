package commands;

import chess.MyGame;
import clientUI.MyClientException;
import clientUI.PrintBoard;
import clientUI.ServerFacade;

public class GameplayCommandDispatcher {
    private String username;
    private final ServerFacade server;
    private String playerColor;


    public GameplayCommandDispatcher(ServerFacade serverFacade, String color) {
        this.username = null;
        this.server = serverFacade;
        this.playerColor = color;
    }

    public boolean dispatch(String userInput) {
        String[] commandParts = userInput.split("\\s+");
        String command = commandParts[0].toLowerCase();
        CommandInterface newCommand = null;

        try {
            switch (command) {
                case "help":
                    newCommand = new GameplayHelpCommand();
                    newCommand.execute(commandParts);
                    break;
                case "leave":
                    newCommand = new LeaveCommand(server);
                    newCommand.execute(commandParts);
                    return false;
                case "resign":
                    newCommand = new ResignCommand(server);
                    newCommand.execute(commandParts);
                    break;
                case "refresh":
                    MyGame myGame = server.getMyGame();
                    PrintBoard chessboard_display =  new PrintBoard();
                    chessboard_display.displayBoard(playerColor, myGame, null);
                    break;
                default:
                    // if it's a valid move, execute it
                    newCommand = new MoveCommand(server);
                    newCommand.execute(commandParts);
                    // Otherwise, throw an error

                    // System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        } catch (MyClientException e) {
            System.out.println(e.toString());
        }
        return true;
    }
}
