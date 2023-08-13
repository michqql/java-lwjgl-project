package me.michqql.game.scene;

import me.michqql.game.entity.GameObject;
import me.michqql.game.gfx.camera.Camera;
import me.michqql.game.gfx.render.Renderer;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected final Camera camera = new Camera(new Vector2f());
    private boolean running = false;
    protected Renderer renderer;
    private final List<GameObject> gameObjectList = new ArrayList<>();

    public void init() {
    }

    public void start() {
        for (GameObject gameObject : gameObjectList) {
            gameObject.start();
            renderer.add(gameObject);
        }
    }

    public void addGameObject(GameObject gameObject) {
        gameObjectList.add(gameObject);
        if(running) {
            gameObject.start();
            renderer.add(gameObject);
        }
    }

    public void update(float deltaTime) {
        for(GameObject gameObject : gameObjectList) {
            gameObject.update(deltaTime);
        }

        renderer.render();
    }

    public Camera getCamera() {
        return camera;
    }
}
