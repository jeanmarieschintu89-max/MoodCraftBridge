package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public final class MoisPanelManager {

    public enum Mode {
        MOIS,
        JOUR,
        RECOMPENSES,
        DIRECT
    }

    private static File file;
    private static FileConfiguration config;
    private static int taskId = -1;

    private MoisPanelManager() {}

    public static void init(Main plugin) {
        file = new File(plugin.getDataFolder(), "mois-panels.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        stop();
        taskId = Bukkit.getScheduler()
                .runTaskTimer(plugin, MoisPanelManager::refresh, 40L, 20L * 60L * 10L)
                .getTaskId();

        refresh();
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public static void setPanel(Location location, Mode mode) {
        if (location == null || location.getWorld() == null) return;
        if (mode == null) mode = Mode.JOUR;

        String id = location.getWorld().getName()
                + ":" + location.getBlockX()
                + ":" + location.getBlockY()
                + ":" + location.getBlockZ();

        String path = "panels." + id + ".";
        config.set(path + "world", location.getWorld().getName());
        config.set(path + "x", location.getBlockX());
        config.set(path + "y", location.getBlockY());
        config.set(path + "z", location.getBlockZ());
        config.set(path + "mode", mode.name());

        save();
        refresh();
    }

    public static boolean clearPanel(Location location) {
        if (location == null || location.getWorld() == null) return false;

        String id = location.getWorld().getName()
                + ":" + location.getBlockX()
                + ":" + location.getBlockY()
                + ":" + location.getBlockZ();

        if (config.getConfigurationSection("panels." + id) == null) return false;

        config.set("panels." + id, null);
        save();
        return true;
    }

    public static void refresh() {
        if (config == null) return;

        ConfigurationSection section = config.getConfigurationSection("panels");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            Location location = getPanelLocation(id);
            if (location == null) continue;

            Mode mode = getPanelMode(id);
            updateSign(location, mode);
        }
    }

    private static Location getPanelLocation(String id) {
        ConfigurationSection section = config.getConfigurationSection("panels." + id);
        if (section == null) return null;

        String worldName = section.getString("world");
        World world = Bukkit.getWorld(worldName == null ? "" : worldName);
        if (world == null) return null;

        return new Location(
                world,
                section.getInt("x"),
                section.getInt("y"),
                section.getInt("z")
        );
    }

    private static Mode getPanelMode(String id) {
        String text = config.getString("panels." + id + ".mode", "JOUR");

        try {
            return Mode.valueOf(text.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return Mode.JOUR;
        }
    }

    private static void updateSign(Location location, Mode mode) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return;

        LocalDate now = LocalDate.now();
        String month = now.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
        month = month.substring(0, 1).toUpperCase(Locale.FRANCE) + month.substring(1);
        String monthYear = month + " " + now.getYear();
        String dayMonth = now.getDayOfMonth() + " " + month;

        switch (mode) {
            case MOIS -> {
                setLine(sign, 0, "§6Classement vote");
                setLine(sign, 1, "");
                setLine(sign, 2, "§a" + monthYear);
                setLine(sign, 3, "");
            }
            case JOUR -> {
                setLine(sign, 0, "§6Classement vote");
                setLine(sign, 1, "");
                setLine(sign, 2, "§a" + dayMonth);
                setLine(sign, 3, "");
            }
            case RECOMPENSES -> {
                setLine(sign, 0, "§eRécompenses");
                setLine(sign, 1, "§eTop Vote");
                setLine(sign, 2, "");
                setLine(sign, 3, "§a" + monthYear);
            }
            case DIRECT -> {
                setLine(sign, 0, "§6Classement en");
                setLine(sign, 1, "§6direct");
                setLine(sign, 2, "");
                setLine(sign, 3, "§a" + dayMonth);
            }
        }

        applyGlowOnly(sign);
        sign.update(true, false);
    }

    private static void applyGlowOnly(Sign sign) {
        try {
            sign.getSide(Side.FRONT).setGlowingText(true);
        } catch (Throwable ignored) {
            try {
                sign.setGlowingText(true);
            } catch (Throwable ignoredAgain) {}
        }
    }

    private static void setLine(Sign sign, int index, String text) {
        try {
            sign.getSide(Side.FRONT).setLine(index, text);
        } catch (Throwable ignored) {
            sign.setLine(index, text);
        }
    }

    private static void save() {
        if (config == null || file == null) return;

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}