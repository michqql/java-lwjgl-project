package me.michqql.engine.physics2d;

import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.Transform;
import me.michqql.engine.entity.components.BoxCollider;
import me.michqql.engine.entity.components.CircleCollider;
import me.michqql.engine.entity.components.Rigidbody2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

public class Physics2d {
    public static final Vec2 GRAVITY = new Vec2(0, -10.0f);

    private final World world = new World(GRAVITY);

    private float physicsTime;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public void update(float deltaTime) {
        physicsTime += deltaTime;
        if(physicsTime >= 0) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    /**
     * Adds the GameObject to the physics world
     * @param gameObject to add
     */
    public void addGameObject(GameObject gameObject) {
        Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
        if(rb == null)
            return;

        if(rb.getRawBody() == null) {
            // This rigid body has not been added to the physics engine yet, add it
            Transform transform = gameObject.getTransform();

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.getRotation());
            bodyDef.position.set(transform.getPosition().x(), transform.getPosition().y());
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();

            switch (rb.getBodyType()) {
                case KINEMATIC -> bodyDef.type = BodyType.KINEMATIC;
                case STATIC -> bodyDef.type = BodyType.STATIC;
                case DYNAMIC -> bodyDef.type = BodyType.DYNAMIC;
            }

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            BoxCollider boxCollider;
            if((circleCollider = gameObject.getComponent(CircleCollider.class)) != null) {
                shape.setRadius(circleCollider.getRadius());
            } else if((boxCollider = gameObject.getComponent(BoxCollider.class)) != null) {
                Vector2f halfSize = new Vector2f(boxCollider.getSize()).mul(0.5f); // TODO: might need to be 0.25
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = boxCollider.getOrigin();
                shape.setAsBox(halfSize.x(), halfSize.y(), new Vec2(origin.x(), origin.y()), 0f);

                bodyDef.position.addLocal(offset.x(), offset.y());
            }

            Body body = world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }

    public void removeGameObject(GameObject gameObject) {
        Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
        if(rb == null || rb.getRawBody() == null)
            return;

        world.destroyBody(rb.getRawBody());
        rb.setRawBody(null);
    }
}
