package me.michqql.game.input;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyboardInput implements KeyListener.Receiver {

    private static KeyboardInput instance;

    public static KeyboardInput getInstance() {
        if(instance == null) {
            instance = new KeyboardInput();
            KeyListener.setReceiver(instance);
        }

        return instance;
    }

    private static final int numKeys;
    private static final boolean[] keysPressed;
    private static final boolean[] lastKeysPressed;

    static {
        numKeys = 256;
        keysPressed = new boolean[numKeys];
        lastKeysPressed = new boolean[numKeys];
    }

    public static boolean isKeyPressed(int key) {
        if(key >= numKeys)
            return false;

        return keysPressed[key];
    }

    // End of static

    private KeyboardInput() {
    }

    @Override
    public void onKeyPressed(int key, int action) {
        if(key >= numKeys)
            return;

        if(action == GLFW_PRESS) {
            keysPressed[key] = true;
        } else if(action == GLFW_RELEASE) {
            keysPressed[key] = false;
        }
    }
}
