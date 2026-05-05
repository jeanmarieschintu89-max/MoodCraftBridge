package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReputationHistoryManager {

    private static File file;
    private static FileConfiguration config;

    private static final Map<UUID, List<String>> history = new HashMap<>();
    private static final int MAX = 30;

    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "reputation_history.yml");

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
            history.put(UUID.fromString(key),
                    new ArrayList<>(config.getStringList(key)));
        }

        log("Chargé (" + history.size() + " joueurs)");
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String now() {
        return new SimpleDateFormat("dd/MM HH:mm").format(new Date());
    }

    private static String name(UUID uuid) {
        var p = Bukkit.getOfflinePlayer(uuid);
        return p.getName() != null ? p.getName() : uuid.toString().substring(0, 8);
    }

    public static void add(UUID uuid, int amount, String reason) {

        List<String> list = history.computeIfAbsent(uuid, k -> new ArrayList<>());

        String color = amount >= 0 ? "§a+" : "§c";
        String line = color + amount + " §8• §7" + reason + " §8(" + now() + ")";

        list.add(0, line);

        if (list.size() > MAX) {
            list.remove(list.size() - 1);
        }

        config.set(uuid.toString(), list);
        save();
    }

    public static List<String> get(UUID uuid) {
        return history.getOrDefault(uuid, new ArrayList<>());
    }

    public static List<String> getPage(UUID uuid, int page, int size) {

        List<String> list = get(uuid);

        int from = (page - 1) * size;
        int to = Math.min(from + size, list.size());

        if (from >= list.size()) return new ArrayList<>();

        return list.subList(from, to);
    }

    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§d[REP] " + msg);
    }
}