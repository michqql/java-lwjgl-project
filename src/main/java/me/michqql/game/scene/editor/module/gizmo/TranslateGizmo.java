package me.michqql.game.scene.editor.module.gizmo;

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
}
