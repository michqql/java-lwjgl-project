package me.michqql.engine.scene.editor.module;

import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.components.BoxCollider;
import me.michqql.engine.entity.components.CircleCollider;
import me.michqql.engine.gfx.render.debug.DebugDraw;
import me.michqql.engine.scene.editor.EditorScene;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class VisualPhysicsColliders implements EditorModule {

    private static final Vector3f OUTLINE_COLOUR = new Vector3f(0.1f, 0.9f, 0.1f);

    private final EditorScene editorScene;

    private final Vector2f center = new Vector2f();

    public VisualPhysicsColliders(EditorScene editorScene) {
        this.editorScene = editorScene;
    }

    @Override
    public void update(float dt) {
        editorScene.forEachGameObject(gameObject -> {
            BoxCollider boxCollider = gameObject.getComponent(BoxCollider.class);
            if(boxCollider != null) {
                drawBoxCollider(gameObject, boxCollider);
                return;
            }

            CircleCollider circleCollider = gameObject.getComponent(CircleCollider.class);
            if(circleCollider != null) {
                drawCircleCollider(gameObject, circleCollider);
            }
        });
    }

    private void drawBoxCollider(GameObject gameObject, BoxCollider collider) {
        center.set(gameObject.getTransform().getPosition()).add(collider.getOffset());
        DebugDraw.addBox2D(center, collider.getSize(), gameObject.getTransform().getRotation(),
                OUTLINE_COLOUR, false, 20);
    }

    private void drawCircleCollider(GameObject gameObject, CircleCollider collider) {

    }

    @Override
    public void display() {

    }
}
