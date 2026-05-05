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
    // 📦 GET INSTANCE
    // =========================
    public static TransferBuilder get(Player p) {
        return cache.computeIfAbsent(p.getUniqueId(), k -> new TransferBuilder());
    }

    // =========================
    // 🎯 TARGET
    // =========================
    public static void setTarget(Player p, UUID target) {
        if (target == null) return;
        get(p).target = target;
    }

    public static UUID getTarget(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());
        return (b != null) ? b.target : null;
    }

    public static boolean hasTarget(Player p) {
        return getTarget(p) != null;
    }

    // =========================
    // 💰 AMOUNT
    // =========================
    public static void setAmount(Player p, double amount) {

        if (amount <= 0) return;

        get(p).amount = amount;
    }

    public static double getAmount(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());
        return (b != null) ? b.amount : 0;
    }

    public static boolean hasAmount(Player p) {
        return getAmount(p) > 0;
    }

    // =========================
    // ⚙ ACTION
    // =========================
    public static void setAction(Player p, Action action) {
        if (action == null) return;
        get(p).action = action;
    }

    public static Action getAction(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());
        return (b != null) ? b.action : null;
    }

    public static boolean hasAction(Player p) {
        return getAction(p) != null;
    }

    // =========================
    // 🧹 CLEAR
    // =========================
    public static void clear(Player p) {
        cache.remove(p.getUniqueId());
    }

    // =========================
    // 🧠 STATE CHECK
    // =========================
    public static boolean isComplete(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());

        return b != null
                && b.action != null
                && b.amount > 0
                && (b.action != Action.PLAYER_TRANSFER || b.target != null);
    }

    // =========================
    // 🧨 DEBUG (optionnel)
    // =========================
    public static String debug(Player p) {
        TransferBuilder b = cache.get(p.getUniqueId());

        if (b == null) return "NULL";

        return "Action=" + b.action +
                ", Amount=" + b.amount +
                ", Target=" + b.target;
    }
}