package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("moodcraft.admin")) {

            sendError(
                    sender,
                    "Accès réservé à l'administration."
            );

            return true;
        }

        if (args.length < 3) {

            sendUsage(sender);

            return true;
        }

        String type =
                args[0].toLowerCase();

        String item =
                args[1].toLowerCase();

        int amount;

        try {

            amount =
                    Integer.parseInt(args[2]);

        } catch (Exception e) {

            sendError(
                    sender,
                    "Nombre invalide."
            );

            return true;
        }

        if (amount <= 0) {

            sendError(
                    sender,
                    "La quantité doit être supérieure à zéro."
            );

            return true;
        }

        if (!MarketState.price.containsKey(item)) {

            sendError(
                    sender,
                    "Item inconnu dans le marché."
            );

            return true;
        }

        double price =
                MarketEngine.getPrice(item);

        switch (type) {

            case "sell" -> sendResult(
                    sender,
                    "SELL",
                    item,
                    amount,
                    price
            );

            case "buy" -> sendResult(
                    sender,
                    "BUY",
                    item,
                    amount,
                    price
            );

            default -> sendError(
                    sender,
                    "Type invalide. Utilisez §ebuy §7ou §esell§7."
            );
        }

        return true;
    }

    private void sendResult(
            CommandSender sender,
            String type,
            String item,
            int amount,
            double price
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§a✔ §fTest marché effectué.");
        sender.sendMessage("");
        sender.sendMessage("§7Type: §e" + type);
        sender.sendMessage("§7Item: §e" + item);
        sender.sendMessage("§7Quantité: §e" + amount);
        sender.sendMessage("§7Prix actuel: §6" + String.format("%.2f", price) + "€");
        sender.sendMessage("§7Total estimé: §e" + String.format("%.2f", price * amount) + "€");
        sender.sendMessage("");
        sender.sendMessage("§8• §7Aucune transaction réelle");
        sender.sendMessage("§8• §7Test réservé au staff");
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private void sendUsage(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§fCommande de test marché.");
        sender.sendMessage("");
        sender.sendMessage("§7Utilisation:");
        sender.sendMessage("§e/ecotest <buy/sell> <item> <quantité>");
        sender.sendMessage("");
        sender.sendMessage("§8• §7Exemple: §e/ecotest sell diamond 64");
        sender.sendMessage("§8• §7Aucune transaction réelle");
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private void sendError(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§c✘ §fAction refusée.");
        sender.sendMessage("");
        sender.sendMessage("§7" + message);
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }
}