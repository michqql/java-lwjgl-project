package me.michqql.engine.util;

import org.joml.Vector2f;

public class MathUtil {

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x() - origin.x();
        float y = vec.y() - origin.y();

        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double xPrime = (x * cos) - (y * sin);
        double yPrime = (x * sin) + (y * cos);

        xPrime += origin.x();
        yPrime += origin.y();

        vec.x = (float) xPrime;
        vec.y = (float) yPrime;
    }

    public static float lerp(float current, float target, float t) {
        return current + (target - current) * t;
    }
}
