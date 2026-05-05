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

    // uuid -> liste de lignes
    private static final Map<UUID, List<String>> history = new HashMap<>();

    private static final int MAX = 20; // limite par joueur

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
            List<String> list = config.getStringList(key);
            history.put(uuid, new ArrayList<>(list));
        }

        log("§a✔ Historique chargé (" + history.size() + " joueurs)");
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ➕ AJOUT TRANSACTION
    // =========================
    public static void add(UUID sender, UUID target, double amount) {

        String date = new SimpleDateFormat("dd/MM HH:mm").format(new Date());

        String sName = Bukkit.getOfflinePlayer(sender).getName();
        String tName = Bukkit.getOfflinePlayer(target).getName();

        String sent = "§c-" + amount + "€ §8→ §7" + tName + " §8(" + date + ")";
        String received = "§a+" + amount + "€ §8← §7" + sName + " §8(" + date + ")";

        push(sender, sent);
        push(target, received);

        save();
    }

    private static void push(UUID uuid, String line) {

        List<String> list = history.computeIfAbsent(uuid, k -> new ArrayList<>());

        list.add(0, line); // ajout en haut

        if (list.size() > MAX) {
            list.remove(list.size() - 1);
        }

        config.set(uuid.toString(), list);
    }

    // =========================
    // 📜 GET
    // =========================
    public static List<String> get(UUID uuid) {
        return history.getOrDefault(uuid, new ArrayList<>());
    }

    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§d[TX] " + msg);
    }
}