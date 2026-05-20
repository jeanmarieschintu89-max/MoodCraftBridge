package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.VoteMonthlyRewardManager;
import fr.moodcraft.bridge.manager.VoteTopService;
import fr.moodcraft.bridge.manager.VoteTopService.VoteEntry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VoteRewardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("econ.admin") && !sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration.");
            return true;
        }

        if (args.length < 1) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "preview", "top", "test" -> preview(sender);
            case "run", "give", "force" -> run(sender, args);
            default -> help(sender);
        }

        return true;
    }

    private void preview(CommandSender sender) {
        header(sender, "Récompenses votes");
        sender.sendMessage("§8• §7Lecture du top votes depuis : §e" + VoteTopService.sourceUrl());
        footer(sender);

        VoteTopService.refreshNowAsync().whenComplete((top, throwable) -> Bukkit.getScheduler().runTask(fr.moodcraft.bridge.Main.getInstance(), () -> {
            header(sender, "Top votes actuel");

            if (throwable != null || top == null || top.isEmpty()) {
                sender.sendMessage("§c✖ §fImpossible de lire le top votes.");
                footer(sender);
                return;
            }

            for (int i = 0; i < Math.min(3, top.size()); i++) {
                VoteEntry entry = top.get(i);
                int rank = i + 1;
                sender.sendMessage("§8• §6#" + rank + " §a" + entry.name() + " §7- §e" + entry.votes() + " votes §8→ §f" + rewardName(rank));
            }

            footer(sender);
        }));
    }

    private void run(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("CONFIRM")) {
            error(sender, "Confirmation obligatoire : §e/votereward run CONFIRM");
            return;
        }

        header(sender, "Récompenses votes");
        sender.sendMessage("§a✔ §fDistribution forcée lancée.");
        sender.sendMessage("§8• §7Le top 3 sera relu sur le site avant distribution.");
        footer(sender);

        VoteMonthlyRewardManager.rewardCurrentMonth(true);
    }

    private String rewardName(int rank) {
        return switch (rank) {
            case 1 -> "clé Émeraude";
            case 2 -> "clé Diamant";
            case 3 -> "clé Or";
            default -> "récompense";
        };
    }

    private void help(CommandSender sender) {
        header(sender, "Récompenses votes");
        sender.sendMessage("§e➜ §7/votereward preview");
        sender.sendMessage("§e➜ §7/votereward run CONFIRM");
        sender.sendMessage("§8• §7Auto : dernier jour du mois à l'heure configurée.");
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Récompenses votes");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §f" + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }
}
