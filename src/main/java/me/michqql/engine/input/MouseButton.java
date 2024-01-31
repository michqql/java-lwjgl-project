package me.michqql.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public enum MouseButton {

    LEFT(GLFW_MOUSE_BUTTON_LEFT),
    MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
    RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
    ;

    final int glfwValue;

    MouseButton(int glfwValue) {
        this.glfwValue = glfwValue;
    }

    public int getButtonCode() {
        return glfwValue;
    }
}
