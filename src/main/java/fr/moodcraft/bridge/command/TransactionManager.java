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
    private static final List<String> globalLogs = new ArrayList<>();

    // 🚨 FRAUDE
    private static final Map<UUID, List<Long>> recentTransfers = new HashMap<>();
    private static final double ALERT_AMOUNT = 10000; // seuil
    private static final int SPAM_LIMIT = 5; // nb transferts
    private static final long SPAM_WINDOW = 60_000; // 1 min

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
            if (key.equals("global")) continue;

            history.put(UUID.fromString(key),
                    new ArrayList<>(config.getStringList(key)));
        }

        if (config.contains("global")) {
            globalLogs.addAll(config.getStringList("global"));
        }

        log("Chargé (" + history.size() + " joueurs)");
    }

    private static String now() {
        return new SimpleDateFormat("dd/MM HH:mm").format(new Date());
    }

    private static void push(UUID uuid, String line) {
        List<String> list = history.computeIfAbsent(uuid, k -> new ArrayList<>());
        list.add(0, line);

        if (list.size() > 50) list.remove(list.size() - 1);

        config.set(uuid.toString(), list);
    }

    private static void pushGlobal(String line) {
        globalLogs.add(0, line);
        if (globalLogs.size() > 200) globalLogs.remove(globalLogs.size() - 1);
        config.set("global", globalLogs);
    }

    private static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 🚨 FRAUDE CHECK
    // =========================
    private static void checkFraude(UUID uuid, double amount) {

        long now = System.currentTimeMillis();

        recentTransfers.putIfAbsent(uuid, new ArrayList<>());
        List<Long> list = recentTransfers.get(uuid);

        list.add(now);
        list.removeIf(t -> now - t > SPAM_WINDOW);

        // 💰 gros montant
        if (amount >= ALERT_AMOUNT) {
            alert("§cGROS VIREMENT: " + amount + "€ par " + uuid);
        }

        // 🔁 spam
        if (list.size() >= SPAM_LIMIT) {
            alert("§cSPAM VIREMENT: " + uuid + " (" + list.size() + "/min)");
        }
    }

    private static void alert(String msg) {
        Bukkit.getConsoleSender().sendMessage("§4[ALERTE] " + msg);

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("moodcraft.admin"))
                .forEach(p -> p.sendMessage("§4⚠ " + msg));
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

        pushGlobal("§eVIREMENT §7" +
                Bukkit.getOfflinePlayer(from).getName() + " → " +
                Bukkit.getOfflinePlayer(to).getName() +
                " §f" + amount + "€");

        checkFraude(from, amount);

        save();
    }

    public static void deposit(UUID uuid, double amount) {
        push(uuid, "§a+" + amount + "€ §8• Dépôt (" + now() + ")");
        pushGlobal("§aDEPOT §7" + Bukkit.getOfflinePlayer(uuid).getName() + " +" + amount);
        save();
    }

    public static void withdraw(UUID uuid, double amount) {
        push(uuid, "§c-" + amount + "€ §8• Retrait (" + now() + ")");
        pushGlobal("§cRETRAIT §7" + Bukkit.getOfflinePlayer(uuid).getName() + " -" + amount);
        save();
    }

    public static List<String> get(UUID uuid) {
        return history.getOrDefault(uuid, new ArrayList<>());
    }

    public static List<String> getGlobal() {
        return globalLogs;
    }

    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§5[TX] " + msg);
    }
}