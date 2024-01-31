package me.michqql.engine.util.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.michqql.engine.entity.Component;
import me.michqql.engine.entity.GameObject;
import me.michqql.engine.gfx.texture.Texture;

public class Serializer {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectTypeAdaptor())
                .registerTypeAdapter(Component.class, new EntityComponentTypeAdaptor())
                .registerTypeAdapter(Texture.class, new TextureTypeAdaptor())
                .serializeNulls()
                .create();
    }

    public static Gson gson() {
        return GSON;
    }
}
