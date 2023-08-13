package me.michqql.game.gfx.render;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.shader.Shader;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private final List<RenderBatch> batches = new ArrayList<>();
    private final Shader shader;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        for(RenderBatch batch : batches) {
            if(batch.isFull())
                continue;

            batch.addSpriteRenderer(spriteRenderer);
            return;
        }

        // SpriteRenderer was not added to a batch, so create a new batch
        RenderBatch batch = new RenderBatch(shader, MAX_BATCH_SIZE);
        batch.start();
        batches.add(batch);
        batch.addSpriteRenderer(spriteRenderer);
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
