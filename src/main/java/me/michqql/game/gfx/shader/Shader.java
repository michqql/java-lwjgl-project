package me.michqql.game.gfx.shader;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Shader {

    private static final String SHADER_PATH = "./assets/shaders";
    private static final File SHADER_DIRECTORY = new File(SHADER_PATH);

    private final File shaderFile;

    private int shaderProgramId;
    private int vaoId; // vertex array object

    private final ShaderUploader uploader;
    private Consumer<ShaderUploader> preparedUpload = null;

    private final float[] vertexArray = {
            // position (x, y, z),   colour (r, g, b, a)           // UV coordinates
            100.5f,   0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,     1, 0, // bottom right [0]
              0.5f, 100.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,     0, 1, // top left     [1]
            100.5f, 100.5f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f,     1, 1, // top right    [2]
              0.5f,   0.5f, 0.0f,      1.0f, 1.0f, 1.0f, 1.0f,     0, 0  // bottom left   [3]
    };

    // Must be counter-clockwise
    private final int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private Shader(String fileName) throws FileNotFoundException {
        this.shaderFile = new File(SHADER_DIRECTORY, fileName);
        if(!shaderFile.exists() || !shaderFile.isFile()) {
            throw new FileNotFoundException("Shader file with name " + fileName + " could not be " +
                    "found!");
        }

        uploader = new ShaderUploader(this);

        compileShader();
        generateVaoVboEbo();
    }

    public void prepareUploads(Consumer<ShaderUploader> preparedUpload) {
        this.preparedUpload = preparedUpload;
    }

    public void useShader() {
        // Bind shader program
        glUseProgram(shaderProgramId);

        // Upload uniforms
        if(preparedUpload != null) {
            preparedUpload.accept(uploader);
        }

        // Bind VAO
        glBindVertexArray(vaoId);

        // Enable pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void detach() {
        glUseProgram(0);
    }

    private void compileShader() {
        Map<String, CharSequence> map = parseShaderFile();

        // Compile the vertex shader
        int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderId, map.get("vertex"));
        glCompileShader(vertexShaderId);
        checkShaderCompiledSuccessfully(vertexShaderId);

        // Compile the fragment shader
        int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderId, map.get("fragment"));
        glCompileShader(fragmentShaderId);
        checkShaderCompiledSuccessfully(fragmentShaderId);

        // Create the shader program
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexShaderId);
        glAttachShader(shaderProgramId, fragmentShaderId);
        glLinkProgram(shaderProgramId);
        checkProgramLinkedSuccessfully(shaderProgramId);
    }

    private void generateVaoVboEbo() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip(); // flipped to read mode
        // Create vbo and upload vertex buffer
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip(); // flipped to read mode
        // Create ebo
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int coloursSize = 4;
        int uvSize = 2;
        int floatSizeBytes = Float.BYTES; // 4
        int vertexSizeBytes = (positionsSize + coloursSize + uvSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, coloursSize, GL_FLOAT, false, vertexSizeBytes,
                positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, coloursSize, GL_FLOAT, false, vertexSizeBytes,
                (positionsSize + coloursSize) * floatSizeBytes);
        glEnableVertexAttribArray(2);
    }

    private Map<String, CharSequence> parseShaderFile() {
        Map<String, CharSequence> shaderTypeToProgramMap = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(shaderFile))) {
            String type;
            String line = reader.readLine();
            StringBuilder builder = null;

            while(line != null) {
                if(line.startsWith("#type")) {
                    type = line.substring("#type ".length());
                    builder = new StringBuilder();
                    shaderTypeToProgramMap.put(type, builder);
                    line = reader.readLine();
                    continue;
                }

                if(builder != null)
                    builder.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ShaderCompileException("IOException when reading shader file: " + shaderFile.getName());
        }

        return shaderTypeToProgramMap;
    }

    private void checkShaderCompiledSuccessfully(int shaderId) {
        int success = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH);
            String infoLog = glGetShaderInfoLog(shaderId, len);
            System.out.println("Error log (" + len + "): " + infoLog);
            throw new ShaderCompileException(infoLog);
        }
    }

    private void checkProgramLinkedSuccessfully(int programId) {
        int success = glGetProgrami(programId, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(programId, GL_INFO_LOG_LENGTH);
            String infoLog = glGetProgramInfoLog(programId, len);
            throw new ShaderCompileException(infoLog);
        }
    }

    // Package-private
    int getShaderProgramId() {
        return shaderProgramId;
    }

    public static Shader getShader(String fileName) {
        try {
            return new Shader(fileName);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
