package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;

import fr.moodcraft.bridge.manager.PriceUpdater;
import fr.moodcraft.bridge.manager.ShopIndex;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoReloadCommand implements CommandExecutor {

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

        MarketEngine.reload();

        ShopIndex.rebuild();

        int updated =
                0;

        for (String item : MarketState.price.keySet()) {

            PriceUpdater.updateItem(item);

            updated++;
        }

        sendSuccess(
                sender,
                updated
        );

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§a✔ §fMarché rechargé.");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8• §7Prix mis à jour");
        Bukkit.broadcastMessage("§8• §7Shops resynchronisés");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8-----------------------------");
        Bukkit.broadcastMessage("");

        return true;
    }

    private void sendSuccess(
            CommandSender sender,
            int updated
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----");
        sender.sendMessage("");
        sender.sendMessage("§a✔ §fReload effectué.");
        sender.sendMessage("");
        sender.sendMessage("§7Items mis à jour: §e" + updated);
        sender.sendMessage("");
        sender.sendMessage("§8• §7Marché rechargé");
        sender.sendMessage("§8• §7Shops resynchronisés");
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