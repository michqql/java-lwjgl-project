package me.michqql.game.entity.components;

import me.michqql.game.entity.Component;
import me.michqql.game.gfx.texture.Texture;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private final Vector4f colour;
    private Texture texture;

    public SpriteRenderer(Vector4f colour) {
        this.colour = colour;
    }

    public SpriteRenderer(Texture texture) {
        this.colour = new Vector4f(1f, 1f, 1f, 1f);
        this.texture = texture;
    }

    @Override
    public void update(float deltaTime) {

    }

    public Vector4f getColour() {
        return colour;
    }

    public Texture getTexture() {
        return texture;
    }
}
