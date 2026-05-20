package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.FortunePanelManager;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FortunePanelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (!player.hasPermission("econ.admin") && !player.hasPermission("moodcraft.admin")) {
            error(player, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 1) {
            help(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "set" -> set(player, args);
            case "clear", "remove", "delete" -> clear(player, args);
            case "refresh", "update" -> refresh(player);
            default -> help(player);
        }

        return true;
    }

    private void set(Player player, String[] args) {

        if (args.length < 2) {
            usage(player, "/fortunepanel set <1/2/3>");
            return;
        }

        Integer rank = parseRank(args[1]);
        if (rank == null) {
            error(player, "Rang invalide. Utilise §e1§7, §e2 §7ou §e3§7.");
            return;
        }

        Block target = player.getTargetBlockExact(6);

        if (target == null || !(target.getState() instanceof Sign)) {
            error(player, "Regarde un panneau à moins de 6 blocs.");
            return;
        }

        FortunePanelManager.setPanel(rank, target.getLocation());

        header(player, "Panneau Fortune");
        player.sendMessage("§a✔ §fPanneau lié au rang §6#" + rank + "§f.");
        player.sendMessage("§8• §7Il s'actualisera automatiquement toutes les §e10 secondes§7.");
        footer(player);
    }

    private void clear(Player player, String[] args) {

        if (args.length < 2) {
            usage(player, "/fortunepanel clear <1/2/3>");
            return;
        }

        Integer rank = parseRank(args[1]);
        if (rank == null) {
            error(player, "Rang invalide. Utilise §e1§7, §e2 §7ou §e3§7.");
            return;
        }

        FortunePanelManager.clearPanel(rank);

        header(player, "Panneau Fortune");
        player.sendMessage("§a✔ §fPanneau du rang §6#" + rank + " §fretiré.");
        footer(player);
    }

    private void refresh(Player player) {
        FortunePanelManager.refresh();

        header(player, "Panneau Fortune");
        player.sendMessage("§a✔ §fClassement fortune actualisé.");
        footer(player);
    }

    private Integer parseRank(String text) {
        try {
            int rank = Integer.parseInt(text);
            if (rank < 1 || rank > 3) return null;
            return rank;
        } catch (Exception e) {
            return null;
        }
    }

    private void help(Player player) {
        header(player, "Panneau Fortune");
        player.sendMessage("§e➜ §7/fortunepanel set <1/2/3>");
        player.sendMessage("§e➜ §7/fortunepanel refresh");
        player.sendMessage("§e➜ §7/fortunepanel clear <1/2/3>");
        player.sendMessage("§8• §7Regarde le panneau avant d'utiliser §e/set§7.");
        footer(player);
    }

    private void usage(Player player, String usage) {
        header(player, "Panneau Fortune");
        player.sendMessage("§c✖ §fCommande incomplète.");
        player.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(player);
    }

    private void error(Player player, String message) {
        header(player, "Panneau Fortune");
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