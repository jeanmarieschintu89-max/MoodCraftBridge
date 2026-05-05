package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;
import fr.moodcraft.bridge.market.ShopIndex;
import fr.moodcraft.bridge.manager.PriceUpdater;

import org.bukkit.command.*;

public class EcoResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // ♻ Reset marché
        MarketEngine.reset();

        // 🔄 Rebuild shops
        ShopIndex.rebuild();

        // 📊 Update tous les items
        for (String item : MarketState.prices.keySet()) {
            PriceUpdater.updateItem(item);
        }

        sender.sendMessage("§a✔ Reset économique terminé");

        return true;
    }
}