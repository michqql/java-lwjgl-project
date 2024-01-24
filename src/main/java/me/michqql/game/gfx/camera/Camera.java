package me.michqql.game.gfx.camera;

import me.michqql.game.gfx.Window;
import me.michqql.game.input.MouseInput;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {

    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f inverseProjectionMatrix;
    private final Matrix4f inverseViewMatrix;
    private final Vector2f position;
    private final Vector2f projectionSize = new Vector2f(32f * 40f, 32f * 21f);

    private float zoom = 1.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();

        adjustProjection();
    }

    public Vector2f getPosition() {
        return position;
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0f, projectionSize.x() * zoom, 0f,
                projectionSize.y() * zoom, 0f, 100f);

        projectionMatrix.invert(inverseProjectionMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0f, 0f, -1f);
        Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
        viewMatrix.identity();
        viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20f),
                cameraFront.add(position.x, position.y, 0f),
                cameraUp
        );
        viewMatrix.invert(inverseViewMatrix);

        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getInverseProjectionMatrix() {
        return inverseProjectionMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return inverseViewMatrix;
    }

    public float getOrthographicX() {
        float x = ((float) MouseInput.getMouseX() / Window.getWidth()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(x, 0, 0, 1);
        tmp.mul(inverseProjectionMatrix).mul(inverseViewMatrix);
        return tmp.x;
    }

    public float getOrthographicY() {
        int windowHeight = Window.getHeight();
        float y = ((float) (windowHeight - MouseInput.getMouseY()) / windowHeight) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(0, y, 0, 1);
        tmp.mul(inverseProjectionMatrix).mul(inverseViewMatrix);
        return tmp.y;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
}
