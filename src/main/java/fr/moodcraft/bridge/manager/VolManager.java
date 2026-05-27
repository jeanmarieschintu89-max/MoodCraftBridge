package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class VolManager {

    private static Main plugin;
    private static File file;
    private static YamlConfiguration data;
    private static NamespacedKey key;
    private static int task = -1;

    private VolManager() {}

    public static void init(Main main) {
        plugin = main;
        key = new NamespacedKey(plugin, "vol_time_seconds");
        file = new File(plugin.getDataFolder(), "vol-data.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de creer vol-data.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
        startTimer();
    }

    public static void shutdown() {
        if (task != -1) Bukkit.getScheduler().cancelTask(task);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isActive(player.getUniqueId())) stopFlight(player, true);
        }
        save();
    }

    public static boolean isEnabled() {
        return plugin != null && plugin.getConfig().getBoolean("vol.enabled", false);
    }

    public static NamespacedKey getItemKey() {
        return key;
    }

    public static boolean isActive(UUID uuid) {
        return data.getBoolean(path(uuid, "active"), false);
    }

    public static long remaining(UUID uuid) {
        return Math.max(0, data.getLong(path(uuid, "remaining"), 0));
    }

    public static void add(UUID uuid, long seconds) {
        if (seconds <= 0) return;
        data.set(path(uuid, "remaining"), remaining(uuid) + seconds);
        save();
    }

    public static void remove(UUID uuid, long seconds) {
        if (seconds <= 0) return;
        data.set(path(uuid, "remaining"), Math.max(0, remaining(uuid) - seconds));
        save();
    }

    public static void reset(UUID uuid) {
        data.set("players." + uuid, null);
        save();
    }

    public static void toggle(Player player) {
        if (!isEnabled()) {
            player.sendMessage("§c✖ §fLe système de vol MoodCraft n'est pas activé.");
            return;
        }
        if (isActive(player.getUniqueId())) {
            stopFlight(player, false);
            return;
        }
        if (remaining(player.getUniqueId()) <= 0) {
            player.sendMessage("§c✖ §fTu n'as pas de temps de vol disponible.");
            return;
        }
        startFlight(player);
    }

    public static void startFlight(Player player) {
        UUID uuid = player.getUniqueId();
        data.set(path(uuid, "active"), true);
        data.set(path(uuid, "started"), now());
        player.setAllowFlight(true);
        player.setFlying(true);
        save();
        player.sendMessage("§a✔ §fVol activé. §7Temps restant: §e" + format(remaining(uuid)));
    }

    public static void stopFlight(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        long left = liveRemaining(uuid);
        data.set(path(uuid, "remaining"), left);
        data.set(path(uuid, "active"), false);
        data.set(path(uuid, "started"), 0);
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        save();
        if (!silent) player.sendMessage("§c✖ §fVol désactivé. §7Temps restant: §e" + format(left));
    }

    public static void consume(Player player, ItemStack item) {
        if (!isEnabled()) {
            player.sendMessage("§c✖ §fLe système de vol MoodCraft n'est pas activé.");
            return;
        }
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        Integer seconds = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (seconds == null || seconds <= 0) return;
        add(player.getUniqueId(), seconds);
        item.setAmount(item.getAmount() - 1);
        player.sendMessage("§a✔ §fTu as ajouté §e" + format(seconds) + " §fde vol. §7Total: §e" + format(remaining(player.getUniqueId())));
    }

    public static ItemStack item(long seconds) {
        ItemStack item = new ItemStack(org.bukkit.Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bBon de vol §f" + format(seconds));
        meta.setLore(java.util.List.of("§8Récompense MoodCraft", "", "§7Clic droit pour ajouter", "§e" + format(seconds) + " §7de vol", "", "§8Commande: §f/vol"));
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, Math.toIntExact(seconds));
        item.setItemMeta(meta);
        return item;
    }

    public static long parse(String input) {
        if (input == null || input.isBlank()) return -1;
        String v = input.trim().toLowerCase();
        try {
            if (v.endsWith("min")) return Long.parseLong(v.substring(0, v.length() - 3)) * 60;
            if (v.endsWith("m")) return Long.parseLong(v.substring(0, v.length() - 1)) * 60;
            if (v.endsWith("s")) return Long.parseLong(v.substring(0, v.length() - 1));
            return Long.parseLong(v) * 60;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String format(long seconds) {
        seconds = Math.max(0, seconds);
        long m = seconds / 60;
        long s = seconds % 60;
        if (m <= 0) return s + " sec";
        return s == 0 ? m + " min" : m + " min " + s + " sec";
    }

    private static long liveRemaining(UUID uuid) {
        long base = remaining(uuid);
        if (!isActive(uuid)) return base;
        long elapsed = Math.max(0, now() - data.getLong(path(uuid, "started"), now()));
        return Math.max(0, base - elapsed);
    }

    private static void startTimer() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!isEnabled()) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isActive(player.getUniqueId()) && liveRemaining(player.getUniqueId()) <= 0) {
                    data.set(path(player.getUniqueId(), "remaining"), 0);
                    data.set(path(player.getUniqueId(), "active"), false);
                    data.set(path(player.getUniqueId(), "started"), 0);
                    if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                    player.sendMessage("§c✖ §fTon temps de vol est terminé.");
                    save();
                }
            }
        }, 20L, 20L).getTaskId();
    }

    private static String path(UUID uuid, String key) {
        return "players." + uuid + "." + key;
    }

    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }

    private static void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder vol-data.yml: " + e.getMessage());
        }
    }
}
