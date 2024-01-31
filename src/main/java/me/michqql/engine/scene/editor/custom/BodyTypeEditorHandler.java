package me.michqql.engine.scene.editor.custom;

import imgui.ImGui;
import imgui.type.ImInt;
import me.michqql.engine.physics2d.BodyType;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BodyTypeEditorHandler implements CustomEditorHandler {

    @Override
    public void handle(Object value, Object object, Field field) throws IllegalAccessException {
        BodyType type = (BodyType) value;
        BodyType[] types = BodyType.values();
        ImInt index = new ImInt(type.ordinal());

        if(ImGui.combo(field.getName(), index, Arrays.stream(types).map(Enum::toString).toArray(String[]::new))) {
            field.set(object, types[index.get()]);
        }
    }
}
