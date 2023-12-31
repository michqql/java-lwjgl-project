package me.michqql.game.gfx.texture;

import org.joml.Vector2f;

public class Sprite {

    private final Texture texture;
    private final Vector2f[] textureCoords;

    public Sprite(Texture texture) {
        this.texture = texture;
        this.textureCoords = new Vector2f[] {
                new Vector2f(1f, 1f),
                new Vector2f(1f, 0f),
                new Vector2f(0f, 0f),
                new Vector2f(0f, 1f)
        };
    }

    public Sprite(Texture texture, Vector2f[] textureCoords) {
        this.texture = texture;
        this.textureCoords = textureCoords;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }
}
