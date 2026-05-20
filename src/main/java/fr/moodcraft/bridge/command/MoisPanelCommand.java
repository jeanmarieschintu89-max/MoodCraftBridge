package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.MoisPanelManager;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoisPanelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (!player.hasPermission("econ.admin") && !player.hasPermission("moodcraft.admin")) {
            error(player, "Accès réservé à l'administration.");
            return true;
        }

        if (args.length < 1) {
            help(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "set" -> set(player);
            case "clear", "remove", "delete" -> clear(player);
            case "refresh", "update" -> refresh(player);
            default -> help(player);
        }

        return true;
    }

    private void set(Player player) {
        Block target = player.getTargetBlockExact(6);

        if (target == null || !(target.getState() instanceof Sign)) {
            error(player, "Regarde un panneau à moins de 6 blocs.");
            return;
        }

        MoisPanelManager.setPanel(target.getLocation());

        header(player, "Panneau Mois");
        player.sendMessage("§a✔ §fPanneau lié au jour et mois actuels.");
        player.sendMessage("§8• §7Format : §eClassement vote §8/ §a21 mai §8/ §e2026");
        footer(player);
    }

    private void clear(Player player) {
        Block target = player.getTargetBlockExact(6);

        if (target == null || !(target.getState() instanceof Sign)) {
            error(player, "Regarde le panneau à supprimer à moins de 6 blocs.");
            return;
        }

        boolean removed = MoisPanelManager.clearPanel(target.getLocation());

        header(player, "Panneau Mois");
        player.sendMessage(removed
                ? "§a✔ §fPanneau mois retiré."
                : "§c✖ §fCe panneau n'était pas lié.");
        footer(player);
    }

    private void refresh(Player player) {
        MoisPanelManager.refresh();

        header(player, "Panneau Mois");
        player.sendMessage("§a✔ §fPanneaux mois actualisés.");
        footer(player);
    }

    private void help(Player player) {
        header(player, "Panneau Mois");
        player.sendMessage("§e➜ §7/moispanel set");
        player.sendMessage("§e➜ §7/moispanel refresh");
        player.sendMessage("§e➜ §7/moispanel clear");
        player.sendMessage("§8• §7Regarde le panneau avant d'utiliser §e/set§7.");
        footer(player);
    }

    private void error(Player player, String message) {
        header(player, "Panneau Mois");
        player.sendMessage("§c✖ §f" + message);
        footer(player);
    }

    private void header(Player player, String title) {
        player.sendMessage("");
        player.sendMessage("§8----- §6✦ §aMood§6Craft §f" + title + " ✦ §8-----");
    }

    private void footer(Player player) {
        player.sendMessage("§8-----------------------------");
        player.sendMessage("");
    }
}
