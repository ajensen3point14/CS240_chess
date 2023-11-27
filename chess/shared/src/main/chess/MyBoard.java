package chess;

import chess.pieces.*;

public class MyBoard implements ChessBoard {
    // Note that this program uses a board of dimensions 1-8 x 1-8
    private final ChessPiece[][] board;
    public MyBoard() {
        board = new ChessPiece[8][8];
    }

    @Override
    protected Object clone() {
        MyBoard newBoard = new MyBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                newBoard.board[row][col] = board[row][col];
            }
        }
        return newBoard;
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
        board[0][0] = new MyRook(ChessGame.TeamColor.WHITE);
        board[0][1] = new MyKnight(ChessGame.TeamColor.WHITE);
        board[0][2] = new MyBishop(ChessGame.TeamColor.WHITE);
        board[0][3] = new MyQueen(ChessGame.TeamColor.WHITE);
        board[0][4] = new MyKing(ChessGame.TeamColor.WHITE);
        board[0][5] = new MyBishop(ChessGame.TeamColor.WHITE);
        board[0][6] = new MyKnight(ChessGame.TeamColor.WHITE);
        board[0][7] = new MyRook(ChessGame.TeamColor.WHITE);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new MyPawn(ChessGame.TeamColor.WHITE);
        }

        // Black pieces
        board[7][0] = new MyRook(ChessGame.TeamColor.BLACK);
        board[7][1] = new MyKnight(ChessGame.TeamColor.BLACK);
        board[7][2] = new MyBishop(ChessGame.TeamColor.BLACK);
        board[7][3] = new MyQueen(ChessGame.TeamColor.BLACK);
        board[7][4] = new MyKing(ChessGame.TeamColor.BLACK);
        board[7][5] = new MyBishop(ChessGame.TeamColor.BLACK);
        board[7][6] = new MyKnight(ChessGame.TeamColor.BLACK);
        board[7][7] = new MyRook(ChessGame.TeamColor.BLACK);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new MyPawn(ChessGame.TeamColor.BLACK);
        }
    }
}
