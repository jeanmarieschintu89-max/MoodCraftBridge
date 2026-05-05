package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.TrendManager;
import fr.moodcraft.bridge.market.MarketState; // ✅ FIX

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetTrendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /trend <item>");
            return true;
        }

        String item = args[0].toLowerCase();

        // 🔒 sécurité
        if (!MarketState.base.containsKey(item)) {
            sender.sendMessage("§cItem inconnu.");
            return true;
        }

        String trend = TrendManager.getTrend(item);

        sender.sendMessage("§6📊 Marché: §e" + item);
        sender.sendMessage("§7Tendance: " + trend);

        return true;
    }
}