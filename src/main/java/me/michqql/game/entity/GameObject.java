package me.michqql.game.entity;

import me.michqql.game.entity.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private final String name;
    private final Transform transform = new Transform();
    private final List<Component> componentList = new ArrayList<>();
    private int zIndex;

    public GameObject(String name) {
        this.name = name;
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
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
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
        this.componentList.add(component);
        component.setGameObjectParent(this);
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
}
