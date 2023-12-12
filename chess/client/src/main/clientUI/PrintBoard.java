package clientUI;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.MyGame;
import chess.MyPosition;
import chess.pieces.MyPiece;
import ui.EscapeSequences;

import java.util.Objects;

public class PrintBoard {
    private static final String SET_BG_COLOR_LIGHT_GREY = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String SET_BG_COLOR_WHITE = EscapeSequences.SET_BG_COLOR_WHITE;
    private static final String SET_BG_COLOR_BLACK = EscapeSequences.SET_BG_COLOR_BLACK;
    private static final String SET_BG_COLOR_DARK_GREY = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String RESET_BG_COLOR = EscapeSequences.RESET_BG_COLOR;
    private static final String SET_TEXT_COLOR_BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String SET_TEXT_COLOR_RED = EscapeSequences.SET_TEXT_COLOR_RED;
    private static final String SET_TEXT_COLOR_BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static final String RESET_TEXT_COLOR = EscapeSequences.RESET_TEXT_COLOR;
    private static final String EMPTY = EscapeSequences.EMPTY;

    // public PrintBoard(String gameState) { initializeBoardFromString(gameState); }
    public PrintBoard() {}


    public void displayBoard(String orientation, MyGame myGame) {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.println("It is " + myGame.getTeamTurn() + "'s turn");

        // Draw the top border
        System.out.print(SET_BG_COLOR_LIGHT_GREY);

        if (orientation.equalsIgnoreCase("black")) {
            System.out.print("    h  g  f  e  d  c  b  a    ");
        } else {
            System.out.print("    a  b  c  d  e  f  g  h    ");
        }

        System.out.print(RESET_BG_COLOR);
        System.out.println();

        // Draw the middle rows
        for (int row = 7; row >= 0; row--) {
            // Draw the numbered border in grey
            System.out.print(SET_BG_COLOR_LIGHT_GREY);

            // Adjust the row numbers based on orientation
            if (orientation.equalsIgnoreCase("black")) {
                System.out.print(" " + (8 - row) + " ");
            } else {
                System.out.print(" " + (row + 1) + " ");
            }

            for (int col = 0; col < 8; col++) {
                // Draw alternating black and white squares
                MyPosition pos = new MyPosition(row + 1, col + 1);
                if (orientation.equalsIgnoreCase("white")) {
                    drawSquare(getBgColor(row, col), getTextColor(row, col, orientation), myGame.getBoard().getPiece(pos));
                } else {
                    int reverseCol = 7 - col;
                    int reverseRow = 7 - row;
                    MyPosition blackPos = new MyPosition(reverseRow + 1, reverseCol + 1);
                    drawSquare(getBgColor(row, col), getTextColor(row, reverseCol, orientation), myGame.getBoard().getPiece(blackPos));
                }
            }

            // Draw the numbered border in grey
            System.out.print(SET_BG_COLOR_LIGHT_GREY);

            // Adjust the row numbers based on orientation
            if (orientation.equalsIgnoreCase("black")) {
                System.out.print(" " + (8 - row) + " ");
            } else {
                System.out.print(" " + (row + 1) + " ");
            }

            System.out.print(RESET_BG_COLOR);
            System.out.println();
        }

        // Draw the bottom border
        System.out.print(SET_BG_COLOR_LIGHT_GREY);

        if (orientation.equalsIgnoreCase("black")) {
            System.out.print("    h  g  f  e  d  c  b  a    ");
        } else {
            System.out.print("    a  b  c  d  e  f  g  h    ");
        }

        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.println();
    }

    private String getRowNum(int rowNum, String orientation) {
        // Get the row number with the correct orientation
        if (orientation.equalsIgnoreCase("white")) {
            return " " + rowNum + " ";
        } else {
            return " " + (8 - rowNum + 1) + " ";
        }
    }

    private String getTextColor(int row, int col, String orientation) {
        // Determine the text color based on the piece and orientation
        return "";
    }

    private String getBgColor(int row, int col) {
        // Determine the background color based on the position
        return ((row + col) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
    }

    private void drawSquare(String bgColor, String textColor, ChessPiece content) {
        // Draw a square with the specified colors and content
        String pieceSymbol = getPieceSymbol(content);
        System.out.print(bgColor + pieceSymbol + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) { return "   "; }
        String color = piece.getTeamColor().toString();
        if (color.equalsIgnoreCase("white")) {
            color = SET_TEXT_COLOR_BLUE;
        } else {
            color = SET_TEXT_COLOR_RED;
        }
        switch (piece.getPieceType()) {
            case PAWN: return color + " P ";
            case KNIGHT: return color + " N ";
            case BISHOP: return color + " B ";
            case KING: return color + " K ";
            case QUEEN: return color + " Q ";
            case ROOK: return color + " R ";
            default: return "   ";  // Empty square
        }
    }
}
