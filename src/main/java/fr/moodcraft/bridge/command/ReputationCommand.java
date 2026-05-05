package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.ReputationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ReputationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // 🔥 ALIAS /toprep
        if (label.equalsIgnoreCase("toprep")) {
            args = new String[]{"top"};
        }

        // =========================
        // 👤 /reputation
        // =========================
        if (args.length == 0) {

            if (!(sender instanceof Player p)) return true;

            int rep = ReputationManager.get(p.getUniqueId().toString());

            p.sendMessage("§6=== Ta Réputation ===");
            p.sendMessage("§e" + rep + " points");
            p.sendMessage("§7Rang: " + ReputationManager.getRank(rep));

            return true;
        }

        // =========================
        // 🏆 /reputation top
        // =========================
        if (args[0].equalsIgnoreCase("top")) {

            sender.sendMessage("§6§l🏆 TOP RÉPUTATION");

            int i = 1;
            for (Map.Entry<String, Integer> entry : ReputationManager.getTop(10).entrySet()) {

                String name = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey())).getName();

                sender.sendMessage("§e#" + i + " §7" + name + " §8» §a" + entry.getValue());
                i++;
            }

            return true;
        }

        // =========================
        // 🔒 ADMIN REQUIRED
        // =========================
        if (!sender.hasPermission("moodcraft.admin")) {
            sender.sendMessage("§cPermission refusée");
            return true;
        }

        // =========================
        // 🔥 /rep reset <joueur>
        // =========================
        if (args[0].equalsIgnoreCase("reset")) {

            if (args.length < 2) {
                sender.sendMessage("§cUsage: /rep reset <joueur>");
                return true;
            }

            OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);

            if (!offline.hasPlayedBefore() && !offline.isOnline()) {
                sender.sendMessage("§cJoueur inconnu");
                return true;
            }

            ReputationManager.reset(offline.getUniqueId().toString());

            sender.sendMessage("§aRéputation de §e" + offline.getName() + " §aréinitialisée");
            return true;
        }

        // =========================
        // ⚙️ /rep <joueur> <valeur>
        // =========================
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /rep <joueur> <valeur>");
            return true;
        }

        OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

        if (!offline.hasPlayedBefore() && !offline.isOnline()) {
            sender.sendMessage("§cJoueur inconnu");
            return true;
        }

        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage("§cValeur invalide");
            return true;
        }

        String id = offline.getUniqueId().toString();

        int newValue = Math.max(0, ReputationManager.get(id) + value);
        ReputationManager.set(id, newValue);

        sender.sendMessage("§aRéputation de §e" + offline.getName() + " §amodifiée: §e" + newValue);

        return true;
    }
}