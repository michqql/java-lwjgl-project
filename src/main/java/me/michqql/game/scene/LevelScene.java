package me.michqql.game.scene;

import me.michqql.game.entity.GameObject;
import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.gfx.render.Renderer;
import me.michqql.game.gfx.shader.Shader;
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

        int xOffset = 10;
        int yOffset = 10;
        float totalWidth = 600 - xOffset * 2f;
        float totalHeight = 300 - yOffset * 2f;
        float sizeX = totalWidth / 100f;
        float sizeY = totalHeight / 100f;

        for(int x = 0; x < 100; x++) {
            for(int y = 0; y < 100; y++) {
                float xPos = xOffset + x * sizeX;
                float yPos = yOffset + y * sizeY;

                GameObject gameObject = new GameObject("Object(" + x + "," + y + ")");
                gameObject.getTransform().getPosition().set(xPos, yPos);
                gameObject.getTransform().getScale().set(sizeX, sizeY);
                gameObject.addComponent(new SpriteRenderer(new Vector4f(
                        xPos / totalWidth,
                        yPos / totalHeight,
                        1f, 1f
                )));

                addGameObject(gameObject);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
