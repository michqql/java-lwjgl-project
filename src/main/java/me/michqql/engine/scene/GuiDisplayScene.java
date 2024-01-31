package me.michqql.engine.scene;

import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.components.GuiDisplay;

public interface GuiDisplayScene {

    /**
     * <p>
     *     Call all ImGui methods in this display method.
     * </p>
     * <p>
     *     Called by the {@link me.michqql.engine.gfx.gui.GuiManager}
     * </p>
     */
    void display();

    default void displayForGameObjects(Scene scene) {
        for(GameObject go : scene.getGameObjects()) {
            GuiDisplay component = go.getComponent(GuiDisplay.class);
            if(component != null)
                component.displayGui();
        }
    }
}
