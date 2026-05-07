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

    //
    // 🔢 AMOUNT
    //

    private static final Map<UUID, Integer>
            amounts = new HashMap<>();

    // =========================
    // 📦 ITEM
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

    public static Material getItem(
            UUID uuid
    ) {

        return items.get(uuid);
    }

    // =========================
    // 🔢 AMOUNT
    // =========================

    public static void setAmount(
            UUID uuid,
            int amount
    ) {

        amounts.put(
                uuid,
                amount
        );
    }

    public static int getAmount(
            UUID uuid
    ) {

        return amounts.getOrDefault(
                uuid,
                0
        );
    }

    // =========================
    // ❌ CLEAR
    // =========================

    public static void clear(
            UUID uuid
    ) {

        items.remove(uuid);

        amounts.remove(uuid);
    }
}