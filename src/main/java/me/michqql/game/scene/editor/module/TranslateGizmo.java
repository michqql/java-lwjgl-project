package me.michqql.game.scene.editor.module;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.util.Prefab;
import org.joml.Vector4f;

public class TranslateGizmo implements EditorModule {

    private static final Vector4f TRANSPARENT = new Vector4f();
    private static final Vector4f X_AXIS_COLOUR = new Vector4f(1, 0, 0, 1);
    private static final Vector4f X_AXIS_COLOUR_HOVER = new Vector4f(1, 0, 0, 1);
    private static final Vector4f Y_AXIS_COLOUR = new Vector4f(0, 0, 1, 1);
    private static final Vector4f Y_AXIS_COLOUR_HOVER = new Vector4f(0, 0, 1, 1);

    private final EditorScene editorScene;
    private final Inspector inspector;

    private final GameObject xAxisGizmo;
    private final SpriteRenderer xAxisSprite;
    private final GameObject yAxisGizmo;
    private final SpriteRenderer yAxisSprite;

    private GameObject selectedObject;

    public TranslateGizmo(EditorScene editorScene, Inspector inspector, Sprite arrowSprite) {
        this.editorScene = editorScene;
        this.inspector = inspector;

        this.xAxisGizmo = Prefab.generateSpriteObject(arrowSprite, 16, 48);
        this.xAxisSprite = xAxisGizmo.getComponent(SpriteRenderer.class);
        this.yAxisGizmo = Prefab.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisSprite = yAxisGizmo.getComponent(SpriteRenderer.class);

        editorScene.addGameObject(xAxisGizmo);
        editorScene.addGameObject(yAxisGizmo);
    }

    @Override
    public void update(float dt) {
        setSelectedObject(inspector.getSelectedObject());
        if(selectedObject != null) {
            xAxisGizmo.getTransform().getPosition().set(selectedObject.getTransform().getPosition());
            yAxisGizmo.getTransform().getPosition().set(selectedObject.getTransform().getPosition());
        }
    }

    @Override
    public void display() {

    }

    public GameObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(GameObject selectedObject) {
        this.selectedObject = selectedObject;
        if(selectedObject != null) {
            xAxisSprite.setColour(X_AXIS_COLOUR);
            yAxisSprite.setColour(Y_AXIS_COLOUR);
        } else {
            xAxisSprite.setColour(TRANSPARENT);
            yAxisSprite.setColour(TRANSPARENT);
        }
    }
}
