package me.michqql.engine.scene.editor.module.gizmo;

import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.components.NonSelectable;
import me.michqql.engine.gfx.render.PickingTexture;
import me.michqql.engine.gfx.texture.Sprite;
import me.michqql.engine.input.KeyboardInput;
import me.michqql.engine.scene.editor.EditorScene;
import me.michqql.engine.scene.editor.module.EditorModule;
import me.michqql.engine.scene.editor.module.GameViewport;
import me.michqql.engine.scene.editor.module.Inspector;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

public class GizmoModule implements EditorModule {

    private final Inspector inspector;

    private final TranslateGizmo translateGizmo;
    private final ScaleGizmo scaleGizmo;

    private GameObject selectedObject;

    public GizmoModule(EditorScene editorScene, PickingTexture pickingTexture, GameViewport gameViewport,
                       Inspector inspector, Sprite translateSprite, Sprite scaleSprite) {
        this.inspector = inspector;

        this.translateGizmo = new TranslateGizmo(editorScene, pickingTexture, gameViewport,
                translateSprite);
        this.scaleGizmo = new ScaleGizmo(editorScene, pickingTexture, gameViewport,
                scaleSprite);
    }

    @Override
    public void update(float dt) {
        setSelectedObject(inspector.getSelectedObject());

        KeyboardInput.setKeyCaptureRequested(true);
        if(KeyboardInput.isKeyPressed(GLFW_KEY_LEFT_ALT)) {
            translateGizmo.setSelectedObject(null);
            scaleGizmo.setSelectedObject(selectedObject);
        } else {
            translateGizmo.setSelectedObject(selectedObject);
            scaleGizmo.setSelectedObject(null);
        }

        translateGizmo.update(dt);
        scaleGizmo.update(dt);
    }

    @Override
    public void display() {

    }

    public void setSelectedObject(GameObject selectedObject) {
        if(selectedObject != null && selectedObject.getComponent(NonSelectable.class) != null) {
            return;
        }

        this.selectedObject = selectedObject;
    }
}
