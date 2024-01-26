package me.michqql.game.scene.editor.module;

import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.scene.editor.module.gizmo.Gizmo;
import org.joml.Vector2f;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(EditorScene editorScene, PickingTexture pickingTexture, GameViewport gameViewport, Inspector inspector, Sprite sprite) {
        super(editorScene, pickingTexture, gameViewport, inspector, sprite);
    }

    @Override
    protected void onGizmoDrag() {
        Vector2f diff = getWorldMouseDiff();
        Vector2f dir = getDragDirection();

        getSelectedObject().getTransform().getPosition().sub(diff.mul(dir));
    }
}
