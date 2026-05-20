package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.manager.VoteTopService.VoteEntry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class VoteMonthlyRewardManager {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static int taskId = -1;

    private VoteMonthlyRewardManager() {}

    public static void init(Main plugin) {
        stop();

        taskId = Bukkit.getScheduler()
                .runTaskTimer(plugin, VoteMonthlyRewardManager::tick, 20L * 60L, 20L * 60L)
                .getTaskId();
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    private static void tick() {
        FileConfiguration config = Main.getInstance().getConfig();

        if (!config.getBoolean("vote-top.monthly-rewards.enabled", true)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        YearMonth month = YearMonth.from(now);
        LocalDate today = now.toLocalDate();

        if (!today.equals(month.atEndOfMonth())) {
            return;
        }

        int hour = config.getInt("vote-top.monthly-rewards.hour", 23);
        int minute = config.getInt("vote-top.monthly-rewards.minute", 55);

        if (now.getHour() != hour || now.getMinute() != minute) {
            return;
        }

        rewardCurrentMonth(false);
    }

    public static void rewardCurrentMonth(boolean force) {
        String monthKey = YearMonth.now().format(MONTH_FORMAT);
        FileConfiguration config = Main.getInstance().getConfig();

        String rewardedMonth = config.getString("vote-top.monthly-rewards.last-rewarded-month", "");

        if (!force && rewardedMonth.equals(monthKey)) {
            Bukkit.getConsoleSender().sendMessage("§e[VoteRewards] Le mois " + monthKey + " a déjà été récompensé.");
            return;
        }

        VoteTopService.refreshNowAsync().whenComplete((top, throwable) -> Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (throwable != null) {
                Bukkit.getConsoleSender().sendMessage("§c[VoteRewards] Impossible de lire le top votes: " + throwable.getMessage());
                return;
            }

            if (top == null || top.size() < 3) {
                Bukkit.getConsoleSender().sendMessage("§c[VoteRewards] Top 3 votes incomplet, récompenses annulées.");
                return;
            }

            rewardRank(1, top.get(0));
            rewardRank(2, top.get(1));
            rewardRank(3, top.get(2));

            config.set("vote-top.monthly-rewards.last-rewarded-month", monthKey);
            Main.getInstance().saveConfig();

            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("§8----- §6✦ §aMood§6Craft §fVotes ✦ §8-----");
            Bukkit.broadcastMessage("§6✦ §fRécompenses mensuelles des votes distribuées.");
            Bukkit.broadcastMessage("§8• §6#1 §a" + top.get(0).name() + " §7- §e" + top.get(0).votes() + " votes");
            Bukkit.broadcastMessage("§8• §6#2 §a" + top.get(1).name() + " §7- §e" + top.get(1).votes() + " votes");
            Bukkit.broadcastMessage("§8• §6#3 §a" + top.get(2).name() + " §7- §e" + top.get(2).votes() + " votes");
            Bukkit.broadcastMessage("§8-----------------------------");
            Bukkit.broadcastMessage("");
        }));
    }

    public static void rewardRank(int rank, VoteEntry entry) {
        if (entry == null) return;

        List<String> commands = Main.getInstance().getConfig().getStringList("vote-top.monthly-rewards.commands." + rank);

        if (commands == null || commands.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§c[VoteRewards] Aucune commande configurée pour le rang #" + rank);
            return;
        }

        String playerName = entry.name();
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        String uuid = offline != null && offline.getUniqueId() != null ? offline.getUniqueId().toString() : "";

        for (String command : commands) {
            String parsed = command
                    .replace("{player}", playerName)
                    .replace("{name}", playerName)
                    .replace("{uuid}", uuid)
                    .replace("{rank}", String.valueOf(rank))
                    .replace("{votes}", String.valueOf(entry.votes()));

            if (parsed.startsWith("/")) {
                parsed = parsed.substring(1);
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }

        Bukkit.getConsoleSender().sendMessage("§a[VoteRewards] Rang #" + rank + " récompensé: " + playerName + " (" + entry.votes() + " votes)");
    }
}
