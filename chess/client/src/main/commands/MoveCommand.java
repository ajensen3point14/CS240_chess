package commands;

import WSShared.GameCommand;
import chess.ChessMove;
import chess.MyGame;
import chess.MyPosition;
import clientUI.PrintBoard;
import clientUI.ServerFacade;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.Collection;

public class MoveCommand implements CommandInterface {
    private ServerFacade server;

    public MoveCommand(ServerFacade server) { this.server = server; }

    @Override
    public void execute(String[] commandParts) {
        // if it's a highlight command, send it to the highlight function
        if (commandParts[0].length() == 2) {
            executeHighlight(commandParts);
            return;
        }
        else if (commandParts[0].length() != 4 || commandParts.length != 1) {
            System.out.println("Move example: e4e5");
            System.out.println("Highlight example: e4");
            return;
        }

        server.move(commandParts[0]);
    }


    public void executeHighlight(String[] commandParts) {
        int startCol = commandParts[0].charAt(0) - 'a' + 1;
        int startRow = commandParts[0].charAt(1) - '1' + 1;

        MyGame myGame = server.getMyGame();
        Collection<ChessMove> moves = myGame.validMoves(new MyPosition(startRow, startCol));

        PrintBoard highlightBoard = new PrintBoard();
        highlightBoard.displayBoard(server.getWSClientPlayerColor(), myGame, moves);
    }
}
