package me.michqql.game.gfx.texture;

import me.michqql.game.util.registry.Registry;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    public static final Registry<Texture> REGISTRY = new Registry<>(s -> {
        try {
            return new Texture(s);
        } catch (TextureLoadException e) {
            return null;
        }
    });
    private static final String TEXTURE_PATH = "./assets/textures";
    private static final File TEXTURE_DIRECTORY = new File(TEXTURE_PATH);

    private static final Map<String, Texture> TEXTURE_CACHE = new HashMap<>();

    private final int textureId;

    private final int width, height;

    private Texture(String fileName) throws TextureLoadException {
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

        stbi_set_flip_vertically_on_load(true);
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
