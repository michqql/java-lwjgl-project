package me.michqql.engine.input;

import me.michqql.engine.Window;
import org.joml.Vector4f;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseInput implements MouseListener.MouseReceiver {

    private static MouseInput instance;

    public static MouseInput getInstance() {
        if(instance == null) {
            instance = new MouseInput();
            MouseListener.register(instance);
        }
        return instance;
    }

    private static double scrollX, scrollY;
    private static double mouseX, mouseY, lastX, lastY;
    private static final int numButtons;
    private static final boolean[] buttonsPressed;
    private static final boolean[] dragging;

    private static boolean mouseCaptureRequest;

    static {
        numButtons = MouseInfo.getNumberOfButtons();
        buttonsPressed = new boolean[numButtons];
        dragging = new boolean[numButtons];
    }

    public static void endFrame() {
        scrollX = 0;
        scrollY = 0;
        lastX = mouseX;
        lastY = mouseY;
    }

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static double getOrthographicX() {
        // x in range (-1, 1)
        float x = ((float) getMouseX() / Window.getWidth()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(x, 0, 0, 1);
        return x;
    }

    public static double getOrthographicY() {
        return -1;
    }

    public static double getMouseDx() {
        return lastX - mouseX;
    }

    public static double getMouseDy() {
        return lastY - mouseY;
    }

    public static double getScrollX() {
        return scrollX;
    }

    public static double getScrollY() {
        return scrollY;
    }

    public static boolean isMouseButtonDown(int button) {
        if(button >= numButtons)
            return false;

        return buttonsPressed[button];
    }

    public static boolean isDragging(int button) {
        if(button >= numButtons)
            return false;

        return dragging[button];
    }

    public static void setMouseCaptureRequested(boolean b) {
        mouseCaptureRequest = b;
    }

    public static boolean isMouseCaptureRequested() {
        return mouseCaptureRequest;
    }

    // End of static methods

    private MouseInput() {
    }

    @Override
    public void onMouseMove(double newX, double newY) {
        lastX = mouseX;
        lastY = mouseY;
        mouseX = newX;
        mouseY = newY;

        for(int i = 0; i < numButtons; i++) {
            if(buttonsPressed[i])
                dragging[i] = true;
        }
    }

    @Override
    public void onMousePress(int button, int action) {
        if(button >= numButtons)
            return;

        if(action == GLFW_PRESS) {
            buttonsPressed[button] = true;
        } else if(action == GLFW_RELEASE) {
            buttonsPressed[button] = false;
            dragging[button] = false;
        }
    }

    @Override
    public void onMouseScroll(double dx, double dy) {
        scrollX = dx;
        scrollY = dy;
    }
}
