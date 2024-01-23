package me.michqql.game.gfx.shader;

import java.util.HashMap;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;

public class ShaderVariables {

    // Static start
    private static final HashMap<String, Integer> VAR_TYPE_TO_SIZE_MAP = new HashMap<>();

    static {
        VAR_TYPE_TO_SIZE_MAP.put("float", 1);
        VAR_TYPE_TO_SIZE_MAP.put("vec2",  2);
        VAR_TYPE_TO_SIZE_MAP.put("vec3",  3);
        VAR_TYPE_TO_SIZE_MAP.put("vec4",  4);
    }

    public static void addVarType(String varType, int size) {
        VAR_TYPE_TO_SIZE_MAP.put(varType, size);
    }
    // Static end

    private final LinkedList<Variable> variables = new LinkedList<>();

    public void addVariable(String type) {
        int offset = 0; // The offset of this variable
        if(!variables.isEmpty()) {
            Variable last = variables.getLast();
            offset = last.offset() + last.size() * Float.BYTES; // need offset in bytes
        }

        Integer size = VAR_TYPE_TO_SIZE_MAP.get(type);
        if(size == null) {
            throw new RuntimeException("[ShaderVariables] Unknown variable type: " + type);
        }

        variables.addLast(new Variable(size, offset));
    }

    public void initVertexAttributes() {
        final int vertexInBytes = getVertexSize() * Float.BYTES;

        for(int i = 0; i < variables.size(); i++) {
            Variable var = variables.get(i);

            glVertexAttribPointer(i, var.size(), GL_FLOAT, false, vertexInBytes, var.offset());
            glEnableVertexAttribArray(i);
        }
    }

    public void enableVertexAttributes() {
        for(int i = 0; i < variables.size(); i++) {
            glEnableVertexAttribArray(i);
        }
    }

    public void disableVertexAttributes() {
        for(int i = 0; i < variables.size(); i++) {
            glDisableVertexAttribArray(i);
        }
    }

    private int getVertexSize() {
        int vertexSize = 0;
        for(Variable var : variables) {
            vertexSize += var.size();
        }
        return vertexSize;
    }

    record Variable(int size, int offset) {}
}
