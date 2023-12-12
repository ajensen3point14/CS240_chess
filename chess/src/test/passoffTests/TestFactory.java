package passoffTests;

import chess.*;
import chess.pieces.*;

/**
 * Used for testing your code
 * Add in code using your classes for each method for each FIXME
 */
public class TestFactory {

    //Chess Functions
    //------------------------------------------------------------------------------------------------------------------
    public static ChessBoard getNewBoard(){
		return new MyBoard();
    }

    public static ChessGame getNewGame(){
		return new MyGame();
    }

    public static ChessPiece getNewPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type){
        return switch(type){
            case KING -> new MyKing(pieceColor);
            case QUEEN -> new MyQueen(pieceColor);
            case BISHOP -> new MyBishop(pieceColor);
            case KNIGHT -> new MyKnight(pieceColor);
            case ROOK -> new MyRook(pieceColor);
            case PAWN -> new MyPawn(pieceColor);
        };
    }

    public static ChessPosition getNewPosition(Integer row, Integer col){

        return new MyPosition(row, col);
    }

    public static ChessMove getNewMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){
		return new MyMove(startPosition, endPosition, promotionPiece);
    }
    //------------------------------------------------------------------------------------------------------------------


    //server.Server API's
    //------------------------------------------------------------------------------------------------------------------
    public static String getServerPort(){
        return "8080";
    }
    //------------------------------------------------------------------------------------------------------------------


    //Websocket Tests
    //------------------------------------------------------------------------------------------------------------------
    public static Long getMessageTime(){
        /*
        Changing this will change how long tests will wait for the server to send messages.
        3000 Milliseconds (3 seconds) will be enough for most computers. Feel free to change as you see fit,
        just know increasing it can make tests take longer to run.
        (On the flip side, if you've got a good computer feel free to decrease it)
         */
        return 1100L;
    }
    //------------------------------------------------------------------------------------------------------------------
}
