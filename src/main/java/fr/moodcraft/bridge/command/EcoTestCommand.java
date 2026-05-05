package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;

import org.bukkit.command.*;

public class EcoTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /ecotest <buy/sell> <item> <amount>");
            return true;
        }

        String type = args[0].toLowerCase();
        String item = args[1].toLowerCase();
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage("§cNombre invalide");
            return true;
        }

        // ✔ vérifie si item existe
        if (!MarketState.prices.containsKey(item)) {
            sender.sendMessage("§cItem inconnu");
            return true;
        }

        double price = MarketEngine.getPrice(item);

        switch (type) {

            case "sell" -> {
                sender.sendMessage("§cTest SELL: " + item + " x" + amount);
                sender.sendMessage("§7Prix actuel: §e" + String.format("%.2f", price) + "€");
            }

            case "buy" -> {
                sender.sendMessage("§aTest BUY: " + item + " x" + amount);
                sender.sendMessage("§7Prix actuel: §e" + String.format("%.2f", price) + "€");
            }

            default -> sender.sendMessage("§cType invalide (buy/sell)");
        }

        return true;
    }
}