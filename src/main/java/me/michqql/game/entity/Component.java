package me.michqql.game.entity;

public abstract class Component {

    private GameObject parentGameObject = null;

    public void start() {
    }

    public abstract void update(float deltaTime);

    public GameObject getParentGameObject() {
        return parentGameObject;
    }

    void setGameObjectParent(GameObject parent) {
        this.parentGameObject = parent;
    }
}
