package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InputManager {

    private static final Map<UUID, String> inputs = new HashMap<>();
    private static final Map<UUID, BukkitTask> timeouts = new HashMap<>();

    // =========================
    // ⏳ WAIT + TIMEOUT AUTO
    // =========================
    public static void wait(Player p, String type) {

        UUID uuid = p.getUniqueId();

        // 🔥 reset ancien état proprement
        clear(p);

        inputs.put(uuid, type);

        // 🔒 metadata propre
        p.setMetadata("input_active", new FixedMetadataValue(Main.getInstance(), true));

        // ⏱ timeout sécurisé
        BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {

            if (!has(p)) return;

            clear(p);

            p.sendMessage("§c⏳ Temps écoulé. Opération annulée.");

        }, 20L * 30);

        timeouts.put(uuid, task);
    }

    // =========================
    // 🔍 HAS
    // =========================
    public static boolean has(Player p) {
        return inputs.containsKey(p.getUniqueId());
    }

    // =========================
    // 📥 GET
    // =========================
    public static String get(Player p) {
        return inputs.get(p.getUniqueId());
    }

    // =========================
    // ❌ REMOVE (alias pour compat)
    // =========================
    public static void remove(Player p) {
        clear(p);
    }

    // =========================
    // ❌ CLEAR
    // =========================
    public static void clear(Player p) {

        UUID uuid = p.getUniqueId();

        inputs.remove(uuid);

        // 🔥 cancel timeout proprement
        if (timeouts.containsKey(uuid)) {
            timeouts.get(uuid).cancel();
            timeouts.remove(uuid);
        }

        // 🔥 remove metadata safe
        if (p.hasMetadata("input_active")) {
            p.removeMetadata("input_active", Main.getInstance());
        }
    }

    // =========================
    // 🧠 DEBUG / CHECK
    // =========================
    public static boolean isActive(Player p) {
        return p.hasMetadata("input_active");
    }
}