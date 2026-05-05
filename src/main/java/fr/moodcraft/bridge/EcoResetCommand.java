package fr.moodcraft.bridge;

import fr.moodcraft.market.MarketAPI;
import org.bukkit.command.*;

public class EcoResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        MarketAPI.reset();

        ShopIndex.rebuild();

        for (String item : MarketAPI.getItems()) {
            PriceUpdater.updateItem(item);
        }

        sender.sendMessage("§a✔ Reset économique terminé");

        return true;
    }
}