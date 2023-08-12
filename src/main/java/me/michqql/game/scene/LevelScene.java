package me.michqql.game.scene;

import me.michqql.game.gfx.shader.Shader;
import me.michqql.game.gfx.texture.Texture;
import me.michqql.game.util.Time;

public class LevelScene extends Scene {

    protected Shader shader;
    protected Texture texture;

    @Override
    public void init() {
        shader = Shader.getShader("default.glsl");
        if(shader == null) {
            System.out.println("WARNING: Shader is null");
            return;
        }

        texture = Texture.getTexture("test.png");
        if(texture == null) {
            System.out.println("WARNING: Texture is null");
            return;
        }

        shader.prepareUploads(uploader -> {
            uploader.matrix4f("uProjMatrix", camera.getProjectionMatrix());
            uploader.matrix4f("uViewMatrix", camera.getViewMatrix());
            uploader.floatValue("uTime", Time.getTime());
            uploader.texture("uTexture", 0, texture);
        });
    }

    @Override
    public void update(float deltaTime) {
        //camera.getPosition().x -= deltaTime * 50f;
        shader.useShader();
    }
}
