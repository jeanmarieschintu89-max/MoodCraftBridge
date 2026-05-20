package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.MoisPanelManager;
import fr.moodcraft.bridge.manager.MoisPanelManager.Mode;

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
            case "set" -> set(player, args);
            case "clear", "remove", "delete" -> clear(player);
            case "refresh", "update" -> refresh(player);
            default -> help(player);
        }

        return true;
    }

    private void set(Player player, String[] args) {
        Block target = player.getTargetBlockExact(6);

        if (target == null || !(target.getState() instanceof Sign)) {
            error(player, "Regarde un panneau à moins de 6 blocs.");
            return;
        }

        Mode mode = Mode.JOUR;

        if (args.length >= 2) {
            String type = args[1].toLowerCase();

            if (type.equals("mois") || type.equals("month")) {
                mode = Mode.MOIS;
            } else if (type.equals("jour") || type.equals("date") || type.equals("day")) {
                mode = Mode.JOUR;
            } else if (type.equals("recompenses") || type.equals("récompenses") || type.equals("reward") || type.equals("rewards") || type.equals("topvote")) {
                mode = Mode.RECOMPENSES;
            } else {
                error(player, "Type invalide. Utilise §emois§7, §ejour §7ou §erecompenses§7.");
                return;
            }
        }

        MoisPanelManager.setPanel(target.getLocation(), mode);

        header(player, "Panneau Mois");
        player.sendMessage("§a✔ §fPanneau lié au mode §e" + mode.name().toLowerCase() + "§f.");
        player.sendMessage(switch (mode) {
            case MOIS -> "§8• §7Format : §eClassement vote §8/ §aMai 2026";
            case JOUR -> "§8• §7Format : §eClassement vote §8/ §a21 Mai";
            case RECOMPENSES -> "§8• §7Format : §eRécompenses §8/ §fTop Vote §8/ §aMai 2026";
        });
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
        player.sendMessage("§e➜ §7/moispanel set mois");
        player.sendMessage("§e➜ §7/moispanel set jour");
        player.sendMessage("§e➜ §7/moispanel set recompenses");
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