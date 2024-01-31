package me.michqql.engine.scene;

import imgui.ImGui;

public class LevelEditorScene  extends Scene implements GuiDisplayScene {
    @Override
    public void display() {
        ImGui.begin("Test");
        //ImGui.colorPicker4("Colour picker:", )
        ImGui.end();
    }
}
