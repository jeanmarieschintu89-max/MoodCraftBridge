package fr.moodcraft.bridge.market;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class MarketEventManager {

    private static final Random RANDOM = new Random();

    private static final String HEADER = "§8----- §6✦ §aMood§6Craft §fÉconomie ✦ §8-----";
    private static final String FOOTER = "§8-----------------------------";
    private static final String DETAIL = "§8• §7";
    private static final String NO_EVENT = "§8• §7Aucun événement économique actif";

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
        if (!hasActiveEvent()) return NO_EVENT;
        return "§6✦ " + active.displayName() + " §8• §7" + active.remainingMinutes() + " min";
    }

    public static String itemLine(String item) {
        if (!appliesTo(item)) return NO_EVENT;
        return "§6✦ " + active.displayName() + " §8• §7" + active.remainingMinutes() + " min";
    }

    private static void tick() {
        if (!enabled()) return;

        if (active != null && active.isExpired()) {
            broadcastEnd(active);
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
        broadcastStart(active, minutes);
    }

    private static void broadcastStart(ActiveEvent event, int minutes) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(HEADER);
        Bukkit.broadcastMessage("§6✦ §fÉvénement économique : " + event.displayName());
        Bukkit.broadcastMessage(DETAIL + "Effet : " + event.type().description(event.targetLabel()));
        Bukkit.broadcastMessage(DETAIL + "Durée : §e" + minutes + " minutes");
        Bukkit.broadcastMessage(DETAIL + "Surveillez les prix, le marché bouge.");
        Bukkit.broadcastMessage(FOOTER);
        Bukkit.broadcastMessage("");
    }

    private static void broadcastEnd(ActiveEvent event) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(HEADER);
        Bukkit.broadcastMessage("§a✔ §fÉvénement terminé : " + event.displayName());
        Bukkit.broadcastMessage(DETAIL + "Le marché retrouve son rythme naturel.");
        Bukkit.broadcastMessage(FOOTER);
        Bukkit.broadcastMessage("");
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
        SHORTAGE("Pénurie", "§c", false, 0.18, 1.15),
        DEMAND("Demande spéciale", "§b", false, 0.12, 1.25),
        SURPLUS("Surproduction", "§a", false, -0.15, 1.10),
        CRASH("Crash local", "§4", false, -0.22, 1.20),
        UNSTABLE("Marché instable", "§d", true, 0.0, 1.45),
        GOLDEN_AGE("Âge d'or", "§6", true, 0.08, 1.15);

        private final String label;
        private final String color;
        private final boolean global;
        private final double priceBias;
        private final double pressureMultiplier;

        EventType(String label, String color, boolean global, double priceBias, double pressureMultiplier) {
            this.label = label;
            this.color = color;
            this.global = global;
            this.priceBias = priceBias;
            this.pressureMultiplier = pressureMultiplier;
        }

        private boolean global() {
            return global;
        }

        private String styledLabel() {
            return color + label;
        }

        private String description(String target) {
            return switch (this) {
                case SHORTAGE -> "§f" + target + " §7devient plus rare et prend temporairement de la valeur.";
                case DEMAND -> "§7La demande de §f" + target + " §7augmente.";
                case SURPLUS -> "§f" + target + " §7baisse temporairement à cause d'une grosse production.";
                case CRASH -> "§f" + target + " §7subit une forte baisse temporaire.";
                case UNSTABLE -> "§7Tous les prix réagissent plus fortement.";
                case GOLDEN_AGE -> "§7Le marché reçoit une légère hausse générale.";
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
            return type.styledLabel() + (type.global() ? "" : " §7de §f" + targetLabel());
        }

        private String targetLabel() {
            if ("*".equals(item)) return "tout le marché";
            return item == null ? "inconnu" : item.toLowerCase(Locale.ROOT);
        }
    }
}
