package fr.moodcraft.bridge.bank;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BankStorage {

    private static File file;
    private static FileConfiguration config;

    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "bank.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    // =========================
    // 💰 GET
    // =========================
    public static double get(String uuid) {
        return config.getDouble(uuid + ".balance", 0.0);
    }

    // =========================
    // 💰 SET
    // =========================
    public static void set(String uuid, double value) {

        if (value < 0) value = 0;

        config.set(uuid + ".balance", value);

        updateName(uuid);
        save();
    }

    // =========================
    // ➕ DEPOT
    // =========================
    public static void deposit(String uuid, double amount) {

        if (amount <= 0) return;

        set(uuid, get(uuid) + amount);

        log(uuid, "DEPOSIT", amount, "-", "bank");
    }

    // =========================
    // ➖ RETRAIT
    // =========================
    public static boolean withdraw(String uuid, double amount) {

        if (amount <= 0) return false;

        double current = get(uuid);

        if (current < amount) return false;

        set(uuid, current - amount);

        log(uuid, "WITHDRAW", amount, "-", "bank");

        return true;
    }

    // =========================
    // 🔄 TRANSFER
    // =========================
    public static boolean transfer(String from, String to, double amount) {

        if (amount <= 0) return false;

        if (!withdraw(from, amount)) return false;

        deposit(to, amount);

        log(from, "TRANSFER_SENT", amount, to, "iban");
        log(to, "TRANSFER_RECEIVED", amount, from, "iban");

        return true;
    }

    // =========================
    // 🛒 ACHAT
    // =========================
    public static boolean purchase(String uuid, double amount, String target) {

        if (!withdraw(uuid, amount)) return false;

        log(uuid, "PURCHASE", amount, target, "shop");

        return true;
    }

    // =========================
    // 💰 VENTE
    // =========================
    public static void sale(String uuid, double amount, String target) {

        deposit(uuid, amount);

        log(uuid, "SALE", amount, target, "shop");
    }

    // =========================
    // 📝 LOG
    // =========================
    private static void log(String uuid, String type, double amount, String target, String reason) {

        String id = UUID.randomUUID().toString();
        String path = "logs." + id;

        config.set(path + ".uuid", uuid);
        config.set(path + ".type", type);
        config.set(path + ".amount", amount);
        config.set(path + ".target", target);
        config.set(path + ".reason", reason);
        config.set(path + ".time", System.currentTimeMillis());

        save();
    }

    // =========================
    // 📜 LOGS
    // =========================
    public static Set<String> getLogs() {

        if (config.getConfigurationSection("logs") == null)
            return Collections.emptySet();

        return config.getConfigurationSection("logs").getKeys(false);
    }

    public static String getLog(String key, String field) {
        return String.valueOf(config.get("logs." + key + "." + field));
    }

    // =========================
    // 🏦 IBAN
    // =========================
    public static String getIban(String uuid) {

        String iban = config.getString(uuid + ".iban");

        if (iban == null) {
            iban = generateIban();
            config.set(uuid + ".iban", iban);
            updateName(uuid);
            save();
        }

        return iban;
    }

    public static String getUUIDByIban(String iban) {

        for (String key : config.getKeys(false)) {

            if (key.equals("logs")) continue;

            String stored = config.getString(key + ".iban");

            if (stored != null && stored.equalsIgnoreCase(iban)) {
                return key;
            }
        }

        return null;
    }

    // =========================
    // 👤 NAME
    // =========================
    public static String getName(String uuid) {
        return config.getString(uuid + ".name", "Unknown");
    }

    private static void updateName(String uuid) {

        try {
            UUID u = UUID.fromString(uuid);
            var player = Bukkit.getOfflinePlayer(u);

            if (player.getName() != null) {
                config.set(uuid + ".name", player.getName());
            }

        } catch (Exception ignored) {}
    }

    // =========================
    // 🔢 IBAN
    // =========================
    private static String generateIban() {

        Random r = new Random();

        int part1 = 1000 + r.nextInt(9000);
        int part2 = 1000 + r.nextInt(9000);

        return "MC-" + part1 + "-" + part2;
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

    // =========================
    // ⚡ COMPATIBILITÉ
    // =========================
    public static void add(String uuid, double amount) {
        deposit(uuid, amount);
    }

    public static boolean remove(String uuid, double amount) {
        return withdraw(uuid, amount);
    }
}