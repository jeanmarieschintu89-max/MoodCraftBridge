package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketAPI;
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

        if (!MarketAPI.hasItem(item)) {
            sender.sendMessage("§cItem inconnu");
            return true;
        }

        switch (type) {

            case "sell" -> {
                MarketAPI.testSell(item, amount);
                sender.sendMessage("§cTest SELL: " + item + " x" + amount);
            }

            case "buy" -> {
                MarketAPI.testBuy(item, amount);
                sender.sendMessage("§aTest BUY: " + item + " x" + amount);
            }

            default -> sender.sendMessage("§cType invalide (buy/sell)");
        }

        return true;
    }
}