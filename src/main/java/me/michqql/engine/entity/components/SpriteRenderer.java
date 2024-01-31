package me.michqql.engine.entity.components;

import me.michqql.engine.entity.Component;
import me.michqql.engine.entity.Transform;
import me.michqql.engine.gfx.texture.Sprite;
import me.michqql.engine.util.Colour;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private final Colour colour;
    private Sprite sprite;

    private transient Transform lastTransform;
    private transient boolean dirty = true;

    public SpriteRenderer() {
        this.colour = new Colour();
    }

    public SpriteRenderer(Vector4f colour) {
        this.colour = new Colour(colour);
    }

    public SpriteRenderer(Sprite sprite) {
        this.colour = new Colour(1f, 1f, 1f, 1f);
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

    public void setDirty() {
        dirty = true;
    }

    public void setClean() {
        dirty = false;
    }
}
