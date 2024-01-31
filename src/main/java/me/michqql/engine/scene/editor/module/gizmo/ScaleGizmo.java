package me.michqql.engine.scene.editor.module.gizmo;

import me.michqql.engine.gfx.render.PickingTexture;
import me.michqql.engine.gfx.texture.Sprite;
import me.michqql.engine.scene.editor.EditorScene;
import me.michqql.engine.scene.editor.module.GameViewport;
import org.joml.Vector2f;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(EditorScene editorScene, PickingTexture pickingTexture, GameViewport gameViewport,
                      Sprite sprite) {
        super(editorScene, pickingTexture, gameViewport, sprite);
    }

    @Override
    protected void onGizmoDrag() {
        Vector2f diff = getWorldMouseDiff();
        Vector2f dir = getDragDirection();

        getSelectedObject().getTransform().getScale().sub(diff.mul(dir));
    }

    @Override
    protected void onGizmoRelease() {
        if(editorScene.isGridSnappingEnabled()) {
            Vector2f scale = getSelectedObject().getTransform().getScale();
            Vector2f diff = new Vector2f(scale.x() % editorScene.getGridSize(), scale.y() % editorScene.getGridSize())
                    .mul(getDragDirection());

            if(diff.x() > editorScene.getGridSize() / 2f) {
                // Snap upwards
                scale.add(editorScene.getGridSize() - diff.x(), 0);
                diff.x = 0;
            }

            if(diff.y() > editorScene.getGridSize() / 2f) {
                scale.add(0, editorScene.getGridSize() - diff.y());
                diff.y = 0;
            }

            scale.sub(diff);
        }
    }
}
