package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketState;
import fr.moodcraft.bridge.market.TrendManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetTrendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (args.length == 0) {

            sendUsage(sender);

            return true;
        }

        String item =
                args[0].toLowerCase();

        if (!MarketState.base.containsKey(item)) {

            sendError(
                    sender,
                    "Item inconnu dans le marché."
            );

            return true;
        }

        String trend =
                TrendManager.getTrend(item);

        sendTrend(
                sender,
                item,
                trend
        );

        return true;
    }

    private void sendTrend(
            CommandSender sender,
            String item,
            String trend
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fMarché §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§fTendance du marché.");
        sender.sendMessage("");
        sender.sendMessage("§7Ressource: §e" + item);
        sender.sendMessage("§7Tendance: " + cleanTrend(trend));
        sender.sendMessage("");
        sender.sendMessage("§8• §7La tendance peut changer");
        sender.sendMessage("§8• §7selon les achats et ventes");
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private void sendUsage(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fMarché §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§fCommande de tendance.");
        sender.sendMessage("");
        sender.sendMessage("§7Utilisation: §e/trend <item>");
        sender.sendMessage("§8• §7Exemple: §e/trend diamond");
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private void sendError(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fMarché §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§c✘ §fAction refusée.");
        sender.sendMessage("");
        sender.sendMessage("§7" + message);
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private String cleanTrend(
            String trend
    ) {

        if (trend == null || trend.isBlank()) {
            return "§7Stable";
        }

        return trend
                .replace("▬", "")
                .trim();
    }
}