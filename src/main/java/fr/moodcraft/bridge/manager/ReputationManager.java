package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReputationManager {

    private static File file;
    private static FileConfiguration config;

    //
    // 🧠 CACHE
    //

    private static final Map<String, Integer> cache =
            new HashMap<>();

    private static final Map<String, String> names =
            new HashMap<>();

    // =========================
    // 🚀 INIT
    // =========================

    public static void init() {

        file = new File(

                Main.getInstance()
                        .getDataFolder(),

                "reputation.yml"
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

        //
        // 🔄 LOAD
        //

        for (String uuid : config.getKeys(false)) {

            //
            // 📦 NOUVEAU FORMAT
            //

            if (config.isConfigurationSection(uuid)) {

                int rep =
                        config.getInt(
                                uuid + ".reputation"
                        );

                String name =
                        config.getString(
                                uuid + ".name",
                                "Inconnu"
                        );

                cache.put(uuid, rep);

                names.put(uuid, name);

                continue;
            }

            //
            // 🔥 ANCIEN FORMAT
            //

            int rep =
                    config.getInt(uuid);

            cache.put(uuid, rep);

            String name = "Inconnu";

            try {

                var offline =
                        Bukkit.getOfflinePlayer(
                                java.util.UUID.fromString(uuid)
                        );

                if (offline.getName() != null) {
                    name = offline.getName();
                }

            } catch (Exception ignored) {}

            names.put(uuid, name);

            //
            // 🔄 MIGRATION AUTO
            //

            config.set(uuid, null);

            config.set(
                    uuid + ".name",
                    name
            );

            config.set(
                    uuid + ".reputation",
                    rep
            );
        }

        save();
    }

    // =========================
    // 🔍 GET
    // =========================

    public static int get(String uuid) {

        return cache.getOrDefault(
                uuid,
                0
        );
    }

    // =========================
    // 🏷️ GET NAME
    // =========================

    public static String getName(String uuid) {

        return names.getOrDefault(
                uuid,
                "Inconnu"
        );
    }

    // =========================
    // ⚙️ SET
    // =========================

    public static void set(
            String uuid,
            int value
    ) {

        value = Math.max(0, value);

        cache.put(uuid, value);

        //
        // 👤 UPDATE NAME
        //

        try {

            var offline =
                    Bukkit.getOfflinePlayer(
                            java.util.UUID.fromString(uuid)
                    );

            if (offline.getName() != null) {

                names.put(
                        uuid,
                        offline.getName()
                );
            }

        } catch (Exception ignored) {}

        //
        // 💾 SAVE
        //

        config.set(
                uuid + ".name",
                names.getOrDefault(uuid, "Inconnu")
        );

        config.set(
                uuid + ".reputation",
                value
        );

        save();
    }

    // =========================
    // ➕ ADD
    // =========================

    public static void add(
            String uuid,
            int value
    ) {

        set(
                uuid,
                get(uuid) + value
        );
    }

    // =========================
    // 🔄 RESET
    // =========================

    public static void reset(
            String uuid
    ) {

        set(uuid, 0);
    }

    // =========================
    // ✨ ADD STYLE
    // =========================

    public static void addRepStyled(
            Player p,
            int value,
            String reason
    ) {

        String id =
                p.getUniqueId().toString();

        int old =
                get(id);

        int now =
                Math.max(0, old + value);

        set(id, now);

        //
        // 📜 HISTORIQUE
        //

        ReputationHistoryManager.add(

                p.getUniqueId(),

                value,

                reason
        );

        //
        // 🎨 STYLE
        //

        String color =
                value >= 0
                        ? "§a+"
                        : "§c";

        String arrow =
                value >= 0
                        ? "⬆"
                        : "⬇";

        p.sendMessage(
                "§8§m-----------------------------"
        );

        p.sendMessage(
                "§6✦ §fRéputation mise à jour"
        );

        p.sendMessage(
                "§7Variation: "
                        + color
                        + value
                        + " §7"
                        + arrow
        );

        p.sendMessage(
                "§7Raison: §e"
                        + reason
        );

        p.sendMessage(
                "§7Total: §e"
                        + now
                        + " §8("
                        + getRank(now)
                        + "§8)"
        );

        p.sendMessage(
                "§8§m-----------------------------"
        );

        p.playSound(

                p.getLocation(),

                value >= 0

                        ? org.bukkit.Sound.ENTITY_PLAYER_LEVELUP

                        : org.bukkit.Sound.ENTITY_VILLAGER_NO,

                1,

                1
        );
    }

    // =========================
    // 🏆 TOP
    // =========================

    public static LinkedHashMap<String, Integer> getTop(
            int limit
    ) {

        return cache.entrySet()
                .stream()
                .sorted((a, b) ->
                        b.getValue()
                                .compareTo(a.getValue())
                )
                .limit(limit)
                .collect(

                        LinkedHashMap::new,

                        (map, entry) ->
                                map.put(
                                        entry.getKey(),
                                        entry.getValue()
                                ),

                        LinkedHashMap::putAll
                );
    }

    // =========================
    // 🧠 RANK
    // =========================

    public static String getRank(int rep) {

        if (rep >= 2500)
            return "§6Légende";

        if (rep >= 1800)
            return "§dPrestigieux";

        if (rep >= 1200)
            return "§5Influent";

        if (rep >= 700)
            return "§bReconnu";

        if (rep >= 350)
            return "§aRespecté";

        if (rep >= 120)
            return "§fÉtabli";

        if (rep >= 40)
            return "§7Habitué";

        if (rep >= 15)
            return "§bCitoyen reconnu";

        if (rep >= 5)
            return "§aHabitant apprécié";

        if (rep >= 1)
            return "§eVoyageur";

        return "§8Nouveau";
    }

    // =========================
    // 🏷️ PREFIX
    // =========================

    public static String getPrefix(int rep) {

        return "§8["

                + getRank(rep)

                + "§8]";
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
}