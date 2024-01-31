package me.michqql.engine.gfx.render;

import me.michqql.engine.gfx.shader.Shader;

import static org.lwjgl.opengl.GL30.*;

public class PickingTexture {

    // Static start
    private static Shader pickingShader;

    static {
        pickingShader = Shader.REGISTRY.get("picking_shader.glsl");
        if(pickingShader == null) {
            throw new RuntimeException("[PickingTexture] Static shader creation error");
        }
    }
    // Static end

    private final int width, height;

    private int fboId;
    private int pickingTextureId;
    private int depthTextureId;

    public PickingTexture(int width, int height) {
        if(!init(width, height)) {
            throw new RuntimeException("Error initialising picking texture");
        }

        this.width = width;
        this.height = height;
    }

    private boolean init(int width, int height) {
        // Generate frame buffer
        this.fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Create the texture to render to
        this.pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        // Setup texture params
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // x direction wrap
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // y direction wrap
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // Upload empty image
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pickingTextureId, 0);
        glEnable(GL_TEXTURE_2D);

        // Create the depth texture object
        this.depthTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTextureId);
        // Skip params and upload empty texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0);

        // Disable the reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            return false;

        // Unbind texture and frame buffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return true;
    }

    public void enableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboId);
    }

    public void disableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public float[] readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

        return pixels;
    }

    public Shader getPickingShader() {
        return pickingShader;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
