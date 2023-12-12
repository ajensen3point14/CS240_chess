package chess;

import chess.pieces.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

public class ChessMoveDeserializer implements JsonDeserializer<ChessMove> {

    @Override
    public ChessMove deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Move":{"startPos":{"row":2,"col":3},"endPos":{"row":4,"col":3}}}

        JsonObject startPos = jsonObject.get("startPos").getAsJsonObject();
        int startRow = startPos.get("row").getAsInt();
        int startCol = startPos.get("col").getAsInt();
        JsonObject endPos = jsonObject.get("endPos").getAsJsonObject();
        int endRow = endPos.get("row").getAsInt();
        int endCol = endPos.get("col").getAsInt();

        String promotion = null;
        if (jsonObject.get("promotion") != null) {
             promotion = jsonObject.get("promotion").getAsString();
        }

        ChessPiece.PieceType promotionPiece = null;
        if ("QUEEN".equals(promotion)) {
            promotionPiece = ChessPiece.PieceType.QUEEN;
        } else if ("ROOK".equals(promotion)) {
            promotionPiece = ChessPiece.PieceType.ROOK;
        } else if ("KNIGHT".equals(promotion)) {
            promotionPiece = ChessPiece.PieceType.KNIGHT;
        } else if ("BISHOP".equals(promotion)) {
            promotionPiece = ChessPiece.PieceType.BISHOP;
        }

        MyMove myMove = new MyMove(new MyPosition(startRow, startCol), new MyPosition(endRow, endCol), promotionPiece);

        return myMove;
    }
}