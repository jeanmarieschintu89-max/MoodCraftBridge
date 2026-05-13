package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.PriceUpdater;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("econ.admin")
                && !sender.hasPermission("moodcraft.admin")) {

            sendError(
                    sender,
                    "Accès réservé à l'administration."
            );

            return true;
        }

        int count = 0;

        for (String item : PriceUpdater.ALLOWED) {

            PriceUpdater.updateItem(item);

            count++;
        }

        sendSuccess(
                sender,
                count
        );

        return true;
    }

    private void sendSuccess(
            CommandSender sender,
            int count
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§a✔ §fSynchronisation effectuée.");
        sender.sendMessage("");
        sender.sendMessage("§7Items mis à jour: §e" + count);
        sender.sendMessage("");
        sender.sendMessage("§8• §7Prix envoyés aux shops");
        sender.sendMessage("§8• §7Marché actualisé");
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