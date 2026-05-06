
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

    //
    // 🧠 INPUTS
    //

    private static final Map<UUID, String>
            inputs = new HashMap<>();

    //
    // ⏱ TIMEOUTS
    //

    private static final Map<UUID, BukkitTask>
            timeouts = new HashMap<>();

    //
    // 💾 DATA
    //

    private static final Map<UUID, String>
            data = new HashMap<>();

    // =========================
    // ⏳ WAIT + TIMEOUT AUTO
    // =========================

    public static void wait(Player p,
                            String type) {

        UUID uuid =
                p.getUniqueId();

        //
        // 🔥 RESET ANCIEN ÉTAT
        //

        clear(p);

        inputs.put(
                uuid,
                type
        );

        //
        // 🔒 METADATA
        //

        p.setMetadata(
                "input_active",

                new FixedMetadataValue(
                        Main.getInstance(),
                        true
                )
        );

        //
        // ⏱ TIMEOUT
        //

        BukkitTask task =
                Bukkit.getScheduler()
                        .runTaskLater(

                                Main.getInstance(),

                                () -> {

                                    if (!has(p))
                                        return;

                                    clear(p);

                                    p.sendMessage(
                                            "§c⏳ Temps écoulé. Opération annulée."
                                    );

                                },

                                20L * 30
                        );

        timeouts.put(
                uuid,
                task
        );
    }

    // =========================
    // 🔍 HAS
    // =========================

    public static boolean has(Player p) {

        return inputs.containsKey(
                p.getUniqueId()
        );
    }

    // =========================
    // 📥 GET
    // =========================

    public static String get(Player p) {

        return inputs.get(
                p.getUniqueId()
        );
    }

    // =========================
    // 💾 SET DATA
    // =========================

    public static void setData(Player p,
                               String value) {

        data.put(
                p.getUniqueId(),
                value
        );
    }

    // =========================
    // 📥 GET DATA
    // =========================

    public static String getData(Player p) {

        return data.get(
                p.getUniqueId()
        );
    }

    // =========================
    // 🧹 CLEAR DATA
    // =========================

    public static void clearData(Player p) {

        data.remove(
                p.getUniqueId()
        );
    }

    // =========================
    // ❌ REMOVE
    // =========================

    public static void remove(Player p) {

        clear(p);
    }

    // =========================
    // ❌ CLEAR
    // =========================

    public static void clear(Player p) {

        UUID uuid =
                p.getUniqueId();

        //
        // 🧹 REMOVE INPUT
        //

        inputs.remove(uuid);

        //
        // 🧹 REMOVE DATA
        //

        data.remove(uuid);

        //
        // 🔥 CANCEL TIMEOUT
        //

        if (timeouts.containsKey(uuid)) {

            timeouts.get(uuid)
                    .cancel();

            timeouts.remove(uuid);
        }

        //
        // 🔥 REMOVE METADATA
        //

        if (p.hasMetadata("input_active")) {

            p.removeMetadata(
                    "input_active",
                    Main.getInstance()
            );
        }
    }

    // =========================
    // 🧠 DEBUG
    // =========================

    public static boolean isActive(Player p) {

        return p.hasMetadata(
                "input_active"
        );
    }
}