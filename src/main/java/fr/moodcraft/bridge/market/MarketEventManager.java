package fr.moodcraft.bridge.market;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class MarketEventManager {

    private static final Random RANDOM = new Random();

    private static ActiveEvent active;
    private static long nextRollAt;
    private static int taskId = -1;

    private MarketEventManager() {}

    public static void start() {
        stop();
        scheduleNextRoll();
        taskId = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), MarketEventManager::tick, 20L * 30L, 20L * 30L).getTaskId();
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        active = null;
        nextRollAt = 0L;
    }

    public static boolean hasActiveEvent() {
        return active != null && !active.isExpired();
    }

    public static double priceBias(String item) {
        if (!appliesTo(item)) return 0.0;
        return active.type().priceBias;
    }

    public static double pressureMultiplier(String item) {
        if (!appliesTo(item)) return 1.0;
        return active.type().pressureMultiplier;
    }

    public static String globalLine() {
        if (!hasActiveEvent()) return "§7Aucun événement actif";
        return active.displayName() + " §8• §7" + active.remainingMinutes() + " min";
    }

    public static String itemLine(String item) {
        if (!appliesTo(item)) return "§7Aucun événement";
        return active.displayName() + " §8• §7" + active.remainingMinutes() + " min";
    }

    private static void tick() {
        if (!enabled()) return;

        if (active != null && active.isExpired()) {
            Bukkit.broadcastMessage("§8----- §6✦ Marché MoodCraft ✦ §8-----");
            Bukkit.broadcastMessage("§7L'événement économique §e" + active.displayName() + " §7est terminé.");
            Bukkit.broadcastMessage("§8-----------------------------");
            active = null;
            scheduleNextRoll();
            return;
        }

        if (active != null) return;
        if (System.currentTimeMillis() < nextRollAt) return;

        double chance = Main.getInstance().getConfig().getDouble("market-events.chance", 1.0);
        if (RANDOM.nextDouble() > chance) {
            scheduleNextRoll();
            return;
        }

        startRandomEvent();
    }

    private static void startRandomEvent() {
        List<String> items = new ArrayList<>(MarketState.base.keySet());
        if (items.isEmpty()) {
            scheduleNextRoll();
            return;
        }

        EventType[] types = EventType.values();
        EventType type = types[RANDOM.nextInt(types.length)];
        String item = type.global ? "*" : items.get(RANDOM.nextInt(items.size()));
        int minutes = Math.max(5, Main.getInstance().getConfig().getInt("market-events.duration-minutes", 30));

        active = new ActiveEvent(type, item, System.currentTimeMillis() + minutes * 60_000L);

        Bukkit.broadcastMessage("§8----- §6✦ Marché MoodCraft ✦ §8-----");
        Bukkit.broadcastMessage("§e★ §fÉvénement économique : " + active.displayName());
        Bukkit.broadcastMessage("§8• §7Effet : §f" + active.type().description(active.targetLabel()));
        Bukkit.broadcastMessage("§8• §7Durée : §e" + minutes + " minutes");
        Bukkit.broadcastMessage("§8-----------------------------");
    }

    private static boolean appliesTo(String item) {
        if (!hasActiveEvent()) return false;
        if (active.type().global()) return true;
        return item != null && item.equalsIgnoreCase(active.item());
    }

    private static void scheduleNextRoll() {
        int minutes = Math.max(1, Main.getInstance().getConfig().getInt("market-events.every-minutes", 60));
        nextRollAt = System.currentTimeMillis() + minutes * 60_000L;
    }

    private static boolean enabled() {
        return Main.getInstance().getConfig().getBoolean("market-events.enabled", true);
    }

    private enum EventType {
        SHORTAGE("Pénurie", false, 0.18, 1.15),
        DEMAND("Demande spéciale", false, 0.12, 1.25),
        SURPLUS("Surproduction", false, -0.15, 1.10),
        CRASH("Crash local", false, -0.22, 1.20),
        UNSTABLE("Marché instable", true, 0.0, 1.45),
        GOLDEN_AGE("Âge d'or", true, 0.08, 1.15);

        private final String label;
        private final boolean global;
        private final double priceBias;
        private final double pressureMultiplier;

        EventType(String label, boolean global, double priceBias, double pressureMultiplier) {
            this.label = label;
            this.global = global;
            this.priceBias = priceBias;
            this.pressureMultiplier = pressureMultiplier;
        }

        private boolean global() {
            return global;
        }

        private String description(String target) {
            return switch (this) {
                case SHORTAGE -> target + " prend temporairement de la valeur.";
                case DEMAND -> "La demande de " + target + " augmente.";
                case SURPLUS -> target + " baisse temporairement.";
                case CRASH -> target + " subit une forte baisse temporaire.";
                case UNSTABLE -> "Tous les prix réagissent plus fortement.";
                case GOLDEN_AGE -> "Le marché reçoit une légère hausse générale.";
            };
        }
    }

    private record ActiveEvent(EventType type, String item, long expiresAt) {
        private boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }

        private long remainingMinutes() {
            return Math.max(0L, (expiresAt - System.currentTimeMillis() + 59_999L) / 60_000L);
        }

        private String displayName() {
            return type.label + (type.global() ? "" : " de " + targetLabel());
        }

        private String targetLabel() {
            if ("*".equals(item)) return "tout le marché";
            return item == null ? "inconnu" : item.toLowerCase(Locale.ROOT);
        }
    }
}
