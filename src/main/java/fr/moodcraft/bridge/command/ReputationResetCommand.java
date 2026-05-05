package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.ReputationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

public class ReputationResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("moodcraft.admin")) {
            sender.sendMessage("§cPermission refusée");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /resetrep <joueur>");
            return true;
        }

        OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

        if (!offline.hasPlayedBefore() && !offline.isOnline()) {
            sender.sendMessage("§cJoueur inconnu");
            return true;
        }

        ReputationManager.reset(offline.getUniqueId().toString());

        sender.sendMessage("§aRéputation de §e" + offline.getName() + " §aréinitialisée");

        return true;
    }
}