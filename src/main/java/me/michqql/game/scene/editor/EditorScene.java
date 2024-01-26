package me.michqql.game.scene.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImBoolean;
import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.Window;
import me.michqql.game.gfx.render.Framebuffer;
import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.gfx.render.debug.DebugDraw;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.gfx.texture.Texture;
import me.michqql.game.gfx.texture.TextureAtlas;
import me.michqql.game.input.MouseInput;
import me.michqql.game.scene.GuiDisplayScene;
import me.michqql.game.scene.Scene;
import me.michqql.game.scene.editor.module.EditorCamera;
import me.michqql.game.scene.editor.module.GameViewport;
import me.michqql.game.scene.editor.module.Inspector;
import me.michqql.game.scene.editor.module.TranslateGizmo;
import me.michqql.game.util.Prefab;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.*;

public class EditorScene extends Scene implements GuiDisplayScene {

    // Level editor
    private List<Sprite> spriteList;
    private float displayScale = 1.0f;
    private GameObject holdingObject = null;
    private boolean gridLines;
    private int gridSize = 32;
    private boolean gridSnapping;

    // Game object editor
    private GameObject selectedGameObject = null;

    // Game window
    private final Framebuffer framebuffer;
    private final GameViewport gameViewport;
    private final Inspector inspector;
    private final EditorCamera editorCamera;
    private final TranslateGizmo translateGizmo;

    public EditorScene(PickingTexture pickingTexture) {
        this.framebuffer = new Framebuffer(Window.getWidth(), Window.getHeight());
        this.gameViewport = new GameViewport(camera, framebuffer);
        this.inspector = new Inspector(this, gameViewport, pickingTexture);
        this.editorCamera = new EditorCamera(camera);
        Texture gizmoTexture = Texture.REGISTRY.get("gizmos.png");
        TextureAtlas atlas = TextureAtlas.getTextureAtlas(gizmoTexture, 24, 48);
        Sprite arrowSprite = atlas.getSprite(1);
        this.translateGizmo = new TranslateGizmo(this, pickingTexture, gameViewport, inspector, arrowSprite);

        Texture tex = Texture.REGISTRY.get("spritesheet.png");
        TextureAtlas.getTextureAtlas(tex, 32, 32);
    }

    @Override
    public void postInit() {
        // Get game object
        forEachGameObject(this::setSelectedGameObject);
        spriteList = Sprite.getSpriteCache();
    }

    @Override
    public void update(float deltaTime) {
        MouseInput.setMouseCaptureRequested(gameViewport.isMouseInViewportArea());
        updateHeldObject();

        inspector.update(deltaTime);
        editorCamera.update(deltaTime);
        translateGizmo.update(deltaTime);

        super.update(deltaTime);
    }

    // Render method omitted from editor scene

