package fr.moodcraft.bridge.bank;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {

    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER
    }

    private static File file;
    private static FileConfiguration config;

    private static final Map<UUID, List<String>> history = new HashMap<>();
    private static final int MAX = 50;

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
            history.put(UUID.fromString(key),
                    new ArrayList<>(config.getStringList(key)));
        }

        log("§aChargé (" + history.size() + " joueurs)");
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
    // 💸 TRANSFER
    // =========================
    public static void transfer(UUID from, UUID to, double amount) {

        String date = now();

        String sender = "§c-" + amount + "€ §8→ §7" +
                Bukkit.getOfflinePlayer(to).getName() + " §8(" + date + ") §8[TRANSFER]";

        String receiver = "§a+" + amount + "€ §8← §7" +
                Bukkit.getOfflinePlayer(from).getName() + " §8(" + date + ") §8[TRANSFER]";

        push(from, sender);
        push(to, receiver);

        log("§fTRANSFER " + from + " -> " + to + " : " + amount + "€");

        save();
    }

    public static void deposit(UUID uuid, double amount) {
        push(uuid, "§a+" + amount + "€ §8• Dépôt §8(" + now() + ") §8[DEPOSIT]");
        log("§aDEPOSIT " + uuid + " : +" + amount);
        save();
    }

    public static void withdraw(UUID uuid, double amount) {
        push(uuid, "§c-" + amount + "€ §8• Retrait §8(" + now() + ") §8[WITHDRAW]");
        log("§cWITHDRAW " + uuid + " : -" + amount);
        save();
    }

    // =========================
    // 🔎 FILTRE
    // =========================
    public static List<String> getFiltered(UUID uuid, String filter, String search) {

        List<String> list = history.getOrDefault(uuid, new ArrayList<>());

        return list.stream()
                .filter(line -> filter == null || line.contains("[" + filter + "]"))
                .filter(line -> search == null || line.toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }

    // =========================
    // 📑 PAGINATION
    // =========================
    public static List<String> getPage(List<String> list, int page, int size) {

        int from = (page - 1) * size;
        int to = Math.min(from + size, list.size());

        if (from >= list.size()) return new ArrayList<>();

        return list.subList(from, to);
    }

    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§5[TX] " + msg);
    }
}