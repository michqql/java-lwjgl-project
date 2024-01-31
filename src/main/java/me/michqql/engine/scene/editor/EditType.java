package me.michqql.engine.scene.editor;

import me.michqql.engine.scene.editor.custom.CustomEditorHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EditType {
    Class<?> value();
    Class<? extends CustomEditorHandler> handler() default CustomEditorHandler.class;
}
