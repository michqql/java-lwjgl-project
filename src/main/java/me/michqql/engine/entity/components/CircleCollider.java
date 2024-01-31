package me.michqql.engine.entity.components;

@Conflicts(conflictingComponents = { BoxCollider.class })
public class CircleCollider extends Collider {

    private float radius = 1.0f;

    @Override
    public void update(float deltaTime) {

    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
