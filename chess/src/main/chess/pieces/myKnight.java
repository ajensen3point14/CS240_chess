package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class myKnight extends myPiece {
    public myKnight(ChessGame.TeamColor color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[] rowMoves = { 2, 2, -2, -2, 1, 1, -1, -1};
        int[] colMoves = { 1, -1, 1, -1, -2, 2, -2, 2};

        return pieceMoves(board, myPosition, rowMoves, colMoves, 8, 1);
    }
}
