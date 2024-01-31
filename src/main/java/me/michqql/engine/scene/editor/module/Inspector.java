package me.michqql.engine.scene.editor.module;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import me.michqql.engine.entity.Component;
import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.Transform;
import me.michqql.engine.entity.components.Conflicts;
import me.michqql.engine.entity.components.NonSelectable;
import me.michqql.engine.gfx.render.PickingTexture;
import me.michqql.engine.gfx.texture.Sprite;
import me.michqql.engine.input.KeyboardInput;
import me.michqql.engine.input.MouseButton;
import me.michqql.engine.input.MouseInput;
import me.michqql.engine.physics2d.BodyType;
import me.michqql.engine.scene.editor.EditType;
import me.michqql.engine.scene.editor.EditorScene;
import me.michqql.engine.scene.editor.custom.*;
import me.michqql.engine.util.Colour;
import me.michqql.engine.util.GameObjectUtil;
import me.michqql.engine.util.gui.GuiClassDisplay;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.reflections.Reflections;

import java.lang.reflect.*;
import java.util.*;

public class Inspector implements EditorModule {

    // Static
    // Class, method to edit class type
    private static final Map<Class<?>, Method> EDITOR_METHOD_CACHE;
    // Class, handler wrapper
    private static final Map<Class<?>, CustomEditorHandler.HandlerWrapper> CUSTOM_HANDLER_CACHE;
    // Name, component class type
    private static final Map<String, Class<? extends Component>> CLASS_NAME_TO_COMPONENT_MAP;
    private static boolean loaded = false;

    static {
        // Get edit methods in this class
        EDITOR_METHOD_CACHE = new HashMap<>();
        CUSTOM_HANDLER_CACHE = new HashMap<>();
        CLASS_NAME_TO_COMPONENT_MAP = new HashMap<>();
    }

