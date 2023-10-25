package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class myRook extends myPiece {
    public myRook(ChessGame.TeamColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Define the four possible horizontal and vertical directions for a rook
        int[] rowDirections = { -1, 1, 0, 0 };
        int[] colDirections = { 0, 0, -1, 1 };

        return pieceMoves(board, myPosition, rowDirections, colDirections, 4, 7);
    }
}
