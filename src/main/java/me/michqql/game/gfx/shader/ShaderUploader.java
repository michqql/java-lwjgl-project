package me.michqql.game.gfx.shader;

import me.michqql.game.gfx.texture.Texture;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderUploader {

    private final Shader shader;

    public ShaderUploader(Shader shader) {
        this.shader = shader;
    }

    public void intValue(String varName, int i) {
        int varLocation = getVariableLocation(varName);
        glUniform1i(varLocation, i);
    }

    public void floatValue(String varName, float f) {
        int varLocation = getVariableLocation(varName);
        glUniform1f(varLocation, f);
    }

    public void matrix4f(String varName, Matrix4f matrix4f) {
        int varLocation = getVariableLocation(varName);

        // capacity of 16 because a matrix 4f is a 4x4 float array, so 16 floats
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix4f.get(buffer);

        glUniformMatrix4fv(varLocation, false, buffer);
    }

    public void matrix3f(String varName, Matrix3f matrix3f) {
        int varLocation = getVariableLocation(varName);

        // capacity of 16 because a matrix 3f is a 3x3 float array, so 16 floats
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix3f.get(buffer);

        glUniformMatrix3fv(varLocation, false, buffer);
    }

    public void matrix2f(String varName, Matrix2f matrix2f) {
        int varLocation = getVariableLocation(varName);

        // capacity of 16 because a matrix 2f is a 2x2 float array, so 16 floats
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix2f.get(buffer);

        glUniformMatrix2fv(varLocation, false, buffer);
    }

    public void vector4f(String varName, Vector4f vector4f) {
        int varLocation = getVariableLocation(varName);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        vector4f.get(buffer);

        glUniform4fv(varLocation, buffer);
    }

    public void vector3f(String varName, Vector3f vector3f) {
        int varLocation = getVariableLocation(varName);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        vector3f.get(buffer);

        glUniform3fv(varLocation, buffer);
    }

    public void vector2f(String varName, Vector2f vector2f) {
        int varLocation = getVariableLocation(varName);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(2);
        vector2f.get(buffer);

        glUniform2fv(varLocation, buffer);
    }

    public void texture(String varName, int slot, Texture texture) {
        intValue(varName, slot);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
    }

    private int getVariableLocation(String varName) {
        return glGetUniformLocation(shader.getShaderProgramId(), varName);
    }
}
