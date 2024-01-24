package me.michqql.game.scene.editor.module;

import me.michqql.game.gfx.camera.Camera;
import me.michqql.game.input.KeyboardInput;
import me.michqql.game.input.MouseButton;
import me.michqql.game.input.MouseInput;
import me.michqql.game.util.MathUtil;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera implements EditorModule {

    private static final Vector2f ZERO_ZERO = new Vector2f();
    private static final float DRAG_SENSITIVITY = 30.0f;
    private static final float SCROLL_SENSITIVITY = 0.1f;
    private static final float MAX_ZOOM = 2.0f;

    private final Camera camera;
    private final Vector2f clickOrigin = new Vector2f(),
            delta = new Vector2f(),
            mousePos = new Vector2f();
    private boolean dragging;
    private boolean reset = false;
    private float resetTime;

    public EditorCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float dt) {
        if(dragging)
            MouseInput.setMouseCaptureRequested(true);

        if(MouseInput.isDragging(MouseButton.MIDDLE.getButtonCode())) {
            mousePos.set(camera.getOrthographicX(), camera.getOrthographicY());
            if(!dragging) {
                clickOrigin.set(mousePos);
                dragging = true;
                return;
            }

            delta.set(mousePos).sub(clickOrigin);
            camera.getPosition().sub(delta.mul(dt).mul(DRAG_SENSITIVITY));
            clickOrigin.lerp(mousePos, dt);
        } else {
            dragging = false;
        }

        if(MouseInput.getScrollY() != 0.0f) {
            float add = (float) Math.pow(Math.abs(MouseInput.getScrollY() * SCROLL_SENSITIVITY), 1 / camera.getZoom());
            add *= -Math.signum(MouseInput.getScrollY());
            camera.setZoom(camera.getZoom() + add);
            if(camera.getZoom() > MAX_ZOOM)
                camera.setZoom(MAX_ZOOM);

            camera.adjustProjection();
        }

        if(KeyboardInput.isKeyPressed(GLFW_KEY_KP_MULTIPLY)) {
            reset = true;
        }

        if(reset) {
            camera.getPosition().lerp(ZERO_ZERO, resetTime);
            camera.setZoom(MathUtil.lerp(camera.getZoom(), 1.0f, resetTime));
            camera.adjustProjection();
            this.resetTime += 0.1f * dt;

            if(Math.abs(camera.getPosition().x()) <= 1 && Math.abs(camera.getPosition().y()) <= 1) {
                camera.getPosition().set(ZERO_ZERO);
                camera.setZoom(1.0f);
                reset = false;
                resetTime = 0;
                camera.adjustProjection();
            }
        }
    }

    @Override
    public void display() {

    }
}
