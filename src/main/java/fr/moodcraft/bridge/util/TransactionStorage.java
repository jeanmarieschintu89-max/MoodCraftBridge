package fr.moodcraft.bridge.util;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionStorage {

    private static File file;
    private static FileConfiguration config;

    // 🔒 thread-safe
    private static final Map<String, List<String>> data = new ConcurrentHashMap<>();

    private static final int MAX_LOGS = 50;

    // =========================
    // INIT
    // =========================
    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "transactions.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        load();

        // 🔁 auto-save toutes les 2 minutes
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(),
                TransactionStorage::save,
                20L * 120,
                20L * 120
        );
    }

    // =========================
    // LOAD
    // =========================
    public static void load() {

        data.clear();

        for (String key : config.getKeys(false)) {
            data.put(key, new ArrayList<>(config.getStringList(key)));
        }
    }

    // =========================
    // SAVE
    // =========================
    public static void save() {

        if (config == null) return;

        for (String key : data.keySet()) {
            config.set(key, data.get(key));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ADD TRANSACTION
    // =========================
    public static void add(String uuid, String log) {

        data.compute(uuid, (k, list) -> {

            if (list == null) list = new ArrayList<>();

            list.add(log);

            // 🔥 limite
            if (list.size() > MAX_LOGS) {
                list.remove(0);
            }

            return list;
        });
    }

    // =========================
    // GET
    // =========================
    public static List<String> get(String uuid) {
        return new ArrayList<>(data.getOrDefault(uuid, Collections.emptyList()));
    }
}