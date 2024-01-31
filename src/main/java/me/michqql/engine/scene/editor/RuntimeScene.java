package me.michqql.engine.scene.editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.michqql.engine.Engine;
import me.michqql.engine.Window;
import me.michqql.engine.event.EventManager;
import me.michqql.engine.event.events.EnginePlayEvent;
import me.michqql.engine.event.events.EngineStopEvent;
import me.michqql.engine.gfx.render.Framebuffer;
import me.michqql.engine.scene.GuiDisplayScene;
import me.michqql.engine.scene.Scene;
import me.michqql.engine.scene.editor.module.GameViewport;

import static org.lwjgl.opengl.GL11.*;

public class RuntimeScene extends Scene implements GuiDisplayScene {

    private final Framebuffer framebuffer;
    private final GameViewport gameViewport;

    public RuntimeScene() {
        super();
        this.framebuffer = new Framebuffer(Window.getWidth(), Window.getHeight());
        this.gameViewport = new GameViewport(camera, framebuffer);
    }

    @Override
    public void display() {
        framebuffer.bind();
        glClearColor(1f, 1f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        super.render(); // Render to the framebuffer
        framebuffer.unbind(); // Unbind frame buffer before draw

        debugWindow();
        gameViewport.display();
    }

    private void debugWindow() {
        ImGui.begin("Level Editor", ImGuiWindowFlags.MenuBar);

        if(ImGui.beginMenuBar()) {
            if (ImGui.menuItem("Play", "", false, !Engine.isRunning()))
                startPlay();
            if (ImGui.menuItem("Stop", "", false, Engine.isRunning()))
                stopPlay();
            ImGui.endMenuBar();
        }

        ImGui.end();
    }

    private void startPlay() {
        EventManager.getInstance().callEvent(new EnginePlayEvent());
    }

    private void stopPlay() {
        EventManager.getInstance().callEvent(new EngineStopEvent());
    }

    @Override
    public void displayForGameObjects(Scene scene) {
        // Do not display GUI for game objects during runtime
    }
}
