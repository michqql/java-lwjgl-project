package me.michqql.game.scene;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.render.Renderer;
import me.michqql.game.gfx.shader.Shader;
import me.michqql.game.gfx.texture.Texture;
import me.michqql.game.util.Time;
import org.joml.Vector4f;

public class LevelScene extends Scene {

    protected Shader shader;

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

        Texture texture = Texture.REGISTRY.get("test.png");

        GameObject gameObject = new GameObject("Object");
        gameObject.getTransform().getPosition().set(100, 100);
        gameObject.getTransform().getScale().set(256, 256);
        gameObject.addComponent(new SpriteRenderer(texture));

        addGameObject(gameObject);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
