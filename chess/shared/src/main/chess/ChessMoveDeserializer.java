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

        // TODO: handle piece promotion
        MyMove myMove = new MyMove(new MyPosition(startRow, startCol), new MyPosition(endRow, endCol), null);

        return null;
    }
}