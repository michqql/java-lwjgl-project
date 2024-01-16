package me.michqql.game.util.serialization;

import com.google.gson.*;
import me.michqql.game.entity.Component;
import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.Transform;

import java.lang.reflect.Type;
import java.util.UUID;

public class GameObjectTypeAdaptor implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String name = obj.get("name").getAsString();
        UUID uuid = Serializer.gson().fromJson(obj.get("uuid"), UUID.class);
        Transform transform = context.deserialize(obj.get("transform"), Transform.class);
        int zIndex = context.deserialize(obj.get("zIndex"), int.class);
        JsonArray components = obj.get("componentList").getAsJsonArray();

        final GameObject go = new GameObject(name, uuid, transform, zIndex);
        for(JsonElement elem : components) {
            Component c = context.deserialize(elem, Component.class);
            go.addComponent(c);
        }

        return go;
    }
}
