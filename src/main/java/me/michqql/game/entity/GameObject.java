package me.michqql.game.entity;

import me.michqql.game.util.collection.FastEmptyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GameObject {

    private static final FastEmptyList<Component> FAST_EMPTY_DIRTY_COMPONENT_LIST = new FastEmptyList<>();

    private final String name;
    private final UUID uuid;
    private final Transform transform;
    private final List<Component> componentList = new ArrayList<>();
    private transient final List<Component> dirtyComponents = new ArrayList<>();
    private int zIndex;

    public GameObject(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.transform = new Transform();
    }

    public GameObject(String name, UUID uuid, Transform transform, int zIndex) {
        this.name = name;
        this.uuid = uuid;
        this.transform = transform;
        this.zIndex = zIndex;
    }

    public void update(float dt) {
        for(Component component : componentList) {
            component.update(dt);
        }
    }

    public void start() {
        for(Component component : componentList) {
            component.start();
        }
        dirtyComponents.clear();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    /**
     * <p>
     *     Creates an unmodifiable view of the actual list. Therefore, changes do not reflect on this list.
     *     Should be called sparingly and results should be cached to avoid creating an unmodifiable list
     *     many times.
     * </p>
     * @return an unmodifiable view of the components list
     */
    public List<Component> getComponentList() {
        return Collections.unmodifiableList(componentList);
    }

    public <T extends Component> T getComponent(Class<T> componentType) {
        for(Component component : componentList) {
            if(componentType.isAssignableFrom(component.getClass())) {
                try {
                    return componentType.cast(component);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        // Component not found
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentType) {
        componentList.removeIf(component -> componentType.isAssignableFrom(component.getClass()));
    }

    public void addComponent(Component component) {
        this.dirtyComponents.add(component);
        this.componentList.add(component);
        component.setGameObjectParent(this);
    }

    public List<Component> getAndClearDirtyComponents() {
        if(dirtyComponents.isEmpty()) return FAST_EMPTY_DIRTY_COMPONENT_LIST;

        List<Component> dirtyCopy = new ArrayList<>(dirtyComponents);
        dirtyComponents.clear();
        return dirtyCopy;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
}