    public static void lazyLoad() {
        if(loaded)
            return;

        // Get all edit methods
        Class<?> type = Inspector.class;
        Method[] methods = type.getDeclaredMethods();
        for(Method method : methods) {
            EditType annotation = method.getAnnotation(EditType.class);
            if(annotation != null) {
                if(annotation.handler().equals(CustomEditorHandler.class)) {
                    // No custom handler is defined, so use the 'primitive' method in this class
                    EDITOR_METHOD_CACHE.put(annotation.value(), method);
                } else {
                    // This class type has a custom handler
                    Class<? extends CustomEditorHandler> handlerClass = annotation.handler();
                    try {
                        Constructor<? extends CustomEditorHandler> constructor = handlerClass.getConstructor();
                        CustomEditorHandler handler = constructor.newInstance();

                        Method handleMethod = handlerClass.getMethod("handle", Object.class, Object.class, Field.class);

                        CustomEditorHandler.HandlerWrapper wrapper =
                                new CustomEditorHandler.HandlerWrapper(handlerClass, handler, handleMethod);

                        CUSTOM_HANDLER_CACHE.put(annotation.value(), wrapper);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        // Get all components
        Reflections reflections = new Reflections("me.michqql.engine.entity");
        Set<Class<? extends Component>> classes = reflections.getSubTypesOf(Component.class);
        if(classes != null) {
            for (Class<? extends Component> clazz : classes) {
                // Skip abstract components
                if(Modifier.isAbstract(clazz.getModifiers()))
                    continue;
                CLASS_NAME_TO_COMPONENT_MAP.put(clazz.getSimpleName(), clazz);
            }
        }

        // Ensures this method cant be called again
        loaded = true;
    }

    private static boolean callEditMethod(Object inspector, Object value, Object object,
                                       Field field) throws InvocationTargetException, IllegalAccessException {
        if(!loaded) lazyLoad();

        Object callingObject = inspector;
        Method method = EDITOR_METHOD_CACHE.get(field.getType());

        // 1. Check if there is a custom handler for the object type
        CustomEditorHandler.HandlerWrapper wrapper = CUSTOM_HANDLER_CACHE.get(object.getClass());
        if(wrapper == null) // 2. Check if there is a custom handler for the field type
            wrapper = CUSTOM_HANDLER_CACHE.get(field.getType());

        // 3. There is a custom handler, set these variables
        if(wrapper != null) {
            callingObject = wrapper.getHandler();
            method = wrapper.getHandleMethod();
        }

        // 4. No editor method or custom handler exists, return
        if(method == null)
            return false;

        // 5. Make the method accessible
        boolean access = method.canAccess(callingObject);
        method.setAccessible(true);

        try {
            // 6. Call the method
            method.invoke(callingObject, value, object, field);
        } finally {
            method.setAccessible(access);
        }
        return true;
    }
    // Static end

    private final EditorScene editorScene;
    private final GameViewport gameViewport;
    private final PickingTexture pickingTexture;

    private GameObject selectedObject;
    private final Map<Class<?>, Field[]> cachedFields = new HashMap<>();
    private long lastClickTime = 0;

    public Inspector(EditorScene editorScene, GameViewport gameViewport,
                     PickingTexture pickingTexture) {
        this.editorScene = editorScene;
        this.gameViewport = gameViewport;
        this.pickingTexture = pickingTexture;
    }

    @Override
    public void update(float dt) {
        if(MouseInput.isMouseButtonDown(MouseButton.LEFT.getButtonCode()) &&
                !MouseInput.isDragging(MouseButton.LEFT.getButtonCode())) {
            if(System.currentTimeMillis() - lastClickTime < 200)
                return;

            int x = (int) gameViewport.getScreenX();
            int y = (int) gameViewport.getScreenY();

            setSelectedObject(GameObjectUtil.getClickedGameObject(editorScene, pickingTexture, x, y));
            lastClickTime = System.currentTimeMillis();
        }

        if(selectedObject != null && KeyboardInput.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
            selectedObject.remove();
            setSelectedObject(null);
        }
    }

    @Override
    public void display() {
        editGameObject();
    }

    private void editGameObject() {
        ImGui.begin("Inspector");
        if(selectedObject == null) {
            ImGui.text("Inspecting: none");
            ImGui.textWrapped("Select a game object to edit properties");
        } else {
            if(cachedFields.isEmpty())
                setupCachedFields();

            ImGui.text("Inspecting: " + selectedObject.getName());

            // Displays the fields for the game object fields
            ImGui.text("Game Object");
            drawFields(selectedObject);

            ImGui.separator();
            ImGui.text("Components");

            for(Component component : selectedObject.getComponentList()) {
                if(ImGui.collapsingHeader(component.getClass().getSimpleName())) {
                    ImGui.indent();
                    drawFields(component);

                    // Remove component button
                    if(ImGui.button("-")) {
                        selectedObject.removeComponent(component.getClass());
                        break;
                    }

                    ImGui.unindent();
                }
            }
        }

        addComponentPopup();

        ImGui.end();
    }

    private void addComponentPopup() {
        if(selectedObject == null || CLASS_NAME_TO_COMPONENT_MAP.isEmpty()) {
            return;
        }

        if(ImGui.beginPopupContextWindow("ComponentPopup")) {
            ImGui.text("Add component");
            ImGui.separator();

            List<Class<? extends Component>> excluded = new ArrayList<>();
            for(Map.Entry<String, Class<? extends Component>> entry : CLASS_NAME_TO_COMPONENT_MAP.entrySet()) {
                // Only care about this component class if our selected object has it
                if(selectedObject.getComponent(entry.getValue()) == null) continue;

                Conflicts conflicts = entry.getValue().getAnnotation(Conflicts.class);
                if(conflicts == null) continue; // Component has no conflicts, ignore

                excluded.addAll(Arrays.asList(conflicts.conflictingComponents()));
            }

            for (Map.Entry<String, Class<? extends Component>> entry : CLASS_NAME_TO_COMPONENT_MAP.entrySet()) {
                if(selectedObject.getComponent(entry.getValue()) != null || excluded.contains(entry.getValue()))
                    continue;

                if(ImGui.menuItem("Add " + entry.getKey())) {
                    Class<?> componentClass = entry.getValue();
                    try {
                        Constructor<?> constructor = componentClass.getDeclaredConstructor();
                        Object obj = constructor.newInstance();

                        if (obj instanceof Component component) {
                            // Which it should be!
                            selectedObject.addComponent(component);
                            ImGui.closeCurrentPopup();
                        }

                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            ImGui.endPopup();
        }
    }

    private void drawFields(Object obj) {
        Field[] fields = cachedFields.get(obj.getClass());
        if(fields == null) {
            // Re-cache the object
            setupCachedFields();
            return;
        }

        for(Field f : fields) {
            if(Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) continue;

            boolean access = f.canAccess(obj);
            f.setAccessible(true);

            try {
                callEditMethod(this, f.get(obj), obj, f);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                f.setAccessible(access);
            }
        }
    }

    private void setupCachedFields() {
        cachedFields.clear();

        // Cache the fields of the game object
        Field[] fields = selectedObject.getClass().getDeclaredFields();
        cachedFields.put(selectedObject.getClass(), fields);

        // Cache the fields of the components of this game object
        for(Component component : selectedObject.getComponentList()) {
            fields = component.getClass().getDeclaredFields();
            cachedFields.put(component.getClass(), fields);
        }
    }

    public GameObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(GameObject selectedObject) {
        if(selectedObject == null || selectedObject.getComponent(NonSelectable.class) == null)
            this.selectedObject = selectedObject;
    }

    // Editor fields
    @EditType(String.class)
    private void stringEdit(Object value, Object object, Field field) throws IllegalAccessException {
        ImGui.text(field.getName() + ": ");
        ImGui.sameLine();

        ImString wrapper = new ImString((String) value);
        if(ImGui.inputText("##", wrapper, ImGuiInputTextFlags.CallbackResize))
            field.set(object, wrapper.get());
    }

    @EditType(int.class)
    private void intEdit(Object value, Object object, Field field) throws IllegalAccessException {
        ImGui.text(field.getName() + ": ");
        ImGui.sameLine();

        int[] wrapper = { (int) value };
        if(ImGui.dragInt("##", wrapper))
            field.set(object, wrapper[0]);
    }

    @EditType(float.class)
    private void floatEdit(Object value, Object object, Field field) throws IllegalAccessException {
        float initial = (float) value;
        float[] wrapper = { initial };
        GuiClassDisplay.drawFloat(field.getName(), wrapper);
        if(wrapper[0] != initial)
            field.set(object, wrapper[0]);
    }

    @EditType(Vector2f.class)
    private void vector2fEdit(Object value, Object object, Field field) {
        Vector2f vec = (Vector2f) value;
        GuiClassDisplay.drawVec2(field.getName(), vec);
    }
    @EditType(Vector3f.class)
    private void vector3fEdit(Object value, Object object, Field field) {
        Vector3f vec = (Vector3f) value;
        GuiClassDisplay.drawVec3(field.getName(), vec);
    }

    @EditType(Vector4f.class)
    private void vector4fEdit(Object value, Object object, Field field) {
        ImGui.text(field.getName() + ": ");
        ImGui.sameLine();

        Vector4f vec = (Vector4f) value;
        float[] wrapper = { vec.x, vec.y, vec.z, vec.w };
        if(ImGui.dragFloat4("##", wrapper)) {
            vec.set(wrapper);
        }
    }

    @EditType(value = Sprite.class, handler = SpritePicker.class)
    private void spriteEdit() {}

    @EditType(value = Transform.class, handler = TransformEditorHandler.class)
    private void transformEdit() {}

    @EditType(value = Colour.class, handler = ColourPickerEditorHandler.class)
    private void colourEdit() {}

    @EditType(value = BodyType.class, handler = BodyTypeEditorHandler.class)
    private void bodyTypeEdit() {}
}
