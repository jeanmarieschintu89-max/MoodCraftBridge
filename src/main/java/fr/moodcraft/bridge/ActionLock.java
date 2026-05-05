package fr.moodcraft.bridge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionLock {

    private static final Map<UUID, Long> map = new HashMap<>();

    public static boolean isLocked(UUID uuid, long delay) {

        long now = System.currentTimeMillis();

        if (!map.containsKey(uuid)) {
            map.put(uuid, now);
            return false;
        }

        long last = map.get(uuid);

        if (now - last < delay) return true;

        map.put(uuid, now);
        return false;
    }
}