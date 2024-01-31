package me.michqql.engine.entity.components;

import org.joml.Vector2f;

@Conflicts(conflictingComponents = { CircleCollider.class })
public class BoxCollider extends Collider {

    private final Vector2f size = new Vector2f(1, 1);

    @Override
    public void update(float deltaTime) {

    }

    public Vector2f getSize() {
        return size;
    }
}
