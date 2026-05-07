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

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fAccès refusé"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Niveau d'autorisation insuffisant."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            return true;
        }

        //
        // ❌ USAGE
        //

        if (args.length < 2) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§6✦ §fAdministration Économique"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Utilisation:"
            );

            p.sendMessage(
                    "§e/subvention <ville> <montant>"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8Exemple: §e/subvention Tokyo 50000"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

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
                    );

        } catch (Exception ex) {

            p.sendMessage(
                    "§cMontant invalide."
            );

            return true;
        }

        //
        // ❌ NEGATIVE
        //

        if (amount <= 0) {

            p.sendMessage(
                    "§cMontant invalide."
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
        }

        //
        // 🌍 BROADCAST
        //

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        Bukkit.broadcastMessage(
                "§6✦ §fMinistère de l’Économie MoodCraft"
        );

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§7Le gouvernement économique annonce"
        );

        Bukkit.broadcastMessage(
                "§7une nouvelle subvention territoriale."
        );

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§e"
                        + town
                        + " §7reçoit une aide exceptionnelle"
        );

        Bukkit.broadcastMessage(
                "§7afin de soutenir son expansion"
        );

        Bukkit.broadcastMessage(
                "§7économique et urbaine."
        );

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§a✔ Fonds transférés: §e"
                        + String.format(
                        "%,.0f",
                        amount
                ).replace(",", " ")
                        + "€"
        );

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        Bukkit.broadcastMessage("");

        return true;
    }
}