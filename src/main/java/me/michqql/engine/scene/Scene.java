package me.michqql.engine.scene;

import me.michqql.engine.entity.Component;
import me.michqql.engine.entity.GameObject;
import me.michqql.engine.gfx.camera.Camera;
import me.michqql.engine.gfx.render.Renderer;
import me.michqql.engine.physics2d.Physics2d;
import me.michqql.engine.util.Time;
import me.michqql.engine.util.serialization.Serializer;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class Scene {

    protected final Camera camera = new Camera(new Vector2f());
    private boolean running = false;
    public Renderer renderer = Renderer.getDefaultRenderer();
    private final Physics2d physics = new Physics2d();
    protected boolean updatePhysics = true;

    private final List<GameObject> gameObjectList = new ArrayList<>();

    /**
     * <p>
     *     Do not create game objects in this init method.
     *     This init method should be used for creating shaders, renderers, loading textures, etc.
     *     Use {@link Scene#firstInit()} for creating game objects, as this method is only called
     *     if the level has not been loaded from file.
     * </p>
     */
    public void init() {
        renderer.getShader().prepareUploads(uploader -> {
            uploader.matrix4f("uProjMatrix", camera.getProjectionMatrix());
            uploader.matrix4f("uViewMatrix", camera.getViewMatrix());
            uploader.floatValue("uTime", Time.getTime());
        });
    }

    /**
     * <p>
     *     This method is only called if the scene has not been loaded from file.
     *     This method should be used to create any {@link GameObject}'s.
     * </p>
     */
    public void firstInit() {
    }

    public void postInit() {
    }

    public void start() {
        for (GameObject gameObject : gameObjectList) {
            handleAddedGameObject(gameObject);
        }
        running = true;
    }

    public void addGameObject(GameObject gameObject) {
        gameObjectList.add(gameObject);
        if(running) {
            handleAddedGameObject(gameObject);
        }
    }

    public GameObject getGameObjectByUUID(UUID uuid) {
        for(GameObject obj : gameObjectList) {
            if(obj.getUuid().equals(uuid)) return obj;
        }
        return null;
    }

    public void update(float deltaTime) {
        if(updatePhysics) physics.update(deltaTime);

        // Faster iteration while removing method
        for (Iterator<GameObject> it = gameObjectList.iterator(); it.hasNext(); ) {
            GameObject gameObject = it.next();

            gameObject.getAndClearDirtyComponents();
            gameObject.update(deltaTime);

            if(gameObject.isRemoved()) {
                it.remove();
                handleRemovedGameObject(gameObject);
            }
        }
    }

    public void render() {
        renderer.render();
    }

    public Camera getCamera() {
        return camera;
    }

    public void save() {
        try(FileWriter fw = new FileWriter("level.json")) {
            List<GameObject> persistentGOs = new ArrayList<>();
            for(GameObject go : gameObjectList) {
                if(go.isPersistent()) persistentGOs.add(go);
            }
            fw.write(Serializer.gson().toJson(persistentGOs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean load() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("level.json")));

            GameObject[] gos = Serializer.gson().fromJson(json, GameObject[].class);
            for (GameObject go : gos) {
                if(go != null)
                    addGameObject(go);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper methods
    public final void forEachGameObject(Consumer<GameObject> consumer) {
        gameObjectList.forEach(consumer);
    }

    private void handleAddedGameObject(GameObject gameObject) {
        gameObject.start();
        renderer.add(gameObject);
        if(updatePhysics)
            physics.addGameObject(gameObject);
    }

    private void handleRemovedGameObject(GameObject gameObject) {
        renderer.remove(gameObject);
        if(updatePhysics)
            physics.removeGameObject(gameObject);
    }

    // Package-private
    List<GameObject> getGameObjects() {
        return gameObjectList;
    }
}
