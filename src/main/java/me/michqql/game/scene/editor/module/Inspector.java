package me.michqql.game.scene.editor.module;

import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import me.michqql.game.entity.Component;
import me.michqql.game.entity.GameObject;
import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.input.MouseInput;
import me.michqql.game.scene.editor.EditType;
import me.michqql.game.scene.editor.EditorScene;
import me.michqql.game.scene.editor.custom.CustomEditorHandler;
import me.michqql.game.scene.editor.custom.SpritePicker;
import me.michqql.game.util.UUIDColourUtil;
import org.joml.Vector4f;
import org.reflections.Reflections;

import java.lang.reflect.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Inspector implements EditorModule {

    // Static
    // Class, method to edit class type
    private static final Map<Class<?>, Method> EDITOR_METHOD_CACHE;
    // Class, handler wrapper
    private static final Map<Class<?>, CustomEditorHandler.HandlerWrapper> CUSTOM_HANDLER_CACHE;
    // Name, component class type
    private static final Map<String, Class<?>> CLASS_NAME_TO_COMPONENT_MAP;
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
        Reflections reflections = new Reflections("me.michqql.game.entity");
        Set<Class<? extends Component>> classes = reflections.getSubTypesOf(Component.class);
        if(classes != null) {
            for (Class<?> clazz : classes) {
                CLASS_NAME_TO_COMPONENT_MAP.put(clazz.getSimpleName(), clazz);
            }
        }

        // Ensures this method cant be called again
        loaded = true;
    }

    private static void callEditMethod(Object inspector, Object value, Object object,
                                       Field field) throws InvocationTargetException, IllegalAccessException {
        if(!loaded) lazyLoad();

        Object callingObject = inspector;
        Method method = EDITOR_METHOD_CACHE.get(field.getType());
        if(method == null) {
            // Check custom handler
            CustomEditorHandler.HandlerWrapper wrapper = CUSTOM_HANDLER_CACHE.get(field.getType());
            if(wrapper == null)
                return;

            callingObject = wrapper.getHandler();
            method = wrapper.getHandleMethod();
        }

        boolean access = method.canAccess(callingObject);
        method.setAccessible(true);

        try {
            method.invoke(callingObject, value, object, field);
        } finally {
            method.setAccessible(access);
        }
    }
    // Static end

    private final EditorScene editorScene;
    private final GameViewport gameViewport;
    private final PickingTexture pickingTexture;

    private GameObject selectedObject;
    private final Map<Class<?>, Field[]> cachedFields = new HashMap<>();
    private boolean addingComponent;
    private int selectedComponentIndex = -1;
    private long lastClickTime = 0;

    public Inspector(EditorScene editorScene, GameViewport gameViewport,
                     PickingTexture pickingTexture) {
        this.editorScene = editorScene;
        this.gameViewport = gameViewport;
        this.pickingTexture = pickingTexture;
    }

    @Override
    public void update(float dt) {
        if(MouseInput.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if(System.currentTimeMillis() - lastClickTime < 200)
                return;

            int x = (int) gameViewport.getScreenX();
            int y = (int) gameViewport.getScreenY();

            float[] rgb = pickingTexture.readPixel(x, y);
            Map<float[], UUID> map = getRGB2UUIDMap();
            UUID uuid = map.get(rgb);
            if(uuid == null) {
                setSelectedObject(null);
            } else {
                GameObject gameObject = editorScene.getGameObjectByUUID(uuid); // game object shouldn't be null here
                setSelectedObject(gameObject);
            }
            lastClickTime = System.currentTimeMillis();
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

            // If the user is adding a new component to the selected game object, call the method
            if(addingComponent) {
                displayAddComponentGui();
            }

            // Displays the fields for the game object fields
            ImGui.text("Game Object");
            drawFields(selectedObject);

            ImGui.separator();
            ImGui.text("Components");

            // Add component button
            ImGui.sameLine();
            if(ImGui.button("+") && !addingComponent) {
                addingComponent = true;
                selectedComponentIndex = -1;
            }

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
        ImGui.end();
    }

    private void displayAddComponentGui() {
        if(selectedObject == null) {
            addingComponent = false;
            return;
        }

        ImGui.beginChild("Add component");
        ImGui.text("Add component to game object: " + selectedObject.getName());

        Set<String> set = CLASS_NAME_TO_COMPONENT_MAP.keySet();
        String[] strings = new String[set.size()];
        if(selectedComponentIndex < 0 || selectedComponentIndex >= strings.length)
            selectedComponentIndex = 0;

        int index = 0;
        for(String s : set) {
            strings[index++] = s;
        }

        String currentItem = strings[selectedComponentIndex];
        if(ImGui.beginCombo("Component: ", currentItem, ImGuiComboFlags.HeightLarge)) {
            for(int i = 0; i < strings.length; i++) {
                boolean selected = currentItem.equals(strings[i]);
                if(ImGui.selectable(strings[i], selected)) {
                    currentItem = strings[i];
                    selectedComponentIndex = i;
                }
                if(selected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }

        if(ImGui.button("Add")) {
            Class<?> componentClass = CLASS_NAME_TO_COMPONENT_MAP.get(currentItem);
            if(componentClass != null) {
                try {
                    Constructor<?> constructor = componentClass.getDeclaredConstructor();
                    Object obj = constructor.newInstance();

                    if (obj instanceof Component component) {
                        // Which it should be!
                        selectedObject.addComponent(component);
                        addingComponent = false;
                    }

                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {

                }
            }
        }

        ImGui.sameLine();
        if(ImGui.button("Cancel")) {
            addingComponent = false;
        }

        ImGui.endChild();
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

    private Map<float[], UUID> getRGB2UUIDMap() {
        TreeMap<float[], UUID> map = new TreeMap<>(Arrays::compare);

        editorScene.forEachGameObject(gameObject -> {
            UUID uuid = gameObject.getUuid();
            float[] arr = UUIDColourUtil.colourFromUUID(uuid);
            map.put(arr, uuid);
        });

        return map;
    }

    public GameObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(GameObject selectedObject) {
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
    private void spriteEdit(Object value, Object object, Field field) {}
}
