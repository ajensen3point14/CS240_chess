package chess;

import chess.pieces.*;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Objects;

public class ChessPieceDeserializer implements JsonDeserializer<ChessPiece> {

    @Override
    public ChessPiece deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String piece = jsonObject.get("my_piece").getAsString();
        String color = jsonObject.get("my_color").getAsString();
        int numMoves = jsonObject.get("numMoves").getAsInt();

        MyPiece myPiece;

        ChessGame.TeamColor pieceColor;

        if (Objects.equals(color, "WHITE")) { pieceColor = ChessGame.TeamColor.WHITE; }
        else { pieceColor = ChessGame.TeamColor.BLACK; }

        if (Objects.equals(piece, "BISHOP")) { myPiece =  new MyBishop(pieceColor); }
        else if (Objects.equals(piece, "ROOK")) { myPiece = new MyRook(pieceColor); }
        else if (Objects.equals(piece, "KNIGHT")) { myPiece =  new MyKnight(pieceColor); }
        else if (Objects.equals(piece, "KING")) { myPiece =  new MyKing(pieceColor); }
        else if (Objects.equals(piece, "QUEEN")) { myPiece =  new MyQueen(pieceColor); }
        else if (Objects.equals(piece, "PAWN")) { myPiece =  new MyPawn(pieceColor); }
        else {
            throw new JsonParseException("Unhandled chess piece type");
        }

        myPiece.setNumMoves(numMoves);

        return myPiece;
    }
}
