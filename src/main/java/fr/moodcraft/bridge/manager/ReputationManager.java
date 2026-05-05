package fr.moodcraft.bridge.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import fr.moodcraft.bridge.Main;

public class ReputationManager {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Integer> cache = new HashMap<>();

    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "reputation.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            cache.put(key, config.getInt(key));
        }
    }

    // =========================
    // 🔍 GET
    // =========================
    public static int get(String uuid) {
        return cache.getOrDefault(uuid, 0);
    }

    // =========================
    // ⚙️ SET
    // =========================
    public static void set(String uuid, int value) {
        value = Math.max(0, value);
        cache.put(uuid, value);
        config.set(uuid, value);
        save();
    }

    // =========================
    // ➕ ADD
    // =========================
    public static void add(String uuid, int value) {
        set(uuid, get(uuid) + value);
    }

    public static void reset(String uuid) {
        set(uuid, 0);
    }

    // =========================
    // ✨ AJOUT STYLÉ + HISTO
    // =========================
    public static void addRepStyled(Player p, int value, String reason) {

        String id = p.getUniqueId().toString();

        int old = get(id);
        int now = Math.max(0, old + value);

        set(id, now);

        // 📜 HISTORIQUE
        ReputationHistoryManager.add(p.getUniqueId(), value, reason);

        // 🎨 MESSAGE STYLÉ
        String color = value >= 0 ? "§a+" : "§c";
        String arrow = value >= 0 ? "⬆" : "⬇";

        p.sendMessage("§8§m-----------------------------");
        p.sendMessage("§6✦ §fRéputation mise à jour");
        p.sendMessage("§7Variation: " + color + value + " §7" + arrow);
        p.sendMessage("§7Raison: §e" + reason);
        p.sendMessage("§7Total: §e" + now + " §8(" + getRank(now) + "§8)");
        p.sendMessage("§8§m-----------------------------");

        p.playSound(
                p.getLocation(),
                value >= 0
                        ? org.bukkit.Sound.ENTITY_PLAYER_LEVELUP
                        : org.bukkit.Sound.ENTITY_VILLAGER_NO,
                1, 1
        );
    }

    // =========================
    // 🏆 CLASSEMENT
    // =========================
    public static LinkedHashMap<String, Integer> getTop(int limit) {

        return cache.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll
                );
    }

    // =========================
    // 🧠 RANG (MOODCRAFT STYLE)
    // =========================
    public static String getRank(int rep) {

        if (rep >= 200) return "§6Maître du Marché";
        if (rep >= 120) return "§dÉlite Commerciale";
        if (rep >= 80) return "§bInfluenceur Éco";
        if (rep >= 50) return "§2Pilier du Marché";
        if (rep >= 25) return "§aMarchand Actif";
        if (rep >= 10) return "§fCommerçant";

        return "§7Visiteur";
    }

    // =========================
    // 🏷️ PREFIX (OPTION BONUS)
    // =========================
    public static String getPrefix(int rep) {
        return "§8[" + getRank(rep) + "§8]";
    }

    // =========================
    // 💾 SAVE
    // =========================
    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}