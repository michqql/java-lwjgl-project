package me.michqql.engine.event.events;

import me.michqql.engine.event.Event;
import me.michqql.engine.scene.Scene;

public class SceneLoadEvent extends Event {

    private final Scene scene;

    public SceneLoadEvent(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
