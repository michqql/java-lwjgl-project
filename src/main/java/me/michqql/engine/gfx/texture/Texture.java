package me.michqql.engine.gfx.texture;

import me.michqql.engine.util.registry.Registry;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    public static final Registry<Texture> REGISTRY = new Registry<>(s -> {
        try {
            return new Texture(s);
        } catch (TextureLoadException e) {
            e.printStackTrace();
            return null;
        }
    });
    private static final String TEXTURE_PATH = "./assets/textures";

    static {
        // Create the textures directory
        new File(TEXTURE_PATH);
    }

    private final String fileName;
    private final int textureId;

    private final int width, height;

    private Texture(String fileName) throws TextureLoadException {
        this.fileName = fileName;
        final String fullTexturePath = TEXTURE_PATH + "/" + fileName;

        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = stbi_load(fullTexturePath, width, height, channels, 0);

        if(image != null) {
            int format = GL_RGB;
            int channelInfo = channels.get(0);
            if(channelInfo == 4) {
                format = GL_RGBA;
            }

            this.width = width.get(0);
            this.height = height.get(0);
            glTexImage2D(GL_TEXTURE_2D, 0, format, this.width, this.height, 0, format,
                    GL_UNSIGNED_BYTE, image);
        } else {
            throw new TextureLoadException("Unable to load texture: " + fileName);
        }

        stbi_image_free(image); // free the memory to avoid a leak
    }

    public Texture(int width, int height) {
        this.fileName = "null";

        this.width = width;
        this.height = height;

        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, 0);
    }

    public String getFileName() {
        return fileName;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
