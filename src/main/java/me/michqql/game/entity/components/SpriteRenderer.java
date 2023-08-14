package me.michqql.game.entity.components;

import me.michqql.game.entity.Component;
import me.michqql.game.gfx.texture.Sprite;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private final Vector4f colour;
    private Sprite sprite;

    private Transform lastTransform;
    private boolean dirty = true;

    public SpriteRenderer(Vector4f colour) {
        this.colour = colour;
    }

    public SpriteRenderer(Sprite sprite) {
        this.colour = new Vector4f(1f, 1f, 1f, 1f);
        this.sprite = sprite;
    }

    @Override
    public void start() {
        this.lastTransform = getParentGameObject().getTransform().copy();
    }

    @Override
    public void update(float deltaTime) {
        if(!lastTransform.equals(getParentGameObject().getTransform())) {
            getParentGameObject().getTransform().copy(lastTransform);
            dirty = true;
        }
    }

    public Vector4f getColour() {
        return colour;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.dirty = true;
    }

    public void setColour(Vector4f colour) {
        this.colour.set(colour);
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setClean() {
        dirty = false;
    }
}
