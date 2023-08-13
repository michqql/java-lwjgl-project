package me.michqql.game.gfx.render;

import me.michqql.game.entity.components.SpriteRenderer;
import me.michqql.game.entity.components.Transform;
import me.michqql.game.gfx.shader.Shader;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {

    private static final Vector2f[] VERTEX_OFFSETS = {
            new Vector2f(1f, 1f), // top-right
            new Vector2f(1f, 0f), // bottom-right
            new Vector2f(0f, 0f), // bottom-left
            new Vector2f(0f, 1f)  // top-left
    };

    // Vertex layout:
    // Position (x, y),        Colour (r, g, b, a)
    // float, float,           float, float, float, float
    private static final int POSITION_SIZE = 2;
    private static final int POSITION_OFFSET = 0;

    private static final int COLOUR_SIZE = 4;
    private static final int COLOUR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = POSITION_SIZE + COLOUR_SIZE;
    private static final int VERTEX_SIZE_IN_BYTES = VERTEX_SIZE * Float.BYTES;

    private final SpriteRenderer[] sprites;
    private final float[] vertices;
    private int numSprites;
    private boolean full;

    private int vaoId, vboId;
    private final int maxBatchSize;
    private Shader shader;

    public RenderBatch(Shader shader, int maxBatchSize) {
        this.shader = shader;
        this.maxBatchSize = maxBatchSize;
        this.sprites = new SpriteRenderer[maxBatchSize];

        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
    }

    public void start() {
        // Generate and bind vertex array object
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Allocate space for the vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable buffer attribute pointers
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOUR_SIZE, GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, COLOUR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void render() {
        // re-buffer all data every frame (for now)
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        shader.useShader();

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    public void addSpriteRenderer(SpriteRenderer spriteRenderer) {
        // Get index and add render object
        int index = numSprites;
        sprites[index] = spriteRenderer;
        numSprites++;

        // Add properties to vertices array
        loadVertexProperties(spriteRenderer, index);

        // Check if array is full
        if(numSprites >= maxBatchSize) {
            full = true;
        }
    }

    public boolean isFull() {
        return full;
    }

    private void loadVertexProperties(SpriteRenderer spriteRenderer, int index) {
        // Find offset (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Transform transform = spriteRenderer.getParentGameObject().getTransform();
        Vector4f colour = spriteRenderer.getColour();
        for(int i = 0; i < VERTEX_OFFSETS.length; i++) {
            Vector2f offsets = VERTEX_OFFSETS[i];

            // Load the position
            vertices[offset] = transform.getPosition().x() + offsets.x() * transform.getScale().x();
            vertices[offset + 1] = transform.getPosition().y() +
                    offsets.y() * transform.getScale().y();

            // Load the colour
            vertices[offset + 2] = colour.x();
            vertices[offset + 3] = colour.y();
            vertices[offset + 4] = colour.z();
            vertices[offset + 5] = colour.w();

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad, 3 per triangle
        int[] elements = new int[6 * maxBatchSize];
        for(int i = 0; i < maxBatchSize; i++) {
            int offsetArrayIndex = 6 * i;
            int offset = 4 * i;

            // Counter clock-wise
            // 3, 2, 0, 0, 2, 1
            elements[offsetArrayIndex] = offset + 3;
            elements[offsetArrayIndex + 1] = offset + 2;
            elements[offsetArrayIndex + 2] = offset;
            elements[offsetArrayIndex + 3] = offset;
            elements[offsetArrayIndex + 4] = offset + 2;
            elements[offsetArrayIndex + 5] = offset + 1;
        }

        return elements;
    }
}
