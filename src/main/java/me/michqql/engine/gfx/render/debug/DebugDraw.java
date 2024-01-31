package me.michqql.engine.gfx.render.debug;

import me.michqql.engine.gfx.camera.Camera;
import me.michqql.engine.gfx.shader.Shader;
import me.michqql.engine.util.MathUtil;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static final int MAX_LINES = 500;
    private static final List<Line2D> LINE_2D_LIST = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static final float[] VERTEX_ARRAY = new float[MAX_LINES * 6 * 2];
    private static final Shader DEBUG_SHADER = Shader.REGISTRY.get("debug_line.glsl");

    private static int vaoId, vboId;
    private static boolean initialised;

    public static void init() {
        // Generate the vertex-array-object (VAO)
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create the vbo and allocate memory
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, VERTEX_ARRAY.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes
        // Position (x,y,z)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // Colour (r,g,b)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        //glLineWidth(3.0f);

        initialised = true;
    }

    public static void beginFrame() {
        if(!initialised) init();

        // Remove expired lines
        LINE_2D_LIST.removeIf(line2D -> line2D.beginFrame() <= 0);
    }

    public static void draw(Camera camera) {
        if(LINE_2D_LIST.isEmpty())
            return;

        int index = 0;
        for(Line2D line : LINE_2D_LIST) {
            // Load position 1 and colour 1
            VERTEX_ARRAY[index] = line.getFrom().x;
            VERTEX_ARRAY[index + 1] = line.getFrom().y;
            VERTEX_ARRAY[index + 2] = -10f;

            VERTEX_ARRAY[index + 3] = line.getColor().x;
            VERTEX_ARRAY[index + 4] = line.getColor().y;
            VERTEX_ARRAY[index + 5] = line.getColor().z;

            // Move index by 6 floats
            index += 6;

            // Load position 2 and colour 2
            VERTEX_ARRAY[index] = line.getTo().x;
            VERTEX_ARRAY[index + 1] = line.getTo().y;
            VERTEX_ARRAY[index + 2] = -10f;

            VERTEX_ARRAY[index + 3] = line.getColor().x;
            VERTEX_ARRAY[index + 4] = line.getColor().y;
            VERTEX_ARRAY[index + 5] = line.getColor().z;

            // Move index by 6 floats again, for next for loop iteration
            index += 6;
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(VERTEX_ARRAY, 0, index));

        DEBUG_SHADER.useShader();
        DEBUG_SHADER.prepareUploads(shaderUploader -> {
            shaderUploader.matrix4f("uProj", camera.getProjectionMatrix());
            shaderUploader.matrix4f("uView", camera.getViewMatrix());
        });

        // Bind the VAO
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawArrays(GL_LINES, 0, LINE_2D_LIST.size());

        // Disable location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        DEBUG_SHADER.detach();

    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, true, 0);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, boolean alwaysDraw, long lifetime) {
        if(LINE_2D_LIST.size() >= MAX_LINES)
            return;

        Line2D line = alwaysDraw ?
                new Line2D(from, to, color) :
                new Line2D(from, to, color, lifetime);
        LINE_2D_LIST.add(line);
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation, Vector3f color,
                                boolean alwaysDraw, long lifetime) {
        final Vector2f halfSize = new Vector2f(size).mul(0.5f);
        Vector2f min = new Vector2f(center).sub(halfSize);
        Vector2f max = new Vector2f(center).add(halfSize);

        Vector2f[] vertices = {
                min,
                new Vector2f(min.x, max.y),
                max,
                new Vector2f(max.x, min.y)
        };

        if(rotation % 90.0f > 0.005) {
            for(Vector2f vertex : vertices) {
                MathUtil.rotate(vertex, rotation, center);
            }
        }

        /*
         * 0------1
         * |      |
         * |      |
         * 3------2
         */
        addLine2D(vertices[0], vertices[1], color, alwaysDraw, lifetime);
        addLine2D(vertices[1], vertices[2], color, alwaysDraw, lifetime);
        addLine2D(vertices[2], vertices[3], color, alwaysDraw, lifetime);
        addLine2D(vertices[3], vertices[0], color, alwaysDraw, lifetime);

    }

    public static void addCircle2D(Vector2f center, float radius, int accuracy, Vector3f color, boolean alwaysDraw,
                                   long lifetime) {
        if(accuracy < 3 || accuracy > 100)
            accuracy = 8;

        Vector2f[] points = new Vector2f[accuracy];
        float increment = 360f / points.length;
        float angle = 0f;

        Vector2f tmp;
        final Vector2f zeroZero = new Vector2f();
        for(int i = 0; i < points.length; i++) {
            tmp = new Vector2f(radius, 0);
            MathUtil.rotate(tmp, angle, zeroZero);
            points[i] = tmp.add(center);

            angle += increment;

            if(i > 0)
                addLine2D(points[i - 1], points[i], color, alwaysDraw, lifetime);
        }

        addLine2D(points[points.length - 1], points[0], color, alwaysDraw, lifetime);
    }
}
