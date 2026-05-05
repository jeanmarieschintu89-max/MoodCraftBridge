package fr.moodcraft.bridge.command;

import fr.moodcraft.reputation.ReputationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ReputationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        String id = p.getUniqueId().toString();
        int rep = ReputationManager.get(id);

        // 🎯 Perso
        p.sendMessage("§6=== Ta Réputation ===");
        p.sendMessage("§e" + rep + " points");
        p.sendMessage("§7Rang: " + ReputationManager.getRank(rep));

        // 🏆 Top
        p.sendMessage("§6=== Top Réputation ===");

        int rank = 1;

        for (Map.Entry<String, Integer> entry : ReputationManager.getTop(5).entrySet()) {

            String name = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey())).getName();

            p.sendMessage("§e#" + rank + " §7" + name + " §8» §a" + entry.getValue());

            rank++;
        }

        return true;
    }
}