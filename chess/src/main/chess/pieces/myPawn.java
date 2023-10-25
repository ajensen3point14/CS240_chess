package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class myPawn extends myPiece {
    public myPawn(ChessGame.TeamColor color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        int rowDirection = 1;
        if (my_color == ChessGame.TeamColor.BLACK) {
            rowDirection = -1; // Changes pawn direction since black is on top and moves down
        }
        int[] rowMoves = {rowDirection};
        int [] colMoves = {0};
        int step = 1;
        boolean promotion = false;

        if (my_color == ChessGame.TeamColor.WHITE) {
            if (myPosition.getRow() == 2) { step = 2; }
            if (myPosition.getRow() == 7) { promotion = true; }
        }else {
            if (myPosition.getRow() == 7) { step = 2; }
            if (myPosition.getRow() == 2) { promotion = true; }
        }

        for (int ii = 1; ii <= step; ii++) {
            int newRow = myPosition.getRow() + ii * rowDirection;
            chess.myPosition newPosition = new myPosition(newRow, myPosition.getColumn());

            // Check if the newPosition is valid
            if (myPosition.isValidPosition(newRow, myPosition.getColumn())) {
                ChessPiece targetPiece = board.getPiece(newPosition);

                // if the space is empty, it is a valid move
                if (targetPiece == null) {
                    if (promotion) {
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.QUEEN));
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.KNIGHT));
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.BISHOP));
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.ROOK));
                    } else {
                        validMoves.add(new myMove(myPosition, newPosition, null));
                    }
                } else {
                    // if any piece is directly in front, this is an invalid move.
                    break;
                }
            } else {
                // Stop in this direction if it goes out of the board
                break;
            }
        }

        // Looking at captures
        for (int ii = 1; ii <= 2; ii++) {
            int newRow = myPosition.getRow() + rowDirection;
            int newCol = myPosition.getColumn() + (ii == 1 ? -1 : 1);
            chess.myPosition newPosition = new myPosition(newRow, newCol);

            // Check if the newPosition is valid
            if (myPosition.isValidPosition(newRow, newCol)) {
                ChessPiece targetPiece = board.getPiece(newPosition);

                // if the space occupied by an enemy, it is a valid move. Promote if necessary
                if (targetPiece != null && targetPiece.getTeamColor() != my_color) {
                    if (promotion) {
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.QUEEN));
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.KNIGHT));
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.BISHOP));
                        validMoves.add(new myMove(myPosition, newPosition, PieceType.ROOK));
                    } else {
                        validMoves.add(new myMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        return validMoves;
    }
}
