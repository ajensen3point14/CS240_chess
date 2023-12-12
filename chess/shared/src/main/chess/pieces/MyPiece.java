package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MyPiece implements ChessPiece {
    PieceType my_piece = null;
    ChessGame.TeamColor my_color = null;
    int numMoves = 0;

    public MyPiece(ChessGame.TeamColor color, ChessPiece.PieceType pieceType) {
        my_piece = pieceType;
        my_color = color;
    }

    @Override
    public boolean hasMoved() {
        return numMoves > 0;
    }

    public void setNumMoves(int numMoves) { this.numMoves = numMoves; }

    @Override
    public void bumpMoveCt() {
        numMoves++;
    }

    public int getMoveCount() {
        return numMoves;
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
                MyPosition newPosition = new MyPosition(newRow, newCol);

                // Check if the newPosition is valid
                if (myPosition.isValidPosition(newRow, newCol)) {
                    ChessPiece targetPiece = board.getPiece(newPosition);

                    // if the space is empty, it is a valid move
                    if (targetPiece == null) {
                        validMoves.add(new MyMove(myPosition, newPosition, null));
                    } else if (targetPiece.getTeamColor() != getTeamColor()) {
                        // if there is an enemy piece there, it is a valid move, and we will take the piece
                        validMoves.add(new MyMove(myPosition, newPosition, null));
                        break;
                    } else {
                        // if a good guy piece is there, this is an invalid move.
                        break;
                    }
                } else {
                    // If you are here, you have gone off the board
                    break;
                }
            }
        }

        return validMoves;
    }

}
