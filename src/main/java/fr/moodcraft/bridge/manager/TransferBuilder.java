package fr.moodcraft.bridge.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransferBuilder {

    private static final Map<UUID, TransferBuilder> cache = new HashMap<>();

    private Action action;
    private UUID target;
    private double amount;

    public enum Action {
        DEPOSIT,
        WITHDRAW,
        PLAYER_TRANSFER,
        IBAN_TRANSFER
    }

    // =========================
    // 📦 GET
    // =========================
    public static TransferBuilder get(Player p) {
        return cache.computeIfAbsent(p.getUniqueId(), k -> new TransferBuilder());
    }

    // =========================
    // 🎯 TARGET
    // =========================
    public static void setTarget(Player p, UUID target) {
        get(p).target = target;
    }

    public static UUID getTarget(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());
        return b != null ? b.target : null;
    }

    // =========================
    // 💰 AMOUNT
    // =========================
    public static void setAmount(Player p, double amount) {

        if (amount <= 0) return; // 🔒 sécurité

        get(p).amount = amount;
    }

    public static double getAmount(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());
        return b != null ? b.amount : 0;
    }

    // =========================
    // ⚙ ACTION
    // =========================
    public static void setAction(Player p, Action action) {
        get(p).action = action;
    }

    public static Action getAction(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());
        return b != null ? b.action : null;
    }

    // =========================
    // 🧹 CLEAR
    // =========================
    public static void clear(Player p) {
        cache.remove(p.getUniqueId());
    }

    // =========================
    // 🧠 HAS (important)
    // =========================
    public static boolean has(Player p) {
        return cache.containsKey(p.getUniqueId());
    }
}