    // Called by the GuiManager last thing in the window loop method
    @Override
    public void display() {
        framebuffer.bind();
        glClearColor(1f, 1f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        super.render(); // Render to the framebuffer
        drawGridLines();
        DebugDraw.draw(getCamera());
        framebuffer.unbind(); // Unbind frame buffer before draw

        editLevel();
        gameViewport.display();
        inspector.display();
    }

    private void updateHeldObject() {
        if(holdingObject == null || !gameViewport.isMouseInViewportArea())
            return;

        if (MouseInput.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            holdingObject = null;
            return;
        }

        final Vector2f pos = holdingObject.getTransform().getPosition();
        pos.set(gameViewport.getAdjustedOrthographicX(), gameViewport.getAdjustedOrthographicY());
        if(gridSnapping) {
            // Snap to grid
            pos.sub(pos.x() % gridSize, pos.y() % gridSize);
        } else {
            // Center sprite on mouse position
            SpriteRenderer sr = holdingObject.getComponent(SpriteRenderer.class);
            float offX = 16, offY = 16;
            if(sr != null && sr.getSprite() != null) {
                offX = (holdingObject.getTransform().getScale().x) / 2;
                offY = (holdingObject.getTransform().getScale().y) / 2;
            }

            pos.sub(offX, offY);
        }
    }

    private void editLevel() {
        ImGui.begin("Level Editor");

        if(ImGui.collapsingHeader("Debug")) {
            ImGui.indent();

            // Grid toggle checkbox
            ImBoolean checkboxValue = new ImBoolean(gridLines);
            if(ImGui.checkbox("Draw Grid Lines", checkboxValue))
                gridLines = checkboxValue.get();

            // Grid snapping checkbox
            ImGui.sameLine();
            checkboxValue.set(gridSnapping);
            if(ImGui.checkbox("Grid Snapping", checkboxValue))
                gridSnapping = checkboxValue.get();

            // Grid size slider
            int[] sliderValue = { gridSize };
            ImGui.sliderInt("Grid Size", sliderValue, 12, 256);
            gridSize = sliderValue[0];

            ImGui.unindent();
        }

        if(ImGui.collapsingHeader("Add new Game Object")) {
            ImGui.indent();
            ImGui.text("Select a sprite");

            ImVec2 windowPos = ImGui.getWindowPos();
            ImVec2 windowSize = ImGui.getWindowSize();
            ImVec2 itemSpacing = ImGui.getStyle().getItemSpacing();

            final float windowX2 = windowPos.x + windowSize.x;
            final int size = spriteList.size();
            for(int i = 0; i < size; i++) {
                Sprite sprite = spriteList.get(i);
                final float displayWidth = sprite.getWidth() * displayScale;
                final float displayHeight = sprite.getHeight() * displayScale;
                Vector2f[] coords = sprite.getTextureCoords();

                ImGui.pushID(i);
                if(ImGui.imageButton(sprite.getTexture().getTextureId(), displayWidth,
                        displayHeight, coords[2].x, coords[0].y, coords[0].x, coords[2].y)) {
                    GameObject obj = Prefab.generateSpriteObject(sprite, displayWidth, displayHeight);
                    holdingObject = obj;
                    addGameObject(obj);
                }
                ImGui.popID();

                ImVec2 lastButtonPos = ImGui.getItemRectMax();
                float nextX2 = lastButtonPos.x + itemSpacing.x + displayWidth;
                if(i + 1 < size && nextX2 < windowX2)
                    ImGui.sameLine();
            }
            ImGui.unindent();
        }

        ImGui.end();
    }

    private void drawGridLines() {
        if(!gridLines)
            return;

        Vector2f cameraPos = camera.getPosition();
        Vector2f projSize = camera.getProjectionSize();

        int firstX = ((int) ((cameraPos.x() / gridSize) - 1) * gridSize);
        int firstY = ((int) ((cameraPos.y() / gridSize) - 1) * gridSize);

        int numVertLines = (int) (projSize.x() * camera.getZoom() / gridSize) + 2;
        int numHorLines = (int) (projSize.y() * camera.getZoom() / gridSize) + 2;

        // Draw vertical lines
        for(int i = 0; i < numVertLines; i++) {
            int x = firstX + (gridSize * i);
            DebugDraw.addLine2D(new Vector2f(x, firstY),
                    new Vector2f(x, firstY + projSize.y() * camera.getZoom() + gridSize * 2),
                    new Vector3f(), false, 20);
        }

        // Draw horizontal lines
        for(int i = 0; i < numHorLines; i++) {
            int y = firstY + (gridSize * i);
            DebugDraw.addLine2D(new Vector2f(firstX, y),
                    new Vector2f(firstX + projSize.x * camera.getZoom() + gridSize * 2, y),
                    new Vector3f(), false, 20);
        }
    }

    public void setSelectedGameObject(GameObject selectedGameObject) {
        this.selectedGameObject = selectedGameObject;
    }

    public GameObject getSelectedGameObject() {
        return selectedGameObject;
    }

    public GameViewport getGameViewport() {
        return gameViewport;
    }
}
