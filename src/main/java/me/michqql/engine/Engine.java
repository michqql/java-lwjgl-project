package me.michqql.engine;

import me.michqql.engine.event.EventHandler;
import me.michqql.engine.event.Listener;
import me.michqql.engine.event.events.EnginePlayEvent;
import me.michqql.engine.event.events.EngineStopEvent;
import me.michqql.engine.gfx.render.PickingTexture;
import me.michqql.engine.scene.editor.RuntimeScene;
import me.michqql.engine.scene.editor.EditorScene;

public class Engine implements Listener {

    private static Engine instance;

    // Package private
    static Engine getInstance(Window window, PickingTexture pickingTexture) {
        if(instance == null)
            instance = new Engine(window, pickingTexture);

        return instance;
    }

    public static boolean isRunning() {
        return instance.running;
    }
    // End of static

    private final Window window;
    private final PickingTexture pickingTexture;
    private boolean running;

    private Engine(Window window, PickingTexture pickingTexture) {
        this.window = window;
        this.pickingTexture = pickingTexture;
    }

    @EventHandler
    public void onPlay(EnginePlayEvent e) {
        running = true;
        window.setScene(unused -> new RuntimeScene());
    }

    @EventHandler
    public void onStop(EngineStopEvent e) {
        running = false;
        window.setScene(unused -> new EditorScene(pickingTexture));
    }
}
