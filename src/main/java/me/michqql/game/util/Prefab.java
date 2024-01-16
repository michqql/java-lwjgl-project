package me.michqql.game.util;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.Transform;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.texture.Sprite;
import org.joml.Vector2f;

import java.util.UUID;

public class Prefab {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject obj = new GameObject("Generated_Sprite_Object", UUID.randomUUID(),
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer(sprite);
        obj.addComponent(renderer);

        return obj;
    }
}
