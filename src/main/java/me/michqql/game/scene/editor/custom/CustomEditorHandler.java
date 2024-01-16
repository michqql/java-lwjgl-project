package me.michqql.game.scene.editor.custom;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface CustomEditorHandler {

    void handle(Object value, Object object, Field field) throws IllegalAccessException;

    class HandlerWrapper {
        final Class<? extends CustomEditorHandler> type;
        final Object handler;
        final Method handleMethod;

        public HandlerWrapper(Class<? extends CustomEditorHandler> type, Object handler, Method handleMethod) {
            this.type = type;
            this.handler = handler;
            this.handleMethod = handleMethod;
        }

        public Class<? extends CustomEditorHandler> getType() {
            return type;
        }

        public Object getHandler() {
            return handler;
        }

        public Method getHandleMethod() {
            return handleMethod;
        }
    }
}
