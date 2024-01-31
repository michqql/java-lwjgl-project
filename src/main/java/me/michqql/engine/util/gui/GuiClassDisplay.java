package me.michqql.engine.util.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import me.michqql.engine.gfx.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GuiClassDisplay {

    private static final Vector3f X_BUTTON_COLOUR = new Vector3f(0.7f, 0.2f, 0.2f);
    private static final Vector3f X_BUTTON_HOVERED_COLOUR = new Vector3f(0.8f, 0.3f, 0.3f);
    private static final Vector3f X_BUTTON_ACTIVE_COLOUR = new Vector3f(0.9f, 0.1f, 0.1f);

    private static final Vector3f Y_BUTTON_COLOUR = new Vector3f(0.2f, 0.7f, 0.2f);
    private static final Vector3f Y_BUTTON_HOVERED_COLOUR = new Vector3f(0.3f, 0.8f, 0.3f);
    private static final Vector3f Y_BUTTON_ACTIVE_COLOUR = new Vector3f(0.1f, 0.9f, 0.1f);

    private static final Vector3f Z_BUTTON_COLOUR = new Vector3f(0.2f, 0.2f, 0.7f);
    private static final Vector3f Z_BUTTON_HOVERED_COLOUR = new Vector3f(0.3f, 0.3f, 0.8f);
    private static final Vector3f Z_BUTTON_ACTIVE_COLOUR = new Vector3f(0.1f, 0.1f, 0.9f);

    private static final Texture RESET_TEXTURE = Texture.REGISTRY.get("editor/reset_11x20.png");

    public static void drawInt(String label, int[] wrapper) {
        drawInt(label, wrapper, 0, 0, false, 0,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawInt(String label, int[] wrapper,
                               int min, int max,
                               boolean reset, int defaultValue) {
        drawInt(label, wrapper, min, max, reset, defaultValue,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawInt(String label, int[] wrapper,
                                 int min, int max,
                                 boolean reset, int defaultValue,
                                 Vector3f buttonColour, Vector3f buttonHovered, Vector3f buttonActive) {
        ImGui.pushID(label);

        // Create columns
        ImGui.columns(2);
        ImGui.setColumnWidth(0, 85);
        // First column: label text
        ImGui.text(label);

        ImGui.nextColumn();
        // Second column: slider
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        // Calculate size of each slider
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight * 0.75f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f);
        ImGui.pushItemWidth(widthEach);
        // X slider
        ImGui.pushStyleColor(ImGuiCol.Button, buttonColour.x, buttonColour.y, buttonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHovered.x, buttonHovered.y, buttonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonActive.x, buttonActive.y, buttonActive.z, 1.0f);
        if(ImGui.imageButton(RESET_TEXTURE.getTextureId(), 11, 20) && reset) {
            // Reset the X value to the default provided X
            wrapper[0] = defaultValue;
        }

        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.sliderInt("##" + label, wrapper, min, max);
        ImGui.popItemWidth();

        ImGui.nextColumn();

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static void drawFloat(String label, float[] wrapper) {
        drawFloat(label, wrapper, 0, 0, false, 0,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawFloat(String label, float[] wrapper,
                                 float min, float max,
                                 boolean reset, float defaultValue) {
        drawFloat(label, wrapper, min, max, reset, defaultValue,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawFloat(String label, float[] wrapper,
                                float min, float max,
                                boolean reset, float defaultValue,
                                Vector3f buttonColour, Vector3f buttonHovered, Vector3f buttonActive) {
        ImGui.pushID(label);

        // Create columns
        ImGui.columns(2);
        ImGui.setColumnWidth(0, 85);
        // First column: label text
        ImGui.text(label);

        ImGui.nextColumn();
        // Second column: slider
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        // Calculate size of each slider
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight * 0.75f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f);
        ImGui.pushItemWidth(widthEach);
        // X slider
        ImGui.pushStyleColor(ImGuiCol.Button, buttonColour.x, buttonColour.y, buttonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHovered.x, buttonHovered.y, buttonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonActive.x, buttonActive.y, buttonActive.z, 1.0f);
        if(ImGui.imageButton(RESET_TEXTURE.getTextureId(), 11, 20) && reset) {
            // Reset the X value to the default provided X
            wrapper[0] = defaultValue;
        }

        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##" + label, wrapper, 0.1f, min, max);
        ImGui.popItemWidth();

        ImGui.nextColumn();

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static void drawVec2(String label, Vector2f value) {
        drawVec2(label, value, "X", "Y",
                0, 0, 0, 0,
                false, 0, 0,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR,
                Y_BUTTON_COLOUR, Y_BUTTON_HOVERED_COLOUR, Y_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawVec2(String label, Vector2f value, String xLabel, String yLabel,
                                float minX, float maxX, float minY, float maxY,
                                boolean reset, float defaultX, float defaultY) {
        drawVec2(label, value, xLabel, yLabel, minX, maxX, minY, maxY, reset, defaultX, defaultY,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR,
                Y_BUTTON_COLOUR, Y_BUTTON_HOVERED_COLOUR, Y_BUTTON_ACTIVE_COLOUR);
    }

    /**
     * For no range, set minX = maxX and minY = maxY
     * @param label The text to display, labelling these values
     * @param value The vector value object
     * @param xLabel Text to display for x label
     * @param yLabel Text to display for y label
     * @param minX minimum value of x for slider
     * @param maxX maximum value of x for slider
     * @param minY minimum value of y for slider
     * @param maxY maximum value of y for slider
     * @param reset if pressing the buttons will reset the value to default
     * @param defaultX the default x value
     * @param defaultY the default y value
     */
    public static void drawVec2(String label, Vector2f value, String xLabel, String yLabel,
                                float minX, float maxX, float minY, float maxY,
                                boolean reset, float defaultX, float defaultY,
                                Vector3f xButtonColour, Vector3f xButtonHovered, Vector3f xButtonActive,
                                Vector3f yButtonColour, Vector3f yButtonHovered, Vector3f yButtonActive) {
        ImGui.pushID(label);

        // Create columns
        ImGui.columns(2);
        ImGui.setColumnWidth(0, 85);
        // First column: label text
        ImGui.text(label);

        ImGui.nextColumn();
        // Second column: x and y sliders
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        // Calculate size of each slider
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight * 0.75f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f);
        ImGui.pushItemWidth(widthEach);
        // X slider
        ImGui.pushStyleColor(ImGuiCol.Button, xButtonColour.x, xButtonColour.y, xButtonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, xButtonHovered.x, xButtonHovered.y, xButtonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, xButtonActive.x, xButtonActive.y, xButtonActive.z, 1.0f);
        if(ImGui.button(xLabel, buttonSize.x, buttonSize.y) && reset) {
            // Reset the X value to the default provided X
            value.x = defaultX;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] wrapper = { value.x };
        if(ImGui.dragFloat("##" + xLabel, wrapper, 0.1f, minX, maxX)) {
            value.x = wrapper[0];
        }
        ImGui.popItemWidth();
        ImGui.sameLine();

        // Y slider
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, yButtonColour.x, yButtonColour.y, yButtonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, yButtonHovered.x, yButtonHovered.y, yButtonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, yButtonActive.x, yButtonActive.y, yButtonActive.z, 1.0f);
        if(ImGui.button(yLabel, buttonSize.x, buttonSize.y) && reset) {
            // Reset the Y value to the default provided Y
            value.y = defaultY;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        wrapper = new float[]{value.y};
        if(ImGui.dragFloat("##" + yLabel, wrapper, 0.1f, minY, maxY)) {
            value.y = wrapper[0];
        }
        ImGui.popItemWidth();
        ImGui.nextColumn();

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static void drawVec3(String label, Vector3f value) {
        drawVec3(label, value, "X", "Y", "Z",
                0, 0, 0, 0, 0, 0,
                false, 0, 0, 0,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR,
                Y_BUTTON_COLOUR, Y_BUTTON_HOVERED_COLOUR, Y_BUTTON_ACTIVE_COLOUR,
                Z_BUTTON_COLOUR, Z_BUTTON_HOVERED_COLOUR, Z_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawVec3(String label, Vector3f value, String xLabel, String yLabel, String zLabel,
                                float minX, float maxX, float minY, float maxY, float minZ, float maxZ,
                                boolean reset, float defaultX, float defaultY, float defaultZ) {
        drawVec3(label, value, xLabel, yLabel, zLabel,
                minX, maxX, minY, maxY, minZ, maxZ,
                reset, defaultX, defaultY, defaultZ,
                X_BUTTON_COLOUR, X_BUTTON_HOVERED_COLOUR, X_BUTTON_ACTIVE_COLOUR,
                Y_BUTTON_COLOUR, Y_BUTTON_HOVERED_COLOUR, Y_BUTTON_ACTIVE_COLOUR,
                Z_BUTTON_COLOUR, Z_BUTTON_HOVERED_COLOUR, Z_BUTTON_ACTIVE_COLOUR);
    }

    public static void drawVec3(String label, Vector3f value, String xLabel, String yLabel, String zLabel,
                                float minX, float maxX, float minY, float maxY, float minZ, float maxZ,
                                boolean reset, float defaultX, float defaultY, float defaultZ,
                                Vector3f xButtonColour, Vector3f xButtonHovered, Vector3f xButtonActive,
                                Vector3f yButtonColour, Vector3f yButtonHovered, Vector3f yButtonActive,
                                Vector3f zButtonColour, Vector3f zButtonHovered, Vector3f zButtonActive) {
        ImGui.pushID(label);

        // Create columns
        ImGui.columns(2);
        ImGui.setColumnWidth(0, 85);
        // First column: label text
        ImGui.text(label);

        ImGui.nextColumn();
        // Second column: x and y sliders
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        // Calculate size of each slider
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight * 0.75f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f);
        ImGui.pushItemWidth(widthEach);
        // X slider
        ImGui.pushStyleColor(ImGuiCol.Button, xButtonColour.x, xButtonColour.y, xButtonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, xButtonHovered.x, xButtonHovered.y, xButtonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, xButtonActive.x, xButtonActive.y, xButtonActive.z, 1.0f);
        if(ImGui.button(xLabel, buttonSize.x, buttonSize.y) && reset) {
            // Reset the X value to the default provided X
            value.x = defaultX;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] wrapper = { value.x };
        if(ImGui.dragFloat("##" + xLabel, wrapper, 0.1f, minX, maxX)) {
            value.x = wrapper[0];
        }
        ImGui.popItemWidth();
        ImGui.sameLine();

        // Y slider
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, yButtonColour.x, yButtonColour.y, yButtonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, yButtonHovered.x, yButtonHovered.y, yButtonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, yButtonActive.x, yButtonActive.y, yButtonActive.z, 1.0f);
        if(ImGui.button(yLabel, buttonSize.x, buttonSize.y) && reset) {
            // Reset the Y value to the default provided Y
            value.y = defaultY;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        wrapper = new float[]{value.y};
        if(ImGui.dragFloat("##" + yLabel, wrapper, 0.1f, minY, maxY)) {
            value.y = wrapper[0];
        }
        ImGui.popItemWidth();

        // Z-Slider
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, zButtonColour.x, zButtonColour.y, zButtonColour.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, zButtonHovered.x, zButtonHovered.y, zButtonHovered.z, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, zButtonActive.x, zButtonActive.y, zButtonActive.z, 1.0f);
        if(ImGui.button(zLabel, buttonSize.x, buttonSize.y) && reset) {
            // Reset the Y value to the default provided Y
            value.z = defaultZ;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        wrapper = new float[]{ value.z };
        if(ImGui.dragFloat("##" + zLabel, wrapper, 0.1f, minZ, maxZ)) {
            value.z = wrapper[0];
        }
        ImGui.popItemWidth();

        // End of sliders
        ImGui.nextColumn();

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }
}
