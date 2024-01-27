package me.michqql.game.scene.editor.module.gizmo;

import me.michqql.game.entity.Transform;
import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.scene.editor.module.GameViewport;
import me.michqql.game.scene.editor.module.Inspector;
import me.michqql.game.scene.editor.module.gizmo.Gizmo;
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
