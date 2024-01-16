package me.michqql.game.scene.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import me.michqql.game.entity.Component;
import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.render.debug.DebugDraw;
import me.michqql.game.gfx.texture.Sprite;
import me.michqql.game.input.MouseInput;
import me.michqql.game.scene.GuiDisplayScene;
import me.michqql.game.scene.Scene;
import me.michqql.game.scene.editor.custom.CustomEditorHandler;
import me.michqql.game.scene.editor.custom.SpritePicker;
import me.michqql.game.util.Prefab;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.reflections.Reflections;

import java.lang.reflect.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class EditorScene extends Scene implements GuiDisplayScene {

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
        Class<?> type = EditorScene.class;
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

    private static void callEditMethod(EditorScene scene, Object value, Object object, Field field) throws InvocationTargetException, IllegalAccessException {
        if(!loaded) lazyLoad();

        Object callingObject = scene;
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

    // Level editor
    private List<Sprite> spriteList;
    private float displayScale = 1.0f;
    private GameObject holdingObject = null;
    private boolean gridLines;
    private int gridSize = 32;
    private boolean gridSnapping;

    // Game object editor
    private GameObject selectedGameObject = null;
    private final Map<Class<?>, Field[]> cachedFields = new HashMap<>();
    private boolean addingComponent;
    private int selectedComponentIndex = -1;

    @Override
    public void postInit() {
        // Get game object
        forEachGameObject(this::setSelectedGameObject);
        spriteList = Sprite.getSpriteCache();
    }

    @Override
    public void update(float deltaTime) {
        if(holdingObject != null) {
            if(MouseInput.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                holdingObject = null;
                return;
            }

            final Vector2f pos = holdingObject.getTransform().getPosition();
            pos.set(camera.getOrthographicX(), camera.getOrthographicY());
            if(gridSnapping) {
                pos.sub(pos.x() % gridSize, pos.y() % gridSize);
            } else {
                SpriteRenderer sr = holdingObject.getComponent(SpriteRenderer.class);
                float offX = 16, offY = 16;
                if(sr != null && sr.getSprite() != null) {
                    offX = (holdingObject.getTransform().getScale().x) / 2;
                    offY = (holdingObject.getTransform().getScale().y) / 2;
                }

                pos.sub(offX, offY);
            }
        } else {
            // Holding object is null, check if user clicked a game object
            float x = camera.getOrthographicX(), y = camera.getOrthographicY();
            
        }

        super.update(deltaTime);
    }

    @Override
    public void display() {
        editLevel();
        editGameObject();
        drawGridLines();
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
            ImGui.sliderInt("Grid Size", sliderValue, 1, 256);
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

    private void editGameObject() {
        if(selectedGameObject == null) {
            ImGui.begin("Editing: none");
            ImGui.text("Select a game object to edit properties");
            ImGui.end();
        } else {
            if(cachedFields.isEmpty())
                setupCachedFields();

            ImGui.begin("Editing: " + selectedGameObject.getName());

            // If the user is adding a new component to the selected game object, call the method
            if(addingComponent) {
                displayAddComponentGui();
            }

            // Displays the fields for the game object fields
            ImGui.text("Game Object");
            displayFields(selectedGameObject);

            ImGui.separator();
            ImGui.text("Components");

            // Add component button
            if(ImGui.button("+") && !addingComponent) {
                addingComponent = true;
                selectedComponentIndex = -1;
            }

            for(Component component : selectedGameObject.getComponentList()) {
                if(ImGui.collapsingHeader(component.getClass().getSimpleName())) {
                    ImGui.indent();
                    displayFields(component);

                    // Remove component button
                    if(ImGui.button("-")) {
                        selectedGameObject.removeComponent(component.getClass());
                        break;
                    }

                    ImGui.unindent();
                }
            }

            ImGui.end();
        }
    }

    private void drawGridLines() {
        if(!gridLines)
            return;

        Vector2f cameraPos = camera.getPosition();
        Vector2f projSize = camera.getProjectionSize();

        int firstX = ((int) (cameraPos.x() / gridSize) * gridSize);
        int firstY = ((int) (cameraPos.y() / gridSize) * gridSize);

        int numVertLines = (int) projSize.x() / gridSize;
        int numHorLines = (int) projSize.y() / gridSize;

        // Draw vertical lines
        for(int i = 0; i < numVertLines; i++) {
            int x = firstX + (gridSize * i);
            DebugDraw.addLine2D(new Vector2f(x, 0), new Vector2f(x, projSize.y()), new Vector3f());
        }

        // Draw horizontal lines
        for(int i = 0; i < numHorLines; i++) {
            int y = firstY + (gridSize * i);
            DebugDraw.addLine2D(new Vector2f(0, y), new Vector2f(projSize.x, y), new Vector3f());
        }
    }

    private void displayAddComponentGui() {
        if(selectedGameObject == null) {
            addingComponent = false;
            return;
        }

        ImGui.beginChild("Add component");
        ImGui.text("Add component to game object: " + selectedGameObject.getName());

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
                        selectedGameObject.addComponent(component);
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

    public void setSelectedGameObject(GameObject selectedGameObject) {
        this.selectedGameObject = selectedGameObject;
    }

    public GameObject getSelectedGameObject() {
        return selectedGameObject;
    }

    private void displayFields(Object obj) {
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
        Field[] fields = selectedGameObject.getClass().getDeclaredFields();
        cachedFields.put(selectedGameObject.getClass(), fields);

        // Cache the fields of the components of this game object
        for(Component component : selectedGameObject.getComponentList()) {
            fields = component.getClass().getDeclaredFields();
            cachedFields.put(component.getClass(), fields);
        }
    }

    @EditType(String.class)
    private void stringEdit(Object value, Object object, Field field) throws IllegalAccessException {
        ImString wrapper = new ImString((String) value);
        if(ImGui.inputText(field.getName() + ": ", wrapper, ImGuiInputTextFlags.CallbackResize))
            field.set(object, wrapper.get());
    }

    @EditType(int.class)
    private void intEdit(Object value, Object object, Field field) throws IllegalAccessException {
        int[] wrapper = { (int) value };
        if(ImGui.dragInt(field.getName() + ": ", wrapper))
            field.set(object, wrapper[0]);
    }

    @EditType(Vector4f.class)
    private void vector4fEdit(Object value, Object object, Field field) {
        Vector4f vec = (Vector4f) value;
        float[] wrapper = { vec.x, vec.y, vec.z, vec.w };
        if(ImGui.dragFloat4(field.getName() + ": ", wrapper)) {
            vec.set(wrapper);
        }
    }

    @EditType(value = Sprite.class, handler = SpritePicker.class)
    private void spriteEdit(Object value, Object object, Field field) {}
}
