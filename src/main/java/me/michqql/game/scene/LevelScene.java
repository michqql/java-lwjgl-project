package me.michqql.game.scene;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.render.Renderer;
import me.michqql.game.gfx.shader.Shader;
import me.michqql.game.gfx.texture.Texture;
import me.michqql.game.gfx.texture.TextureAtlas;
import me.michqql.game.util.Time;

public class LevelScene extends Scene {

    protected Shader shader;
    protected TextureAtlas atlas;
    protected GameObject gameObject;

    @Override
    public void init() {
        shader = Shader.REGISTRY.get("default.glsl");
        if(shader == null) {
            System.out.println("WARNING: Shader is null");
            return;
        }

        shader.prepareUploads(uploader -> {
            uploader.matrix4f("uProjMatrix", camera.getProjectionMatrix());
            uploader.matrix4f("uViewMatrix", camera.getViewMatrix());
            uploader.floatValue("uTime", Time.getTime());
        });

        renderer = new Renderer(shader);

        Texture texture = Texture.REGISTRY.get("spritesheet.png");
        this.atlas = TextureAtlas.getTextureAtlas(texture, 32, 32);

        this.gameObject = new GameObject("Object");
        gameObject.getTransform().getPosition().set(100, 100);
        gameObject.getTransform().getScale().set(256, 256);
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
            gameObject.getComponent(SpriteRenderer.class)
                    .setSprite(atlas.getSprite(index % atlas.getNumberOfSprites()));
        }

        gameObject.getTransform().getPosition().x += 50 * deltaTime;

        super.update(deltaTime);
    }
}
