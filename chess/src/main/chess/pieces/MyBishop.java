package chess.pieces;

import chess.*;

import java.util.Collection;

public class MyBishop extends MyPiece {
    public MyBishop(ChessGame.TeamColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Define the four possible diagonal directions for a bishop
        int[] rowDirections = { -1, -1, 1, 1 };
        int[] colDirections = { -1, 1, -1, 1 };

        return pieceMoves(board, myPosition, rowDirections, colDirections, 4, 7);
    }
}
