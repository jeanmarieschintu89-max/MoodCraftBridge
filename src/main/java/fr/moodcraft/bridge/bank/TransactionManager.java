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
        MARKET_SELL
    }

    private static File file;

    private static FileConfiguration config;

    //
    // 📜 HISTORIQUE JOUEUR
    //

    private static final Map<UUID, List<String>>
            history = new HashMap<>();

    //
    // 🌍 LOGS GLOBAUX
    //

    private static final List<String>
            globalLogs = new ArrayList<>();

    //
    // 📦 MAX HISTORIQUE
    //

    private static final int MAX = 50;

    //
    // 🚨 FRAUDE
    //

    private static final Map<UUID, List<Long>>
            recentTransfers = new HashMap<>();

    private static final double ALERT_AMOUNT = 10000;

    private static final int SPAM_LIMIT = 5;

    private static final long SPAM_WINDOW = 60000;

    //
    // 🚀 INIT
    //

    public static void init() {

        file = new File(
                Main.getInstance().getDataFolder(),
                "transactions.yml"
        );

        if (!file.exists()) {

            try {

                file.getParentFile().mkdirs();

                file.createNewFile();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        config =
                YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {

            if (key.equals("global"))
                continue;

            history.put(

                    UUID.fromString(key),

                    new ArrayList<>(
                            config.getStringList(key)
                    )
            );
        }

        if (config.contains("global")) {

            globalLogs.addAll(
                    config.getStringList("global")
            );
        }

        log("§aChargé (" + history.size() + " joueurs)");
    }

    //
    // 💾 SAVE
    //

    public static void save() {

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    //
    // 🕒 DATE
    //

    private static String now() {

        return new SimpleDateFormat(
                "dd/MM HH:mm"
        ).format(new Date());
    }

    //
    // 📜 PUSH PLAYER
    //

    private static void push(UUID uuid,
                             String line) {

        List<String> list =
                history.computeIfAbsent(
                        uuid,
                        k -> new ArrayList<>()
                );

        list.add(0, line);

        if (list.size() > MAX) {

            list.remove(
                    list.size() - 1
            );
        }

        config.set(
                uuid.toString(),
                list
        );
    }

    //
    // 🌍 PUSH GLOBAL
    //

    private static void pushGlobal(String line) {

        globalLogs.add(0, line);

        if (globalLogs.size() > 200) {

            globalLogs.remove(
                    globalLogs.size() - 1
            );
        }

        config.set(
                "global",
                globalLogs
        );
    }

    //
    // 🚨 FRAUDE
    //

    private static void checkFraude(UUID uuid,
                                    double amount) {

        long now =
                System.currentTimeMillis();

        recentTransfers.putIfAbsent(
                uuid,
                new ArrayList<>()
        );

        List<Long> list =
                recentTransfers.get(uuid);

        list.add(now);

        list.removeIf(
                t -> now - t > SPAM_WINDOW
        );

        if (amount >= ALERT_AMOUNT) {

            alert(
                    "Gros virement: "
                            + amount
                            + "€ ("
                            + uuid
                            + ")"
            );
        }

        if (list.size() >= SPAM_LIMIT) {

            alert(
                    "Spam virement: "
                            + uuid
                            + " ("
                            + list.size()
                            + "/min)"
            );
        }
    }

    //
    // 🚨 ALERT
    //

    private static void alert(String msg) {

        Bukkit.getConsoleSender().sendMessage(
                "§4[ALERTE] " + msg
        );

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(
                        p -> p.hasPermission(
                                "moodcraft.admin"
                        )
                )
                .forEach(
                        p -> p.sendMessage(
                                "§4⚠ " + msg
                        )
                );
    }

    //
    // 💸 VIREMENT
    //

    public static void transfer(UUID from,
                                UUID to,
                                double amount) {

        String date =
                now();

        String sender =

                "§c-"
                        + amount
                        + "€ §8→ §7"
                        + Bukkit.getOfflinePlayer(to).getName()
                        + " §8("
                        + date
                        + ") §8[TRANSFER]";

        String receiver =

                "§a+"
                        + amount
                        + "€ §8← §7"
                        + Bukkit.getOfflinePlayer(from).getName()
                        + " §8("
                        + date
                        + ") §8[TRANSFER]";

        push(from, sender);

        push(to, receiver);

        pushGlobal(

                "§eVIREMENT §7"
                        + Bukkit.getOfflinePlayer(from).getName()
                        + " → "
                        + Bukkit.getOfflinePlayer(to).getName()
                        + " §f"
                        + amount
                        + "€"
        );

        checkFraude(from, amount);

        log(
                "TRANSFER "
                        + from
                        + " -> "
                        + to
                        + " : "
                        + amount
                        + "€"
        );

        save();
    }

    //
    // 📥 DEPOT
    //

    public static void deposit(UUID uuid,
                               double amount) {

        push(

                uuid,

                "§a+"
                        + amount
                        + "€ §8• Dépôt §8("
                        + now()
                        + ") §8[DEPOSIT]"
        );

        pushGlobal(

                "§aDEPOT §7"
                        + Bukkit.getOfflinePlayer(uuid).getName()
                        + " +"
                        + amount
                        + "€"
        );

        save();
    }

    //
    // 📤 RETRAIT
    //

    public static void withdraw(UUID uuid,
                                double amount) {

        push(

                uuid,

                "§c-"
                        + amount
                        + "€ §8• Retrait §8("
                        + now()
                        + ") §8[WITHDRAW]"
        );

        pushGlobal(

                "§cRETRAIT §7"
                        + Bukkit.getOfflinePlayer(uuid).getName()
                        + " -"
                        + amount
                        + "€"
        );

        save();
    }

    //
    // 🛒 ACHAT MARCHÉ
    //

    public static void marketBuy(UUID uuid,
                                 String item,
                                 double amount,
                                 int qty) {

        push(

                uuid,

                "§6-"
                        + amount
                        + "€ §8• Achat §e"
                        + qty
                        + "x "
                        + item
                        + " §8("
                        + now()
                        + ") §8[MARKET_BUY]"
        );

        pushGlobal(

                "§6ACHAT §7"
                        + Bukkit.getOfflinePlayer(uuid).getName()
                        + " §f"
                        + qty
                        + "x "
                        + item
                        + " §8("
                        + amount
                        + "€)"
        );

        save();
    }

    //
    // 💸 VENTE MARCHÉ
    //

    public static void marketSell(UUID uuid,
                                  String item,
                                  double amount,
                                  int qty) {

        push(

                uuid,

                "§a+"
                        + amount
                        + "€ §8• Vente §e"
                        + qty
                        + "x "
                        + item
                        + " §8("
                        + now()
                        + ") §8[MARKET_SELL]"
        );

        pushGlobal(

                "§aVENTE §7"
                        + Bukkit.getOfflinePlayer(uuid).getName()
                        + " §f"
                        + qty
                        + "x "
                        + item
                        + " §8("
                        + amount
                        + "€)"
        );

        save();
    }

    //
    // 🔎 FILTRE
    //

    public static List<String> getFiltered(UUID uuid,
                                           String filter,
                                           String search) {

        List<String> list =
                history.getOrDefault(
                        uuid,
                        new ArrayList<>()
                );

        return list.stream()

                .filter(
                        line ->
                                filter == null
                                        || line.contains(
                                        "[" + filter + "]"
                                )
                )

                .filter(
                        line ->
                                search == null
                                        || line.toLowerCase()
                                        .contains(
                                                search.toLowerCase()
                                        )
                )

                .collect(Collectors.toList());
    }

    //
    // 📑 PAGINATION
    //

    public static List<String> getPage(List<String> list,
                                       int page,
                                       int size) {

        int from =
                (page - 1) * size;

        int to =
                Math.min(
                        from + size,
                        list.size()
                );

        if (from >= list.size())
            return new ArrayList<>();

        return list.subList(from, to);
    }

    //
    // 🌍 LOGS
    //

    public static List<String> getGlobal() {

        return globalLogs;
    }

    //
    // 📜 GET PLAYER HISTORY
    //

    public static List<String> getHistory(UUID uuid) {

        return history.getOrDefault(
                uuid,
                new ArrayList<>()
        );
    }

    //
    // 🧹 CLEAR PLAYER
    //

    public static void clear(UUID uuid) {

        history.remove(uuid);

        config.set(
                uuid.toString(),
                null
        );

        save();
    }

    //
    // 🖨 LOG
    //

    private static void log(String msg) {

        Bukkit.getConsoleSender().sendMessage(
                "§5[TX] " + msg
        );
    }
}