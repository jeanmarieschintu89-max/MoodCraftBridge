package fr.moodcraft.bridge.bank;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {

    public enum Type {

        DEPOSIT,
        WITHDRAW,
        TRANSFER,

        MARKET_BUY,
        MARKET_SELL,

        PAY_SENT,
        PAY_RECEIVED,
        ESSENTIALS
    }

    private static File file;

    private static FileConfiguration config;

    private static final Map<UUID, List<String>> history = new HashMap<>();

    private static final List<String> globalLogs = new ArrayList<>();

    private static final int MAX = 250;

    private static final int GLOBAL_MAX = 1000;

    private static final Map<UUID, List<Long>> recentTransfers = new HashMap<>();

    private static final double ALERT_AMOUNT = 10000;

    private static final int SPAM_LIMIT = 5;

    private static final long SPAM_WINDOW = 60000;

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
            try {
                history.put(UUID.fromString(key), new ArrayList<>(config.getStringList(key)));
            } catch (Exception ignored) {}
        }

        if (config.contains("global")) {
            globalLogs.addAll(config.getStringList("global"));
        }

        log("§aTransactionManager chargé §8(" + history.size() + " joueurs)");
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String money(double value) {
        return String.format("%,.2f", value) + "€";
    }

    private static String playerName(UUID uuid) {
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name != null ? name : uuid.toString().substring(0, 8);
    }

    private static String now() {
        return new SimpleDateFormat("dd/MM HH:mm").format(new Date());
    }

    private static void push(UUID uuid, String line) {
        List<String> list = history.computeIfAbsent(uuid, k -> new ArrayList<>());
        list.add(0, line);
        if (list.size() > MAX) list.remove(list.size() - 1);
        config.set(uuid.toString(), list);
    }

    private static void pushGlobal(String line) {
        globalLogs.add(0, line);
        if (globalLogs.size() > GLOBAL_MAX) globalLogs.remove(globalLogs.size() - 1);
        config.set("global", globalLogs);
    }

    private static void checkFraude(UUID uuid, double amount) {
        long now = System.currentTimeMillis();
        recentTransfers.putIfAbsent(uuid, new ArrayList<>());
        List<Long> list = recentTransfers.get(uuid);
        list.add(now);
        list.removeIf(t -> now - t > SPAM_WINDOW);

        if (amount >= ALERT_AMOUNT) {
            alert("Gros virement détecté §8• §e" + playerName(uuid) + " §7(" + money(amount) + ")");
        }

        if (list.size() >= SPAM_LIMIT) {
            alert("Spam transferts §8• §e" + playerName(uuid) + " §7(" + list.size() + "/min)");
        }
    }

    private static void alert(String msg) {
        Bukkit.getConsoleSender().sendMessage("§4[ALERTE] " + msg);
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("moodcraft.admin"))
                .forEach(p -> p.sendMessage("§4⚠ " + msg));
    }

    public static void transfer(UUID from, UUID to, double amount) {
        String date = now();

        String sender = "§c-" + money(amount) + " §8→ §e" + playerName(to) + " §8• " + date + " §8[TRANSFER]";
        String receiver = "§a+" + money(amount) + " §8← §e" + playerName(from) + " §8• " + date + " §8[TRANSFER]";

        push(from, sender);
        push(to, receiver);

        pushGlobal("§6💸 VIREMENT §8• §e" + playerName(from) + " §7→ §e" + playerName(to) + " §8(" + money(amount) + ")");

        checkFraude(from, amount);
        log("TRANSFER " + playerName(from) + " -> " + playerName(to) + " : " + money(amount));
        save();
    }

    public static void deposit(UUID uuid, double amount) {
        push(uuid, "§a+" + money(amount) + " §8• Dépôt bancaire §8• " + now() + " §8[DEPOSIT]");
        pushGlobal("§a🏦 DEPOT §8• §e" + playerName(uuid) + " §7+" + money(amount));
        save();
    }

    public static void withdraw(UUID uuid, double amount) {
        push(uuid, "§c-" + money(amount) + " §8• Retrait bancaire §8• " + now() + " §8[WITHDRAW]");
        pushGlobal("§c🏦 RETRAIT §8• §e" + playerName(uuid) + " §7-" + money(amount));
        save();
    }

    public static void marketBuy(UUID uuid, String item, double amount, int qty) {
        push(uuid, "§6-" + money(amount) + " §8• Achat §e" + qty + "x " + item + " §8• " + now() + " §8[MARKET_BUY]");
        pushGlobal("§6📈 ACHAT §8• §e" + playerName(uuid) + " §7" + qty + "x " + item + " §8(" + money(amount) + ")");
        save();
    }

    public static void marketSell(UUID uuid, String item, double amount, int qty) {
        push(uuid, "§a+" + money(amount) + " §8• Vente §e" + qty + "x " + item + " §8• " + now() + " §8[MARKET_SELL]");
        pushGlobal("§a📊 VENTE §8• §e" + playerName(uuid) + " §7" + qty + "x " + item + " §8(" + money(amount) + ")");
        save();
    }

    public static void paySent(UUID from, String targetName, double amount) {
        push(from, "§c-" + money(amount) + " §8→ §e" + targetName + " §8• /pay Essentials §8• " + now() + " §8[PAY_SENT]");
        pushGlobal("§c💸 PAY §8• §e" + playerName(from) + " §7→ §e" + targetName + " §8(" + money(amount) + ")");
        checkFraude(from, amount);
        save();
    }

    public static void payReceived(UUID to, String senderName, double amount) {
        push(to, "§a+" + money(amount) + " §8← §e" + senderName + " §8• /pay Essentials §8• " + now() + " §8[PAY_RECEIVED]");
        pushGlobal("§a💸 PAY RECU §8• §e" + playerName(to) + " §7← §e" + senderName + " §8(" + money(amount) + ")");
        save();
    }

    public static void essentialsChange(UUID uuid, double delta, String reason) {
        if (Math.abs(delta) < 0.01) return;
        String sign = delta >= 0 ? "§a+" : "§c-";
        push(uuid, sign + money(Math.abs(delta)) + " §8• Variation Vault §8• " + clean(reason) + " §8• " + now() + " §8[ESSENTIALS]");
        pushGlobal("§b⚙ VAULT §8• §e" + playerName(uuid) + " §7" + (delta >= 0 ? "+" : "-") + money(Math.abs(delta)) + " §8(" + clean(reason) + ")");
        save();
    }

    public static List<String> getFiltered(UUID uuid, String filter, String search) {
        List<String> list = history.getOrDefault(uuid, new ArrayList<>());
        return list.stream()
                .filter(line -> matchesFilter(line, filter))
                .filter(line -> search == null || line.toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }

    private static boolean matchesFilter(String line, String filter) {
        if (filter == null) return true;
        return switch (filter) {
            case "BANK" -> line.contains("[DEPOSIT]") || line.contains("[WITHDRAW]");
            case "TRANSFER" -> line.contains("[TRANSFER]") || line.contains("[PAY_SENT]") || line.contains("[PAY_RECEIVED]");
            case "ESSENTIALS" -> line.contains("[ESSENTIALS]") || line.contains("[PAY_SENT]") || line.contains("[PAY_RECEIVED]");
            default -> line.contains("[" + filter + "]");
        };
    }

    public static List<String> getPage(List<String> list, int page, int size) {
        int from = (page - 1) * size;
        int to = Math.min(from + size, list.size());
        if (from >= list.size()) return new ArrayList<>();
        return list.subList(from, to);
    }

    public static List<String> getGlobal() {
        return globalLogs;
    }

    public static List<String> getHistory(UUID uuid) {
        return history.getOrDefault(uuid, new ArrayList<>());
    }

    public static void clear(UUID uuid) {
        history.remove(uuid);
        config.set(uuid.toString(), null);
        save();
    }

    private static String clean(String text) {
        return text == null || text.isBlank() ? "inconnu" : text.replace("§", "").trim();
    }

    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§5[TX] " + msg);
    }
}
