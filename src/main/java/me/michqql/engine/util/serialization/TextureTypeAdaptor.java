package me.michqql.engine.util.serialization;

import com.google.gson.*;
import me.michqql.engine.gfx.texture.Texture;

import java.lang.reflect.Type;

public class TextureTypeAdaptor implements JsonSerializer<Texture>, JsonDeserializer<Texture> {

    private static final String TEXTURE_PATH = "texture_path";

    @Override
    public Texture deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String fileName = obj.get(TEXTURE_PATH).getAsString();
        return Texture.REGISTRY.get(fileName);
    }

    @Override
    public JsonElement serialize(Texture src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty(TEXTURE_PATH, src.getFileName());
        return obj;
    }
}
