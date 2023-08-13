package me.michqql.game.gfx.shader;

import me.michqql.game.util.registry.Registry;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    public static final Registry<Shader> REGISTRY = new Registry<>(fileName -> {
        try {
            return new Shader(fileName);
        } catch (FileNotFoundException e) {
            return null;
        }
    });
    private static final String SHADER_PATH = "./assets/shaders";
    private static final File SHADER_DIRECTORY = new File(SHADER_PATH);

    private final File shaderFile;

    private int shaderProgramId;

    private final ShaderUploader uploader;
    private Consumer<ShaderUploader> preparedUpload = null;

    private Shader(String fileName) throws FileNotFoundException {
        this.shaderFile = new File(SHADER_DIRECTORY, fileName);
        if(!shaderFile.exists() || !shaderFile.isFile()) {
            throw new FileNotFoundException("Shader file with name " + fileName + " could not be " +
                    "found!");
        }

        uploader = new ShaderUploader(this);

        compileShader();
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
}
