package me.michqql.engine.scene.editor.custom;

import me.michqql.engine.util.Colour;
import me.michqql.engine.util.gui.GuiClassDisplay;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class ColourPickerEditorHandler implements CustomEditorHandler {

    @Override
    public void handle(Object value, Object object, Field field) throws IllegalAccessException {
        Colour colour = (Colour) value;
        Vector3f wrapper = new Vector3f(colour.x, colour.y, colour.z).mul(255);
        GuiClassDisplay.drawVec3("Colour", wrapper,
                "R", "G", "B", 0, 255, 0, 255, 0, 255,
                true, 255, 255, 255);
        colour.set(wrapper.div(255), 1.0f);
    }
}
