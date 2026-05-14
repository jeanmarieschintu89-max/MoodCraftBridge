package fr.moodcraft.bridge.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class SubventionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (!p.hasPermission("moodcraft.admin")) {
            header(p);
            p.sendMessage("§c✖ §fAccès refusé.");
            p.sendMessage("§e➜ §7Commande réservée à l'administration économique.");
            footer(p);
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.85f);
            return true;
        }

        if (args.length < 2) {
            header(p);
            p.sendMessage("§e➜ §7Commande de subvention.");
            p.sendMessage("§e➜ §7Utilisation : §e/subvention <ville> <montant>");
            p.sendMessage("§e➜ §7Exemple : §e/subvention Tokyo 50000");
            p.sendMessage("§e➜ §7L'argent est versé à la banque de ville.");
            footer(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
            return true;
        }

        String town = args[0];
        double amount;

        try {
            amount = Double.parseDouble(args[1].replace(",", "."));
        } catch (Exception ex) {
            header(p);
            p.sendMessage("§c✖ §fMontant invalide.");
            p.sendMessage("§e➜ §7Écris un nombre correct.");
            p.sendMessage("§e➜ §7Exemple : §e50000");
            footer(p);
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.85f);
            return true;
        }

        if (amount <= 0) {
            header(p);
            p.sendMessage("§c✖ §fMontant invalide.");
            p.sendMessage("§e➜ §7La subvention doit être supérieure à zéro.");
            footer(p);
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.85f);
            return true;
        }

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "ta deposit " + town + " " + amount
        );

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.playSound(online.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1f);
            online.playSound(online.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.35f, 1.25f);
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8----- §6✦ Ministère de l’Économie ✦ §8-----");
        Bukkit.broadcastMessage("§a✔ §fSubvention nationale accordée.");
        Bukkit.broadcastMessage("§e➜ §7Ville : §b" + town);
        Bukkit.broadcastMessage("§e➜ §7Montant : §e" + money(amount) + "€");
        Bukkit.broadcastMessage("§e➜ §7Fonds versés à la banque de ville");
        Bukkit.broadcastMessage("§e➜ §7Soutien au développement urbain");
        Bukkit.broadcastMessage("§e➜ §7Service officiel de §aMood§6Craft§7.");
        Bukkit.broadcastMessage("§8-----------------------------");

        return true;
    }

    private void header(Player p) {
        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Ministère de l’Économie ✦ §8-----");
    }

    private void footer(Player p) {
        p.sendMessage("§8-----------------------------");
    }

    private String money(double amount) {
        return String.format("%,.0f", amount).replace(",", " ");
    }
}
