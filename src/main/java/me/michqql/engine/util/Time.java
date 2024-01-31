package me.michqql.engine.util;

public class Time {

    private static final long TIME_STARTED;

    static {
        TIME_STARTED = System.nanoTime();
    }

    public static float getTime() {
        return (float) ((System.nanoTime() - TIME_STARTED) * 1E-9);
    }
}
