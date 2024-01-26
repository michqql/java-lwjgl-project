package me.michqql.game.scene.editor.module.gizmo;

import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.scene.editor.module.GameViewport;
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
}
