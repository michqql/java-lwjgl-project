package me.michqql.engine.gfx.render;

import me.michqql.engine.entity.GameObject;
import me.michqql.engine.entity.components.SpriteRenderer;
import me.michqql.engine.gfx.shader.Shader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    // Static start
    public static Renderer getDefaultRenderer() {
        Shader shader = Shader.REGISTRY.get("default.glsl");
        if(shader == null) {
            throw new RuntimeException("[Renderer] Static default renderer creation: Default shader does not exist");
        }

        return new Renderer(shader);
    }
    private static Shader tempShader;

    public static void setTempShader(@Nullable Shader shader) {
        tempShader = shader;
    }
    // Static end

    private final int MAX_BATCH_SIZE = 1000;
    public final List<RenderBatch> batches = new ArrayList<>();
    private final Shader shader;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        // Try to find a batch that will accept this SpriteRenderer, otherwise we will need to create a new batch
        for(RenderBatch batch : batches) {
            if(batch.isFull() || batch.getZIndex() != spriteRenderer.getParentGameObject().getTransform().getZIndex())
                continue;

            batch.addSpriteRenderer(spriteRenderer);
            return;
        }

        // SpriteRenderer was not added to a batch, so create a new batch
        // The SpriteRenderer may not have been added to a batch for the following reasons:
        // - all the batches were full
        // - no batch for a sprite with this z index
        RenderBatch batch = new RenderBatch(shader, MAX_BATCH_SIZE,
                spriteRenderer.getParentGameObject().getTransform().getZIndex());
        batch.start();
        batches.add(batch);
        batch.addSpriteRenderer(spriteRenderer);

        // Sort the batches array (based on z index)
        Collections.sort(batches);
    }

    public void remove(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) {
            remove(spriteRenderer);
        }
    }

    private void remove(SpriteRenderer spriteRenderer) {
        for(RenderBatch batch : batches) {
            if(batch.removeSpriteRenderer(spriteRenderer))
                break;
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            // Temp shader might be null, in which case the batch will use its default renderer
            batch.render(tempShader);
        }
    }


}
