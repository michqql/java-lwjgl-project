package me.michqql.engine.util;

import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.Transform;
import me.michqql.engine.entity.components.SpriteRenderer;
import me.michqql.engine.gfx.texture.Sprite;
import org.joml.Vector2f;

import java.util.UUID;

public class Prefab {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject obj = new GameObject("Generated_Sprite_Object", UUID.randomUUID(),
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)));
        SpriteRenderer renderer = new SpriteRenderer(sprite);
        obj.addComponent(renderer);

        return obj;
    }

    public static GameObject generateNonPersistentSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject obj = new GameObject("Generated_Sprite_Object", UUID.randomUUID(),
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)));
        obj.setPersistent(false);
        SpriteRenderer renderer = new SpriteRenderer(sprite);
        obj.addComponent(renderer);

        return obj;
    }
}
