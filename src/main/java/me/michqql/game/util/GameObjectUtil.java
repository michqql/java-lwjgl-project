package me.michqql.game.util;

import me.michqql.game.entity.GameObject;
import me.michqql.game.gfx.render.PickingTexture;
import me.michqql.game.scene.Scene;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class GameObjectUtil {

    public static float[] colourFromUUID(@Nonnull UUID uuid) {
        int hash = uuid.hashCode();
        float r = (float) ((hash >> 16) & 0xFF) / 255;
        float g = (float) ((hash >> 8) & 0xFF) / 255;
        float b = (float) (hash & 0xFF) / 255;
        return new float[] { r, g, b };
    }

    @Nullable
    public static GameObject getClickedGameObject(@Nonnull Scene scene, @Nonnull PickingTexture pickingTexture,
                                                  int x, int y) {
        Map<float[], UUID> map = new TreeMap<>(Arrays::compare);
        scene.forEachGameObject(gameObject -> {
            UUID uuid = gameObject.getUuid();
            float[] arr = GameObjectUtil.colourFromUUID(uuid);
            map.put(arr, uuid);
        });

        float[] rgb = pickingTexture.readPixel(x, y);
        UUID uuid = map.get(rgb);
        if(uuid == null) {
            return null;
        } else {
            // Game object shouldn't be null here, but might be
            return scene.getGameObjectByUUID(uuid);
        }
    }
}
