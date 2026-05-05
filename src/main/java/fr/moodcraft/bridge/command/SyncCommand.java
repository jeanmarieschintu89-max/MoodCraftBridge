package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.PriceUpdater;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // 🔒 permission (IMPORTANT)
        if (!sender.hasPermission("econ.admin")) {
            sender.sendMessage("§c❌ Permission refusée.");
            return true;
        }

        int count = 0;

        for (String item : PriceUpdater.ALLOWED) {
            PriceUpdater.updateItem(item);
            count++;
        }

        sender.sendMessage("§a✔ Sync effectué (§f" + count + " items§a)");
        return true;
    }
}