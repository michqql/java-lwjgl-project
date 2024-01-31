package me.michqql.engine.scene.editor.module.gizmo;

import me.michqql.engine.gfx.render.PickingTexture;
import me.michqql.engine.gfx.texture.Sprite;
import me.michqql.engine.scene.editor.EditorScene;
import me.michqql.engine.scene.editor.module.GameViewport;
import org.joml.Vector2f;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(EditorScene editorScene, PickingTexture pickingTexture, GameViewport gameViewport,
                          Sprite sprite) {
        super(editorScene, pickingTexture, gameViewport, sprite);
    }

    @Override
    protected void onGizmoDrag() {
        Vector2f diff = getWorldMouseDiff();
        Vector2f dir = getDragDirection();

        getSelectedObject().getTransform().getPosition().sub(diff.mul(dir));
    }

    @Override
    protected void onGizmoRelease() {
        if(editorScene.isGridSnappingEnabled()) {
            Vector2f pos = getSelectedObject().getTransform().getPosition();
            Vector2f diff = new Vector2f(pos.x() % editorScene.getGridSize(), pos.y() % editorScene.getGridSize())
                            .mul(getDragDirection());

            if(diff.x() > editorScene.getGridSize() / 2f) {
                // Snap upwards
                pos.add(editorScene.getGridSize() - diff.x(), 0);
                diff.x = 0;
            }

            if(diff.y() > editorScene.getGridSize() / 2f) {
                pos.add(0, editorScene.getGridSize() - diff.y());
                diff.y = 0;
            }

            pos.sub(diff);
        }
    }
}
