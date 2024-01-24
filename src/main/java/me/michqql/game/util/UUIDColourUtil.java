package me.michqql.game.util;

import java.util.UUID;

public class UUIDColourUtil {

    public static float[] colourFromUUID(UUID uuid) {
        int hash = uuid.hashCode();
        float r = (float) ((hash >> 16) & 0xFF) / 255;
        float g = (float) ((hash >> 8) & 0xFF) / 255;
        float b = (float) (hash & 0xFF) / 255;
        return new float[] { r, g, b };
    }
}
