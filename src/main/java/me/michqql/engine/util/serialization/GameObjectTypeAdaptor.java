package me.michqql.engine.util.serialization;

import com.google.gson.*;
import me.michqql.engine.entity.Component;
import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.Transform;

import java.lang.reflect.Type;
import java.util.UUID;

public class GameObjectTypeAdaptor implements JsonSerializer<GameObject>, JsonDeserializer<GameObject> {

    @Override
    public JsonElement serialize(GameObject src, Type typeOfSrc, JsonSerializationContext context) {
        if(!src.isPersistent()) {
            return JsonNull.INSTANCE;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("name", src.getName());
        obj.addProperty("uuid", src.getUuid().toString());
        obj.add("transform", context.serialize(src.getTransform(), Transform.class));

        JsonArray components = new JsonArray();
        obj.add("componentList", components);
        for(Component c : src.getComponentList()) {
            components.add(context.serialize(c, Component.class));
        }

        return obj;
    }

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String name = obj.get("name").getAsString();
        UUID uuid = Serializer.gson().fromJson(obj.get("uuid"), UUID.class);
        Transform transform = context.deserialize(obj.get("transform"), Transform.class);
        JsonArray components = obj.get("componentList").getAsJsonArray();

        final GameObject go = new GameObject(name, uuid, transform);
        for(JsonElement elem : components) {
            Component c = context.deserialize(elem, Component.class);
            go.addComponent(c);
        }

        return go;
    }
}
