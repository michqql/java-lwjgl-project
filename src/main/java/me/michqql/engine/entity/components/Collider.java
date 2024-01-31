package me.michqql.engine.entity.components;

import me.michqql.engine.entity.Component;
import org.joml.Vector2f;

abstract class Collider extends Component {

    private Vector2f offset = new Vector2f();
    private Vector2f origin = new Vector2f();

    public Vector2f getOffset() {
        return offset;
    }

    public Vector2f getOrigin() {
        return origin;
    }
}
