package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.manager.FortuneService.FortuneResult;

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
import java.util.List;

public final class FortunePanelManager {

    private static File file;
    private static FileConfiguration config;
    private static int taskId = -1;

    private FortunePanelManager() {}

    public static void init(Main plugin) {
        file = new File(plugin.getDataFolder(), "fortune-panels.yml");

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
                .runTaskTimer(plugin, FortunePanelManager::refresh, 40L, 20L * 10L)
                .getTaskId();
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public static void setPanel(int rank, Location location) {
        if (rank < 1 || rank > 3 || location == null || location.getWorld() == null) return;

        String path = "panels." + rank + ".";

        config.set(path + "world", location.getWorld().getName());
        config.set(path + "x", location.getBlockX());
        config.set(path + "y", location.getBlockY());
        config.set(path + "z", location.getBlockZ());

        save();
        refresh();
    }

    public static void clearPanel(int rank) {
        if (rank < 1 || rank > 3) return;

        config.set("panels." + rank, null);
        save();
    }

    public static void refresh() {
        if (config == null) return;

        List<FortuneResult> top = FortuneService.top(3);

        for (int rank = 1; rank <= 3; rank++) {
            Location location = getPanelLocation(rank);
            if (location == null) continue;

            FortuneResult result = top.size() >= rank ? top.get(rank - 1) : null;
            updateSign(location, rank, result);
        }
    }

    public static boolean hasPanel(int rank) {
        return getPanelLocation(rank) != null;
    }

    private static Location getPanelLocation(int rank) {
        if (config == null) return null;

        ConfigurationSection section = config.getConfigurationSection("panels." + rank);
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

    private static void updateSign(Location location, int rank, FortuneResult result) {
        Block block = location.getBlock();

        if (!(block.getState() instanceof Sign sign)) {
            return;
        }

        if (result == null) {
            setLine(sign, 0, "§6#" + rank + " §fFortune");
            setLine(sign, 1, "");
            setLine(sign, 2, "§7Aucun joueur");
            setLine(sign, 3, "§80€");
            sign.update(true, false);
            return;
        }

        setLine(sign, 0, "§6#" + rank + " §fFortune");
        setLine(sign, 1, "");
        setLine(sign, 2, "§a" + shorten(result.name(), 15));
        setLine(sign, 3, "§e" + FortuneService.money(result.total()));

        sign.update(true, false);
    }

    private static void setLine(Sign sign, int index, String text) {
        try {
            sign.getSide(Side.FRONT).setLine(index, text);
        } catch (Throwable ignored) {
            sign.setLine(index, text);
        }
    }

    private static String shorten(String text, int max) {
        if (text == null) return "Inconnu";
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(1, max - 1)) + ".";
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