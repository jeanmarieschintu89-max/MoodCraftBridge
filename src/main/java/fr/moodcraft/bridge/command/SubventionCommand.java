package fr.moodcraft.bridge.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class SubventionCommand
        implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        //
        // 👤 PLAYER ONLY
        //

        if (!(sender instanceof Player p)) {

            sender.sendMessage(
                    "Commande joueur uniquement."
            );

            return true;
        }

        //
        // 🔒 PERMISSION
        //

        if (!p.hasPermission(
                "moodcraft.admin"
        )) {

            header(p);

            p.sendMessage("§c✘ §fAccès refusé.");
            p.sendMessage("");
            p.sendMessage("§7Commande réservée à");
            p.sendMessage("§7l'administration économique.");

            footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.85f
            );

            return true;
        }

        //
        // ❌ USAGE
        //

        if (args.length < 2) {

            header(p);

            p.sendMessage("§fCommande de subvention.");
            p.sendMessage("");
            p.sendMessage("§7Utilisation:");
            p.sendMessage("§e/subvention <ville> <montant>");
            p.sendMessage("");
            p.sendMessage("§8• §7Exemple: §e/subvention Tokyo 50000");
            p.sendMessage("§8• §7L'argent est versé à la banque de ville.");

            footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return true;
        }

        //
        // 📦 DATA
        //

        String town =
                args[0];

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            args[1]
                                    .replace(",", ".")
                    );

        } catch (Exception ex) {

            header(p);

            p.sendMessage("§c✘ §fMontant invalide.");
            p.sendMessage("");
            p.sendMessage("§7Écris un nombre correct.");
            p.sendMessage("§8• §7Exemple: §e50000");

            footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.85f
            );

            return true;
        }

        //
        // ❌ NEGATIVE
        //

        if (amount <= 0) {

            header(p);

            p.sendMessage("§c✘ §fMontant invalide.");
            p.sendMessage("");
            p.sendMessage("§7La subvention doit être");
            p.sendMessage("§7supérieure à zéro.");

            footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.85f
            );

            return true;
        }

        //
        // 💰 DEPOSIT
        //

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "ta deposit "
                        + town
                        + " "
                        + amount
        );

        //
        // 🔊 SOUND
        //

        for (Player online :
                Bukkit.getOnlinePlayers()) {

            online.playSound(
                    online.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    0.7f,
                    1f
            );

            online.playSound(
                    online.getLocation(),
                    Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    0.35f,
                    1.25f
            );
        }

        //
        // 🌍 BROADCAST
        //

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
                "§8----- §6✦ Ministère de l’Économie ✦ §8-----"
        );
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
                "§a✔ §fSubvention nationale accordée."
        );
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
                "§7Ville: §b" + town
        );
        Bukkit.broadcastMessage(
                "§7Montant: §e" + money(amount) + "€"
        );
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
                "§8• §7Fonds versés à la banque de ville"
        );
        Bukkit.broadcastMessage(
                "§8• §7Soutien au développement urbain"
        );
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
                "§aService officiel de §aMood§6Craft§a."
        );
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(
                "§8-----------------------------"
        );
        Bukkit.broadcastMessage("");

        return true;
    }

    //
    // 🎨 HEADER
    //

    private void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage(
                "§8----- §6✦ Ministère de l’Économie ✦ §8-----"
        );
        p.sendMessage("");
    }

    //
    // 🎨 FOOTER
    //

    private void footer(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage(
                "§8-----------------------------"
        );
        p.sendMessage("");
    }

    //
    // 💶 MONEY
    //

    private String money(
            double amount
    ) {

        return String.format(
                "%,.0f",
                amount
        ).replace(",", " ");
    }
}