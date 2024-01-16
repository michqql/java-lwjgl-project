package me.michqql.game.scene.editor.custom;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.gfx.texture.Texture;
import me.michqql.game.gfx.texture.TextureAtlas;
import org.joml.Vector2f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SpritePicker implements CustomEditorHandler {

    private static final String SPRITE_POPUP = "Sprite selection";

    private final List<Sprite> spriteList;
    private Texture questionMarkTexture;

    private float displayScale = 1;

    private Sprite selectedSprite = null;

    public SpritePicker() {
        spriteList = new ArrayList<>();

        Texture texture = Texture.REGISTRY.get("spritesheet.png");
        TextureAtlas.getTextureAtlas(texture, 32, 32);

        loadRequiredTextures();
        loadTextureAtlases();
    }

    private void loadRequiredTextures() {
        this.questionMarkTexture = Texture.REGISTRY.get("editor/question_mark.png");
    }

    private void loadTextureAtlases() {
        spriteList.clear();

        for(TextureAtlas atlas : TextureAtlas.getAllTextureAtlases()) {
            for(int i = 0; i < atlas.getNumberOfSprites(); i++) {
                spriteList.add(atlas.getSprite(i));
            }
        }
    }

    @Override
    public void handle(Object value, Object object, Field field) throws IllegalAccessException {
        Sprite sprite = (Sprite) value;

        if(selectedSprite != null) {
            field.set(object, selectedSprite);
            sprite = selectedSprite;
        }

        if(ImGui.collapsingHeader("Sprite")) {
            ImGui.indent();

            if(sprite == null) {
                ImGui.text("No sprite selected");
                ImGui.sameLine();
                if(ImGui.imageButton(questionMarkTexture.getTextureId(), questionMarkTexture.getWidth(),
                        questionMarkTexture.getHeight())) {
                    // Open the popup
                    ImGui.openPopup(SPRITE_POPUP);
                }

            } else {
                ImGui.text("Selected sprite: ");
                ImGui.sameLine();
                Vector2f[] coords = sprite.getTextureCoords();
                if(ImGui.imageButton(sprite.getTexture().getTextureId(), sprite.getWidth(),
                        sprite.getHeight(), coords[0].x, coords[0].y, coords[2].x, coords[2].y)) {
                    // Open the popup
                    ImGui.openPopup(SPRITE_POPUP);
                }
            }

            spriteSelectionPopup();

            ImGui.unindent();
        }
    }

    private void spriteSelectionPopup() {
        boolean closePopup = false;
        if(ImGui.beginPopupModal(SPRITE_POPUP, ImGuiWindowFlags.MenuBar)) {
            if(ImGui.beginMenuBar()) {
                if(ImGui.beginMenu("Cancel")) {
                    closePopup = true;
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }


            ImGui.text("Select a sprite");

            ImVec2 windowPos = ImGui.getWindowPos();
            ImVec2 windowSize = ImGui.getWindowSize();
            ImVec2 itemSpacing = ImGui.getStyle().getItemSpacing();

            final float windowX2 = windowPos.x + windowSize.x;
            final int size = spriteList.size();
            for(int i = 0; i < size; i++) {
                Sprite sprite = spriteList.get(i);
                final float displayWidth = sprite.getWidth() * displayScale;
                Vector2f[] coords = sprite.getTextureCoords();

                ImGui.pushID(i);
                if(ImGui.imageButton(sprite.getTexture().getTextureId(), displayWidth,
                        sprite.getHeight() * displayScale, coords[0].x, coords[0].y, coords[2].x, coords[2].y)) {
                    selectedSprite = sprite;
                    closePopup = true;
                }
                ImGui.popID();

                ImVec2 lastButtonPos = ImGui.getItemRectMax();
                float nextX2 = lastButtonPos.x + itemSpacing.x + displayWidth;
                if(i + 1 < size && nextX2 < windowX2)
                    ImGui.sameLine();
            }

            ImGui.setItemDefaultFocus();
            if(ImGui.button("Cancel") || closePopup)
                ImGui.closeCurrentPopup();
            ImGui.endPopup();
        }
    }

    /*
    final int columns = 3;
            final int rows = (int) Math.ceil((double) spriteList.size() / (double) columns);
            final int size = spriteList.size();
            if(ImGui.beginTable("Table", columns)) {
                int index = 0;
                for(int row = 0; row < rows; row++) {
                    ImGui.tableNextRow();
                    for(int col = 0; col < columns; col++, index++) {
                        if(index >= size)
                            break;

                        ImGui.tableSetColumnIndex(col);

                        final Sprite sprite = spriteList.get(index);
                        final Texture tex = sprite.getTexture();

                        ImGui.image(tex.getTextureId(), tex.getWidth(), tex.getHeight());
                        //ImGui.image(tex.getTextureId(), tex.getWidth(), tex.getHeight(), sprite.getTextureCoords());
                    }
                }
                ImGui.endTable();
            }
     */
}
