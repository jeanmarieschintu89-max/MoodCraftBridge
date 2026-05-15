package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.gui.EcoTrackGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrackEcoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (!player.hasPermission("moodcraft.admin") && !player.hasPermission("econ.admin")) {
            error(player, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 1) {
            header(player);
            player.sendMessage("§e➜ §7Utilisation : §e/trackeco <joueur>");
            footer(player);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        EcoTrackGUI.open(player, target);
        return true;
    }

    private void error(Player player, String message) {
        header(player);
        player.sendMessage("§c✖ §f" + message);
        footer(player);
    }

    private void header(Player player) {
        player.sendMessage("");
        player.sendMessage("§8----- §6✦ TrackEco ✦ §8-----");
    }

    private void footer(Player player) {
        player.sendMessage("§8-----------------------------");
    }
}
