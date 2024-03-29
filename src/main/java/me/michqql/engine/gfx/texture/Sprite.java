package me.michqql.engine.gfx.texture;

import me.michqql.engine.util.collection.ReadOnlyList;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Sprite {

    private static final List<Sprite> SPRITE_CACHE = new ArrayList<>();
    private static final ReadOnlyList<Sprite> READ_ONLY_CACHE = new ReadOnlyList<>(SPRITE_CACHE);
    public static List<Sprite> getSpriteCache() {
        return READ_ONLY_CACHE; // Controls access to read only
    }

    private final Texture texture;
    private final Vector2f[] textureCoords;
    private final float width, height;

    public Sprite(Texture texture) {
        this.texture = texture;
        this.textureCoords = new Vector2f[] {
                new Vector2f(1f, 1f),
                new Vector2f(1f, 0f),
                new Vector2f(0f, 0f),
                new Vector2f(0f, 1f)
        };
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public Sprite(Texture texture, Vector2f[] textureCoords, float spriteWidth, float spriteHeight) {
        this.texture = texture;
        this.textureCoords = textureCoords;
        this.width = spriteWidth;
        this.height = spriteHeight;

        SPRITE_CACHE.add(this);
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
