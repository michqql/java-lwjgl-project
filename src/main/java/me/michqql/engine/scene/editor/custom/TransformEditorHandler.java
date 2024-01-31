package me.michqql.engine.scene.editor.custom;

import imgui.ImGui;
import me.michqql.engine.entity.Transform;
import me.michqql.engine.util.gui.GuiClassDisplay;

import java.lang.reflect.Field;

public class TransformEditorHandler implements CustomEditorHandler {


    @Override
    public void handle(Object value, Object object, Field field) {
        if(ImGui.collapsingHeader("Transform")) {
            final Transform transform = (Transform) value;

            // Transform edit code here
            // Position and scale
            GuiClassDisplay.drawVec2("Position", transform.getPosition());
            GuiClassDisplay.drawVec2("Scale", transform.getScale());
            // Rotation
            float startRotation = transform.getRotation();
            float[] rotationWrapper = { startRotation };
            GuiClassDisplay.drawFloat("Rotation", rotationWrapper, 0, 0, true, 0);
            if(rotationWrapper[0] != startRotation)
                transform.setRotation(rotationWrapper[0]);
            // Z-index
            int startIndex = transform.getZIndex();
            int[] zIndexWrapper = { startIndex };
            GuiClassDisplay.drawInt("Z-Index", zIndexWrapper, 0, 100, true, 0);
            if(zIndexWrapper[0] != startIndex)
                transform.setZIndex(zIndexWrapper[0]);
        }
    }
}
