package adapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import model.piece.*;

import java.io.IOException;

public class PieceTypeAdapter extends TypeAdapter<Piece> {
    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, Piece value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", value.getName());
        jsonObject.addProperty("color", value.getColor());
        gson.toJson(jsonObject, out);
    }

    @Override
    public Piece read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        JsonObject jsonObject = gson.fromJson(in, JsonObject.class);
        String pieceName = jsonObject.get("name").getAsString().toLowerCase();
        String pieceColor = jsonObject.get("color").getAsString();

        return switch (pieceName) {
            case "rook" -> new Rook(pieceColor);
            case "knight" -> new Knight(pieceColor);
            case "bishop" -> new Bishop(pieceColor);
            case "queen" -> new Queen(pieceColor);
            case "king" -> new King(pieceColor);
            case "pawn" -> new Pawn(pieceColor);
            default -> throw new JsonParseException(STR."Unknown piece type: \{pieceName}");
        };
    }
}