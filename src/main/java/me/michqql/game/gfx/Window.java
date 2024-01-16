package me.michqql.game.gfx;

import me.michqql.game.gfx.gui.GuiManager;
import me.michqql.game.gfx.render.debug.DebugDraw;
import me.michqql.game.scene.LevelScene;
import me.michqql.game.scene.Scene;
import me.michqql.game.input.KeyListener;
import me.michqql.game.input.KeyboardInput;
import me.michqql.game.input.MouseInput;
import me.michqql.game.input.MouseListener;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL14C;

import java.util.Objects;
import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance;

    public static Window getInstance() {
        return instance == null ? (instance = new Window()) : instance;
    }

    private int width, height;
    private String title;

    private long glfwWindowId;

    // Scene
    private Scene currentScene;
    private Scene lastScene;

    // Gui
    private GuiManager guiManager;

    // Scenes
    private LevelScene levelScene;
    private EditorScene editorScene;

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Java Game";
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        levelScene = new LevelScene();
        //setScene(u -> levelScene);
        editorScene = new EditorScene();
        setScene(u -> editorScene);
        loop();
        stop();
    }

    public void setScene(Function<Void, Scene> sceneFactory) {
        // Keep a reference to the old scene and save it, if possible
        lastScene = currentScene;
        if(lastScene != null)
            lastScene.save();

        // Create and load the new scene
        currentScene = sceneFactory.apply(null);
        currentScene.init();
        // Only call postInit if the scene did not load from file
        if(!currentScene.load())
            currentScene.firstInit();
        currentScene.postInit();
        currentScene.start();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialise GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialise GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create the window
        glfwWindowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if(glfwWindowId == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Register input listeners and other callbacks
        // Mouse
        MouseInput.getInstance(); // registers the listener
        glfwSetCursorPosCallback(glfwWindowId, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindowId, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindowId, MouseListener::mouseScrollCallback);
        // Keyboard
        KeyboardInput.getInstance(); // registers the listener
        glfwSetKeyCallback(glfwWindowId, KeyListener::keyCallback);
        // Window resize callback
        glfwSetWindowSizeCallback(glfwWindowId, (windowId, nWidth, nHeight) -> {
            this.width = nWidth;
            this.height = nHeight;
        });

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindowId);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindowId);

        // Critical - do not remove
        GL.createCapabilities();

        // Enables alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
        GL14C.glBlendFuncSeparate(GL11C.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL11C.GL_SRC_ALPHA, GL11C.GL_DST_ALPHA);
        // Alpha blending end

        guiManager = new GuiManager(glfwWindowId, "#version 330 core");
    }

    private void loop() {
        float startTime = Time.getTime();
        float endTime;

        while(!glfwWindowShouldClose(glfwWindowId)) {
            // Calculate delta time
            endTime = Time.getTime();
            float deltaTime = endTime - startTime;
            startTime = endTime;

            // Poll mouse and key events
            glfwPollEvents();

            glClearColor(1f, 1f, 1f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Prepare the next frame
            DebugDraw.beginFrame();

            if(currentScene != null) {
                currentScene.update(deltaTime);

                // Draw the debug lines
                DebugDraw.draw(currentScene.getCamera());
            }

            // Update gui before swapping buffers
            guiManager.update(deltaTime, currentScene);

            glfwSwapBuffers(glfwWindowId);
        }

        currentScene.save();
    }

    private void stop() {
        guiManager.destroyImGui();

        // Free the memory
        Callbacks.glfwFreeCallbacks(glfwWindowId);
        glfwDestroyWindow(glfwWindowId);

        // Terminate GLFW
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public static int getWidth() {
        return getInstance().width;
    }

    public static int getHeight() {
        return getInstance().height;
    }
}
