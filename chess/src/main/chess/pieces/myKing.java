package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class myKing extends myPiece {
    public myKing(ChessGame.TeamColor color) {
        super(color, PieceType.KING);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Define the four possible diagonal and linear directions for a king
        int[] rowDirections = { -1, 1, 0, 0, -1, -1, 1, 1 };
        int[] colDirections = { 0, 0, -1, 1, 1, -1, 1, -1 };

        return pieceMoves(board, myPosition, rowDirections, colDirections, 8, 1);
    }
}
