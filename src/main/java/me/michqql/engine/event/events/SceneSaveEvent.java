package me.michqql.engine.event.events;

import me.michqql.engine.event.Event;
import me.michqql.engine.scene.Scene;

public class SceneSaveEvent extends Event {

    private final Scene scene;

    public SceneSaveEvent(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
