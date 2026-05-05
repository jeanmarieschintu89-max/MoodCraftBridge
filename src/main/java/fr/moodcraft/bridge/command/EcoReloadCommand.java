package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;
import fr.moodcraft.bridge.market.ShopIndex;
import fr.moodcraft.bridge.manager.PriceUpdater;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

public class EcoReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // 🔄 reload marché
        MarketEngine.reload();

        // 🔁 rebuild shops (optionnel si tu n’as pas ShopIndex)
        ShopIndex.rebuild();

        // 📊 refresh des prix
        for (String item : MarketState.prices.keySet()) {
            PriceUpdater.updateItem(item);
        }

        Bukkit.broadcastMessage("§6🏦 Économie rechargée");
        sender.sendMessage("§a✔ Reload complet effectué");

        return true;
    }
}