package chess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.pieces.*;

public class myBoard implements ChessBoard {
    // Note that this program uses a board of dimensions 1-8 x 1-8
    private final ChessPiece[][] board;
    public myBoard() {
        board = new ChessPiece[8][8];
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    @Override
    public void resetBoard() {
        // Clear the board by setting all elements to null
        // row
        for (int ii = 0; ii < 8; ii++) {
            // column
            for (int jj = 0; jj < 8; jj++) {
                board[ii][jj] = null;
            }
        }

        // Set up the standard starting positions
        // White pieces
        board[0][0] = new myRook(ChessGame.TeamColor.WHITE);
        board[0][1] = new myKnight(ChessGame.TeamColor.WHITE);
        board[0][2] = new myBishop(ChessGame.TeamColor.WHITE);
        board[0][3] = new myQueen(ChessGame.TeamColor.WHITE);
        board[0][4] = new myKing(ChessGame.TeamColor.WHITE);
        board[0][5] = new myBishop(ChessGame.TeamColor.WHITE);
        board[0][6] = new myKnight(ChessGame.TeamColor.WHITE);
        board[0][7] = new myRook(ChessGame.TeamColor.WHITE);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new myPawn(ChessGame.TeamColor.WHITE);
        }

        // Black pieces
        board[7][0] = new myRook(ChessGame.TeamColor.BLACK);
        board[7][1] = new myKnight(ChessGame.TeamColor.BLACK);
        board[7][2] = new myBishop(ChessGame.TeamColor.BLACK);
        board[7][3] = new myQueen(ChessGame.TeamColor.BLACK);
        board[7][4] = new myKing(ChessGame.TeamColor.BLACK);
        board[7][5] = new myBishop(ChessGame.TeamColor.BLACK);
        board[7][6] = new myKnight(ChessGame.TeamColor.BLACK);
        board[7][7] = new myRook(ChessGame.TeamColor.BLACK);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new myPawn(ChessGame.TeamColor.BLACK);
        }
    }
}
