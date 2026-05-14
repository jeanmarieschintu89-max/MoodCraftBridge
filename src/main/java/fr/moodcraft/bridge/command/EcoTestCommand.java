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
        sender.sendMessage(header());
        sender.sendMessage("");
        sender.sendMessage("§a✔ §fTest marché effectué.");
        sender.sendMessage("");
        sender.sendMessage(detail("Type : §e" + type));
        sender.sendMessage(detail("Item : §e" + item));
        sender.sendMessage(detail("Quantité : §e" + amount));
        sender.sendMessage(detail("Prix actuel : §6" + String.format("%.2f", price) + "€"));
        sender.sendMessage(detail("Total estimé : §e" + String.format("%.2f", price * amount) + "€"));
        sender.sendMessage("");
        sender.sendMessage(detail("Aucune transaction réelle"));
        sender.sendMessage(detail("Test réservé au staff"));
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
        sender.sendMessage("§e➜ §fCommande de test marché.");
        sender.sendMessage("");
        sender.sendMessage(detail("Utilisation : §e/ecotest <buy/sell> <item> <quantité>"));
        sender.sendMessage(detail("Exemple : §e/ecotest sell diamond 64"));
        sender.sendMessage(detail("Aucune transaction réelle"));
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
        sender.sendMessage("§c✖ §fAction refusée.");
        sender.sendMessage("");
        sender.sendMessage(detail(message));
        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    private String header() {
        return "§8----- §6✦ §aMood§6Craft §fÉconomie ✦ §8-----";
    }

    private String detail(String text) {
        return "§8• §7" + text;
    }
}
