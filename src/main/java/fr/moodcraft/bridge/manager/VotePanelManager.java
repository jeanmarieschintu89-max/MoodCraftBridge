package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.manager.VoteTopService.VoteEntry;

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

public final class VotePanelManager {

    private static File file;
    private static FileConfiguration config;
    private static int taskId = -1;

    private VotePanelManager() {}

    public static void init(Main plugin) {
        file = new File(plugin.getDataFolder(), "vote-panels.yml");

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
                .runTaskTimer(plugin, VotePanelManager::refresh, 80L, 20L * 60L)
                .getTaskId();

        VoteTopService.refreshAsync();
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public static void setPanel(int rank, Location location) {
        if (rank < 1 || rank > 10 || location == null || location.getWorld() == null) return;

        String path = "panels." + rank + ".";
        config.set(path + "world", location.getWorld().getName());
        config.set(path + "x", location.getBlockX());
        config.set(path + "y", location.getBlockY());
        config.set(path + "z", location.getBlockZ());

        save();
        refresh();
    }

    public static void clearPanel(int rank) {
        if (rank < 1 || rank > 10) return;
        config.set("panels." + rank, null);
        save();
    }

    public static void refresh() {
        VoteTopService.top(10);
        refreshFromCache();
    }

    public static void forceRefresh() {
        VoteTopService.forceRefresh();
    }

    public static void refreshFromCache() {
        if (config == null) return;

        List<VoteEntry> top = VoteTopService.top(10);

        for (int rank = 1; rank <= 10; rank++) {
            Location location = getPanelLocation(rank);
            if (location == null) continue;

            VoteEntry entry = top.size() >= rank ? top.get(rank - 1) : null;
            updateSign(location, rank, entry);
        }
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

    private static void updateSign(Location location, int rank, VoteEntry entry) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return;

        if (entry == null) {
            setLine(sign, 0, "§6#" + rank + " §fVotes");
            setLine(sign, 1, "");
            setLine(sign, 2, "§7Aucun vote");
            setLine(sign, 3, "§80 vote");
            sign.update(true, false);
            return;
        }

        setLine(sign, 0, "§6#" + rank + " §fVotes");
        setLine(sign, 1, "");
        setLine(sign, 2, "§a" + VoteTopService.shorten(entry.name(), 15));
        setLine(sign, 3, "§e" + VoteTopService.panelVotes(entry.votes()));
        sign.update(true, false);
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