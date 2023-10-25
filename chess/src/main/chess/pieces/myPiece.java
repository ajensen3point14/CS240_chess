package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class myPiece implements ChessPiece {
    PieceType my_piece = null;
    ChessGame.TeamColor my_color = null;

    public myPiece(ChessGame.TeamColor color, ChessPiece.PieceType pieceType) {
        my_piece = pieceType;
        my_color = color;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return my_color;
    }

    @Override
    public PieceType getPieceType() {
        return my_piece;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, int[] rowDirections, int[] colDirections, int arraySize, int maxStep) {
        List<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = 0; i < arraySize; i++) {
            for (int step = 1; step <= maxStep; step++) {
                int newRow = row + step * rowDirections[i];
                int newCol = col + step * colDirections[i];
                myPosition newPosition = new myPosition(newRow, newCol);

                // Check if the newPosition is valid
                if (myPosition.isValidPosition(newRow, newCol)) {
                    ChessPiece targetPiece = board.getPiece(newPosition);

                    // if the space is empty, it is a valid move
                    if (targetPiece == null) {
                        validMoves.add(new myMove(myPosition, newPosition, null));
                    } else if (targetPiece.getTeamColor() != getTeamColor()) {
                        // if there is an enemy piece there, it is a valid move, and we will take the piece
                        validMoves.add(new myMove(myPosition, newPosition, null));
                        break;
                    } else {
                        // if the player's own piece is there, this is an invalid move.
                        break;
                    }
                } else {
                    // Stop in this direction if it goes out of the board
                    break;
                }
            }
        }

        return validMoves;
    }

}
