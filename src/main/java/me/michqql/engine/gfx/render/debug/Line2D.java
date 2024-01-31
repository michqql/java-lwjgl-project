package me.michqql.engine.gfx.render.debug;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
    private Vector2f from, to;
    private Vector3f color;
    private boolean alwaysDraw;
    private long lifetime;
    private long startTime;

    public Line2D(Vector2f from, Vector2f to, Vector3f color) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.alwaysDraw = true;
    }

    public Line2D(Vector2f from, Vector2f to, Vector3f color, long lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifetime = lifetime;
    }

    public long beginFrame() {
        if(alwaysDraw) return 1L;

        if(startTime == 0)
            startTime = System.currentTimeMillis();
        return startTime - System.currentTimeMillis() + lifetime;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }
}
