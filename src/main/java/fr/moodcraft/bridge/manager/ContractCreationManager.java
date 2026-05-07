package fr.moodcraft.bridge.manager;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContractCreationManager {

    //
    // 📦 ITEM
    //

    private static final Map<UUID, Material>
            items = new HashMap<>();

    // =========================
    // 📦 SET ITEM
    // =========================

    public static void setItem(
            UUID uuid,
            Material material
    ) {

        items.put(
                uuid,
                material
        );
    }

    // =========================
    // 📦 GET ITEM
    // =========================

    public static Material getItem(
            UUID uuid
    ) {

        return items.get(uuid);
    }

    // =========================
    // ❌ CLEAR
    // =========================

    public static void clear(
            UUID uuid
    ) {

        items.remove(uuid);
    }
}