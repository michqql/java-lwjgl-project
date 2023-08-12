package me.michqql.game.input;

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
