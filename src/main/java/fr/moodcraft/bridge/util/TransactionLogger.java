package fr.moodcraft.bridge.util;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionLogger {

    private static File file;
    private static FileConfiguration config;

    // 🔒 thread-safe
    private static final Map<String, List<String>> logs = new ConcurrentHashMap<>();

    private static final int MAX_LOGS = 100; // limite par joueur

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd/MM HH:mm");

    // =========================
    // 🔧 INIT
    // =========================
    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "transactions.yml");

        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }

        config = YamlConfiguration.loadConfiguration(file);

        // 🔄 LOAD
        for (String player : config.getKeys(false)) {
            logs.put(player, new ArrayList<>(config.getStringList(player)));
        }

        // 🔁 AUTO SAVE (toutes les 2 min)
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(),
                TransactionLogger::save,
                20L * 120,
                20L * 120
        );
    }

    // =========================
    // 📝 LOG SIMPLE
    // =========================
    public static void log(String player, String type, double amount) {
        log(player, type, amount, null);
    }

    // =========================
    // 📝 LOG AVEC CIBLE
    // =========================
    public static void log(String player, String type, double amount, String target) {

        String date = FORMAT.format(new Date());

        String line = (target != null)
                ? date + "||" + type + " -> " + target + "||" + format(amount)
                : date + "||" + type + "||" + format(amount);

        logs.computeIfAbsent(player, k -> new ArrayList<>()).add(line);

        // 🔥 limite taille
        List<String> list = logs.get(player);
        if (list.size() > MAX_LOGS) {
            list.remove(0); // supprime le plus ancien
        }
    }

    // =========================
    // 📜 GET LOGS
    // =========================
    public static List<String> getAll(String player) {
        return new ArrayList<>(logs.getOrDefault(player, Collections.emptyList()));
    }

    // =========================
    // 💾 SAVE
    // =========================
    public static void save() {

        if (config == null) return;

        for (String player : logs.keySet()) {
            config.set(player, logs.get(player));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 💰 FORMAT SAFE
    // =========================
    private static String format(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }
}