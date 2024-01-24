package me.michqql.game.scene.editor.module;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import me.michqql.game.gfx.Window;
import me.michqql.game.gfx.camera.Camera;
import me.michqql.game.gfx.render.Framebuffer;
import me.michqql.game.input.MouseInput;
import org.joml.Vector4f;

public class GameViewport implements EditorModule {

    private final Camera camera;
    private final Framebuffer framebuffer;

    // Used for setting window position
    private final ImVec2 windowSize;
    private final ImVec2 windowPos;

    // Used for mouse position
    private final ImVec2 windowOffset;

    public GameViewport(Camera camera, Framebuffer framebuffer) {
        this.camera = camera;
        this.framebuffer = framebuffer;

        this.windowSize = new ImVec2();
        this.windowPos = new ImVec2();

        this.windowOffset = new ImVec2();
    }

    @Override
    public void update(float dt) {}

    public void display() {
        int windowFlags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
        ImGui.begin("Game Viewport", windowFlags);

        calculateLargestSize();
        calculateCenteredPosition();

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImGui.getCursorScreenPos(windowOffset);
        windowOffset.x -= ImGui.getScrollX();
        windowOffset.y -= ImGui.getScrollY();

        ImGui.image(framebuffer.getTexture().getTextureId(), windowSize.x, windowSize.y,
                0, 1, 1, 0);

        ImGui.end();
    }

    private void calculateLargestSize() {
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        windowSize.set(aspectWidth, aspectHeight);
    }

    private void calculateCenteredPosition() {
        windowOffset.set(ImGui.getCursorPos());

        ImGui.getContentRegionAvail(windowPos);
        windowPos.x -= ImGui.getScrollX();
        windowPos.y -= ImGui.getScrollY();

        float viewportX = (windowPos.x / 2.0f) - (windowSize.x / 2.0f);
        float viewportY = (windowPos.y / 2.0f) - (windowSize.y / 2.0f);

        windowPos.set(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }

    public boolean isMouseInViewportArea() {
        double x = MouseInput.getMouseX();
        double y = MouseInput.getMouseY();

        return (x >= windowOffset.x && x <= windowOffset.x + windowSize.x &&
                y >= windowOffset.y && y <= windowOffset.y + windowSize.y);
    }

    public float getAdjustedOrthographicX() {
        float x = ((float) (MouseInput.getMouseX() - windowOffset.x) / windowSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(x, 0, 0, 1);
        tmp.mul(camera.getInverseProjectionMatrix()).mul(camera.getInverseViewMatrix());
        return tmp.x;
    }

    public float getAdjustedOrthographicY() {
        float y = -(((float) (MouseInput.getMouseY() - windowOffset.y) / windowSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, y, 0, 1);
        tmp.mul(camera.getInverseProjectionMatrix()).mul(camera.getInverseViewMatrix());
        return tmp.y;
    }

    public float getScreenX() {
        return ((float) (MouseInput.getMouseX() - windowOffset.x) / windowSize.x) * Window.getWidth();
    }

    public float getScreenY() {
        return Window.getHeight() - ((float) (MouseInput.getMouseY() - windowOffset.y) / windowSize.y) * Window.getHeight();
    }
}
