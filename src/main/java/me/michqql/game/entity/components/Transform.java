package me.michqql.game.entity.components;

import org.joml.Vector2f;

import java.util.Objects;

public class Transform {

    private final Vector2f position;
    private final Vector2f scale;

    public Transform() {
        this(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        this(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

    public void copy(Transform to) {
        to.getPosition().set(position);
        to.getScale().set(scale);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transform transform = (Transform) o;
        return Objects.equals(position, transform.position) &&
                Objects.equals(scale, transform.scale);
    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (scale != null ? scale.hashCode() : 0);
        return result;
    }
}
