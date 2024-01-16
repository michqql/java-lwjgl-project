package me.michqql.game.scene;

import me.michqql.game.entity.Component;
import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.GuiDisplay;

public interface GuiDisplayScene {

    /**
     * <p>
     *     Call all ImGui methods in this display method.
     * </p>
     * <p>
     *     Called by the {@link me.michqql.game.gfx.gui.GuiManager}
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
