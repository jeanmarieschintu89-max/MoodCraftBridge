package fr.moodcraft.bridge.bank;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionManager {

    private static File file;
    private static FileConfiguration config;

    private static final Map<UUID, List<String>> history = new HashMap<>();
    private static final int MAX = 30;

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

        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            history.put(uuid, new ArrayList<>(config.getStringList(key)));
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

    private static void push(UUID uuid, String line) {
        List<String> list = history.computeIfAbsent(uuid, k -> new ArrayList<>());
        list.add(0, line);

        if (list.size() > MAX) {
            list.remove(list.size() - 1);
        }

        config.set(uuid.toString(), list);
    }

    // =========================
    // 💸 VIREMENT
    // =========================
    public static void transfer(UUID from, UUID to, double amount) {

        String date = now();

        String sender = "§c-" + amount + "€ §8→ §7" +
                Bukkit.getOfflinePlayer(to).getName() + " §8(" + date + ")";

        String receiver = "§a+" + amount + "€ §8← §7" +
                Bukkit.getOfflinePlayer(from).getName() + " §8(" + date + ")";

        push(from, sender);
        push(to, receiver);

        save();
    }

    // =========================
    // 💰 DEPOT
    // =========================
    public static void deposit(UUID uuid, double amount) {
        push(uuid, "§a+" + amount + "€ §8• Dépôt §7(" + now() + ")");
        save();
    }

    // =========================
    // 💸 RETRAIT
    // =========================
    public static void withdraw(UUID uuid, double amount) {
        push(uuid, "§c-" + amount + "€ §8• Retrait §7(" + now() + ")");
        save();
    }

    public static List<String> get(UUID uuid) {
        return history.getOrDefault(uuid, new ArrayList<>());
    }

    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§d[TX] " + msg);
    }
}