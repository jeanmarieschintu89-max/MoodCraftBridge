package fr.moodcraft.bridge.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmountInputManager {

    //
    // 🧠 TYPES
    //

    public enum Type {

        DEPOSIT,

        WITHDRAW,

        PLAYER_TRANSFER
    }

    //
    // 💾 INPUTS
    //

    private static final Map<UUID, Type>
            inputs = new HashMap<>();

    // =========================
    // ⏳ WAIT
    // =========================

    public static void wait(Player p,
                            Type type) {

        UUID uuid =
                p.getUniqueId();

        inputs.put(
                uuid,
                type
        );

        //
        // 🔥 SYNC INPUT
        //

        InputManager.wait(
                p,
                "amount"
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
    // 📥 GET TYPE
    // =========================

    public static Type getType(Player p) {

        return inputs.get(
                p.getUniqueId()
        );
    }

    // =========================
    // ❌ CLEAR
    // =========================

    public static void clear(Player p) {

        UUID uuid =
                p.getUniqueId();

        inputs.remove(uuid);

        //
        // 🔥 CLEAR INPUT
        //

        InputManager.clear(p);
    }
}