package fr.moodcraft.bridge;

import fr.moodcraft.market.MarketAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

public class EcoReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // 🔥 reload market propre
        MarketAPI.reload();

        // 🔄 rebuild shops
        ShopIndex.rebuild();

        // 🔄 sync prix
        for (String item : MarketAPI.getItems()) {
            PriceUpdater.updateItem(item);
        }

        Bukkit.broadcastMessage("§6🏦 Économie rechargée");
        sender.sendMessage("§a✔ Reload complet effectué");

        return true;
    }
}