package me.michqql.game.gfx.texture;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class TextureAtlas {

    private final Texture texture;
    private final List<Sprite> sprites = new ArrayList<>();

    private TextureAtlas(Texture texture, int spriteWidth, int spriteHeight) {
        this.texture = texture;
        initSprites(spriteWidth, spriteHeight);
    }

    private void initSprites(int spriteWidth, int spriteHeight) {
        for(int y = 0; y < texture.getHeight(); y += spriteHeight) {
            for(int x = 0; x < texture.getWidth(); x+= spriteWidth) {
                float topY = y / (float) texture.getHeight();
                float bottomY = (y + spriteHeight) / (float) texture.getHeight();
                float leftX = x / (float) texture.getWidth();
                float rightX = (x + spriteWidth) / (float) texture.getWidth();

                Vector2f[] textureCoords = {
                        new Vector2f(rightX, topY),
                        new Vector2f(rightX, bottomY),
                        new Vector2f(leftX, bottomY),
                        new Vector2f(leftX, topY)
                };

                this.sprites.add(new Sprite(texture, textureCoords));
            }
        }
    }

    public int getNumberOfSprites() {
        return sprites.size();
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }

    public static TextureAtlas getTextureAtlas(Texture texture, int spriteWidth, int spriteHeight) {
        return new TextureAtlas(texture, spriteWidth, spriteHeight);
    }
}
