package chess;

import chess.*;

import java.util.Collection;

public class myGame implements ChessGame {
    private myBoard board;
    private TeamColor teamTurn;
    public myGame() {
        this.board = new myBoard();
        this.teamTurn = TeamColor.WHITE; // White starts the game
        board.resetBoard();
    }

    @Override
    public TeamColor getTeamTurn() {
        return null;
    }

    @Override
    public void setTeamTurn(TeamColor team) {

    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return null;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    @Override
    public void setBoard(ChessBoard board) {
        // this.board = board;

    }

    @Override
    public ChessBoard getBoard() {
        return null;
    }
}
