package me.michqql.engine.util;

import org.joml.Vector4f;

public class Colour extends Vector4f {

    public Colour() {}

    public Colour(Vector4f from) {
        set(from);
    }

    public Colour(float x, float y, float z, float w) {
        super(x, y, z, w);
    }
}
