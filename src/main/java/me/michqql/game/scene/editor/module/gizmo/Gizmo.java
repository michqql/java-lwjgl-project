package me.michqql.game.scene.editor.module.gizmo;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.NonSelectable;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.input.KeyboardInput;
import me.michqql.game.input.MouseButton;
import me.michqql.game.input.MouseInput;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.scene.editor.module.EditorModule;
import me.michqql.game.scene.editor.module.GameViewport;
import me.michqql.game.scene.editor.module.Inspector;
import me.michqql.game.util.GameObjectUtil;
import me.michqql.game.util.Prefab;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class Gizmo implements EditorModule {

    private static final Vector4f TRANSPARENT = new Vector4f(1, 1, 1, 0);

    private static final Vector4f X_AXIS_COLOUR = new Vector4f(1, 0, 0, 1);
    private static final Vector4f Y_AXIS_COLOUR = new Vector4f(0, 1, 0, 1);

    private static final Vector4f X_AXIS_COLOUR_HOVER = new Vector4f(1, 1, 0, 1);
    private static final Vector4f Y_AXIS_COLOUR_HOVER = new Vector4f(1, 1, 0, 1);

    protected final EditorScene editorScene;
    private final PickingTexture pickingTexture;
    private final GameViewport gameViewport;

    private final GameObject xAxisGizmo;
    private final SpriteRenderer xAxisSprite;
    private final GameObject yAxisGizmo;
    private final SpriteRenderer yAxisSprite;

    private GameObject selectedObject;
    private boolean xAxisDragging, yAxisDragging, dragBoth;
    private float worldX, worldY, lastWorldX, lastWorldY;

    public Gizmo(EditorScene editorScene, PickingTexture pickingTexture, GameViewport gameViewport,
                          Sprite sprite) {
        this.editorScene = editorScene;
        this.pickingTexture = pickingTexture;
        this.gameViewport = gameViewport;

        this.xAxisGizmo = Prefab.generateNonPersistentSpriteObject(sprite, 16, 48);
        xAxisGizmo.addComponent(new NonSelectable());
        this.xAxisSprite = xAxisGizmo.getComponent(SpriteRenderer.class);
        xAxisGizmo.getTransform().setZIndex(Integer.MAX_VALUE);

        this.yAxisGizmo = Prefab.generateNonPersistentSpriteObject(sprite, 16, 48);
        yAxisGizmo.addComponent(new NonSelectable());
        this.yAxisSprite = yAxisGizmo.getComponent(SpriteRenderer.class);
        yAxisGizmo.getTransform().setZIndex(Integer.MAX_VALUE);

        editorScene.addGameObject(xAxisGizmo);
        editorScene.addGameObject(yAxisGizmo);

        xAxisGizmo.getTransform().setRotation(90.0f);
        yAxisGizmo.getTransform().setRotation(180.0f);
    }

    protected void onGizmoDrag() {}
    protected void onGizmoRelease() {}

    @Override
    public void update(float dt) {
        if(selectedObject != null) {
            // Save compute power, only do these calculations if there is an object selected
            lastWorldX = worldX;
            lastWorldY = worldY;
            worldX = gameViewport.getAdjustedOrthographicX();
            worldY = gameViewport.getAdjustedOrthographicY();

            // Position the gizmos correctly
            xAxisGizmo.getTransform().getPosition().set(selectedObject.getTransform().getPosition())
                    .add(selectedObject.getTransform().getScale().x() +
                            xAxisGizmo.getTransform().getScale().y(),
                            selectedObject.getTransform().getScale().y() / 2f -
                                    xAxisGizmo.getTransform().getScale().x() / 2f);
            yAxisGizmo.getTransform().getPosition().set(selectedObject.getTransform().getPosition())
                    .add(selectedObject.getTransform().getScale().x() / 2f +
                            yAxisGizmo.getTransform().getScale().x() / 2f,
                            selectedObject.getTransform().getScale().y() +
                                    yAxisGizmo.getTransform().getScale().y()
                    );

            // Check if gizmos are hovered
            GameObject hovered = GameObjectUtil.getClickedGameObject(editorScene, pickingTexture,
                    (int) gameViewport.getScreenX(), (int) gameViewport.getScreenY());
            if(!yAxisDragging && (xAxisGizmo == hovered || xAxisDragging)) {
                xAxisSprite.setColour(X_AXIS_COLOUR_HOVER);
                yAxisSprite.setColour(Y_AXIS_COLOUR);
                if(MouseInput.isDragging(MouseButton.LEFT.getButtonCode())) {
                    xAxisDragging = true;
                }
            } else if(!xAxisDragging && (yAxisGizmo == hovered || yAxisDragging)) {
                xAxisSprite.setColour(X_AXIS_COLOUR);
                yAxisSprite.setColour(Y_AXIS_COLOUR_HOVER);
                if(MouseInput.isDragging(MouseButton.LEFT.getButtonCode())) {
                    yAxisDragging = true;
                }
            } else {
                xAxisSprite.setColour(X_AXIS_COLOUR);
                yAxisSprite.setColour(Y_AXIS_COLOUR);
            }

            // Check if dragging gizmo
            if(MouseInput.isDragging(MouseButton.LEFT.getButtonCode()) && (xAxisDragging || yAxisDragging)) {
                KeyboardInput.setKeyCaptureRequested(true);
                dragBoth = KeyboardInput.isKeyPressed(GLFW_KEY_LEFT_SHIFT);

                onGizmoDrag();
            } else {
                // Reset dragging
                if(xAxisDragging || yAxisDragging) {
                    // Only call release event if they were dragging
                    onGizmoRelease();
                }
                xAxisDragging = false;
                yAxisDragging = false;
            }
        }
    }

    @Override
    public void display() {

    }

    public GameObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(GameObject selectedObject) {
        if(selectedObject == xAxisGizmo || selectedObject == yAxisGizmo ||
                (selectedObject != null && selectedObject.getComponent(NonSelectable.class) != null)) {
            return;
        }

        this.selectedObject = selectedObject;
        if(selectedObject == null) {
            xAxisSprite.setColour(TRANSPARENT);
            yAxisSprite.setColour(TRANSPARENT);
        }
    }

    protected Vector2f getDragDirection() {
        return new Vector2f(xAxisDragging | dragBoth ? 1 : 0, yAxisDragging | dragBoth ? 1 : 0);
    }

    protected Vector2f getWorldMouseDiff() {
        return new Vector2f(lastWorldX - worldX, lastWorldY - worldY);
    }
}
