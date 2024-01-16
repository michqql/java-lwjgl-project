package me.michqql.game.scene;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.render.Renderer;
import me.michqql.game.gfx.shader.Shader;
import me.michqql.game.gfx.texture.Texture;
import me.michqql.game.gfx.texture.TextureAtlas;
import me.michqql.game.util.Time;

public class LevelScene extends Scene {

    protected TextureAtlas atlas;

    @Override
    public void init() {
        super.init();

        Texture texture = Texture.REGISTRY.get("spritesheet.png");
        this.atlas = TextureAtlas.getTextureAtlas(texture, 32, 32);
    }

    @Override
    public void firstInit() {
        GameObject gameObject = new GameObject("Object");
        gameObject.getTransform().getPosition().set(100, 100);
        gameObject.getTransform().getScale().set(1, 1);
        gameObject.addComponent(new SpriteRenderer(atlas.getSprite(0)));
        addGameObject(gameObject);
    }

    private int index = 0;
    private float accumulatedDt = 0f;

    @Override
    public void update(float deltaTime) {
        accumulatedDt += deltaTime;
        if(accumulatedDt > 1) {
            accumulatedDt = 0;
            index++;
            forEachGameObject(go -> {
                go.getComponent(SpriteRenderer.class)
                        .setSprite(atlas.getSprite(index % atlas.getNumberOfSprites()));
            });
        }

        forEachGameObject(go -> {
            SpriteRenderer sr = go.getComponent(SpriteRenderer.class);
            float offX = 16, offY = 16;
            if(sr != null && sr.getSprite() != null) {
                offX = (go.getTransform().getScale().x) / 2;
                offY = (go.getTransform().getScale().y) / 2;
            }
            go.getTransform().getPosition().set(camera.getOrthographicX() - offX,
                    camera.getOrthographicY() - offY);
        });

        super.update(deltaTime);
    }
}
