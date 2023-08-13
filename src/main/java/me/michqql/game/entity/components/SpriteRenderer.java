package me.michqql.game.entity.components;

import me.michqql.game.entity.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private final Vector4f colour;

    public SpriteRenderer(Vector4f colour) {
        this.colour = colour;
    }

    @Override
    public void update(float deltaTime) {

    }

    public Vector4f getColour() {
        return colour;
    }
}
