package fr.moodcraft.bridge.manager;

import org.bukkit.configuration.file.*;
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

    public static int get(String uuid) {
        return cache.getOrDefault(uuid, 0);
    }

    public static void set(String uuid, int value) {
        value = Math.max(0, value);
        cache.put(uuid, value);
        config.set(uuid, value);

        // 🔥 optimisation: évite spam disque
        BukkitRunnableSave.schedule();
    }

    public static void add(String uuid, int value) {
        set(uuid, get(uuid) + value);
    }

    public static void reset(String uuid) {
        set(uuid, 0);
    }

    public static void addRepStyled(Player p, int value, String reason) {

        String id = p.getUniqueId().toString();

        int old = get(id);
        int now = Math.max(0, old + value);

        set(id, now);

        if (value > 0) {
            p.sendMessage("§a+" + value + " réputation §8» §7" + reason);
        } else {
            p.sendMessage("§c" + value + " réputation §8» §7" + reason);
        }
    }

    // 🏆 CLASSEMENT
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

    // 🧠 RANG
    public static String getRank(int rep) {
        if (rep >= 500) return "§6Elite";
        if (rep >= 200) return "§aConfirmé";
        if (rep >= 50) return "§eApprenti";
        return "§7Débutant";
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}