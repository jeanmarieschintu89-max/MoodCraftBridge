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
        sender.sendMessage(header());
        sender.sendMessage("");
        sender.sendMessage(info("Tendance du marché."));
        sender.sendMessage("");
        sender.sendMessage(detail("Ressource : §e" + item));
        sender.sendMessage(detail("Tendance : " + cleanTrend(trend)));
        sender.sendMessage("");
        sender.sendMessage(detail("La tendance peut changer selon les achats et ventes."));
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private void sendUsage(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage(header());
        sender.sendMessage("");
        sender.sendMessage(info("Commande de tendance."));
        sender.sendMessage("");
        sender.sendMessage(detail("Utilisation : §e/trend <item>"));
        sender.sendMessage(detail("Exemple : §e/trend diamond"));
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private void sendError(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage("");
        sender.sendMessage(header());
        sender.sendMessage("");
        sender.sendMessage(errorLine("Action refusée."));
        sender.sendMessage("");
        sender.sendMessage(detail(message));
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private String header() {
        return "§8----- §6✦ §aMood§6Craft §fMarché ✦ §8-----";
    }

    private String info(String text) {
        return "§e➜ §f" + text;
    }

    private String detail(String text) {
        return "§8• §7" + text;
    }

    private String errorLine(String text) {
        return "§c✖ §f" + text;
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