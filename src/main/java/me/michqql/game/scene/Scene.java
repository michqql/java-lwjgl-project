package me.michqql.game.scene;

import me.michqql.game.entity.GameObject;
import me.michqql.game.gfx.camera.Camera;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera = new Camera(new Vector2f());
    private boolean running = false;
    private final List<GameObject> gameObjectList = new ArrayList<>();

    public void init() {
    }

    public void start() {
        for (GameObject gameObject : gameObjectList) {
            gameObject.start();
        }
    }

    public void addGameObject(GameObject gameObject) {
        gameObjectList.add(gameObject);
        if(running)
            gameObject.start();
    }

    public void update(float deltaTime) {
        for(GameObject gameObject : gameObjectList) {
            gameObject.update(deltaTime);
        }
    }
}
