package me.michqql.engine.util.serialization;

import com.google.gson.*;
import me.michqql.engine.entity.Component;

import java.lang.reflect.Type;

public class EntityComponentTypeAdaptor implements JsonSerializer<Component>, JsonDeserializer<Component> {

    private static final String CLASS_META_KEY = "CLASS_META_KEY";

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement elem = context.serialize(src, src.getClass());
        elem.getAsJsonObject().addProperty(CLASS_META_KEY, src.getClass().getCanonicalName());

        return elem;
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String className = obj.get(CLASS_META_KEY).getAsString();
        try {
            Class<?> type = Class.forName(className);
            return context.deserialize(json, type);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
