package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.VotePanelManager;
import fr.moodcraft.bridge.manager.VoteTopService;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VotePanelCommand implements CommandExecutor {

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
            case "set" -> set(player, args);
            case "clear", "remove", "delete" -> clear(player, args);
            case "refresh", "update" -> refresh(player);
            default -> help(player);
        }

        return true;
    }

    private void set(Player player, String[] args) {
        if (args.length < 2) {
            usage(player, "/votepanel set <1-10>");
            return;
        }

        Integer rank = parseRank(args[1]);
        if (rank == null) {
            error(player, "Rang invalide. Utilise un nombre entre §e1 §7et §e10§7.");
            return;
        }

        Block target = player.getTargetBlockExact(6);
        if (target == null || !(target.getState() instanceof Sign)) {
            error(player, "Regarde un panneau à moins de 6 blocs.");
            return;
        }

        VotePanelManager.setPanel(rank, target.getLocation());

        header(player, "Panneau Votes");
        player.sendMessage("§a✔ §fPanneau lié au rang §6#" + rank + "§f.");
        player.sendMessage("§8• §7Source : §e" + VoteTopService.sourceUrl());
        player.sendMessage("§8• §7Actualisation site : cache de §e5 minutes§7.");
        footer(player);
    }

    private void clear(Player player, String[] args) {
        if (args.length < 2) {
            usage(player, "/votepanel clear <1-10>");
            return;
        }

        Integer rank = parseRank(args[1]);
        if (rank == null) {
            error(player, "Rang invalide. Utilise un nombre entre §e1 §7et §e10§7.");
            return;
        }

        VotePanelManager.clearPanel(rank);

        header(player, "Panneau Votes");
        player.sendMessage("§a✔ §fPanneau du rang §6#" + rank + " §fretiré.");
        footer(player);
    }

    private void refresh(Player player) {
        VotePanelManager.forceRefresh();

        header(player, "Panneau Votes");
        player.sendMessage("§a✔ §fLecture du classement vote relancée.");
        player.sendMessage("§8• §7Les panneaux se mettront à jour dès réception du site.");
        footer(player);
    }

    private Integer parseRank(String text) {
        try {
            int rank = Integer.parseInt(text);
            if (rank < 1 || rank > 10) return null;
            return rank;
        } catch (Exception e) {
            return null;
        }
    }

    private void help(Player player) {
        header(player, "Panneau Votes");
        player.sendMessage("§e➜ §7/votepanel set <1-10>");
        player.sendMessage("§e➜ §7/votepanel refresh");
        player.sendMessage("§e➜ §7/votepanel clear <1-10>");
        player.sendMessage("§8• §7Regarde le panneau avant d'utiliser §e/set§7.");
        footer(player);
    }

    private void usage(Player player, String usage) {
        header(player, "Panneau Votes");
        player.sendMessage("§c✖ §fCommande incomplète.");
        player.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(player);
    }

    private void error(Player player, String message) {
        header(player, "Panneau Votes");
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
