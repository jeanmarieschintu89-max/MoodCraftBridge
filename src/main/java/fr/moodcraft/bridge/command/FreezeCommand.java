package fr.moodcraft.bridge.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeCommand
        implements CommandExecutor {

    //
    // ❄ JOUEURS FREEZE
    //

    private static final Set<UUID> frozen =
            new HashSet<>();

    //
    // 🔍 CHECK
    //

    public static boolean isFrozen(
            Player p
    ) {

        return frozen.contains(
                p.getUniqueId()
        );
    }

    //
    // ❄ FREEZE
    //

    public static void freeze(
            Player p
    ) {

        frozen.add(
                p.getUniqueId()
        );
    }

    //
    // 🔥 UNFREEZE
    //

    public static void unfreeze(
            Player p
    ) {

        frozen.remove(
                p.getUniqueId()
        );
    }

    //
    // 📜 COMMAND
    //

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        //
        // 🔒 PERMISSION
        //

        if (!sender.hasPermission("moodcraft.admin")) {

            sendError(
                    sender,
                    "Sécurité Staff",
                    "Permission refusée."
            );

            return true;
        }

        //
        // 📛 USAGE
        //

        if (args.length < 1) {

            sendInfo(
                    sender,
                    "Sécurité Staff",
                    "Utilisation : §e/freeze <joueur>"
            );

            return true;
        }

        //
        // 👤 TARGET
        //

        Player target =
                Bukkit.getPlayer(args[0]);

        if (target == null) {

            sendError(
                    sender,
                    "Sécurité Staff",
                    "Joueur introuvable ou hors ligne."
            );

            return true;
        }

        //
        // 🔄 TOGGLE
        //

        if (isFrozen(target)) {

            unfreeze(target);

            sendUnfreezeTarget(
                    target
            );

            sendSuccess(
                    sender,
                    "Sécurité Staff",
                    "Joueur défreeze : §e" + target.getName()
            );

            return true;
        }

        freeze(target);

        sendFreezeTarget(
                target
        );

        sendSuccess(
                sender,
                "Sécurité Staff",
                "Joueur freeze : §e" + target.getName()
        );

        return true;
    }

    //
    // ❄ MESSAGE TARGET FREEZE
    //

    private void sendFreezeTarget(
            Player target
    ) {

        target.sendTitle(
                "§cFreeze",
                "§7Attends les consignes du staff.",
                10,
                999999,
                10
        );

        target.sendMessage("");
        target.sendMessage("§8----- §6✦ Sécurité §aMood§6Craft ✦ §8-----");
        target.sendMessage("§c✖ §fTu as été freeze.");
        target.sendMessage("§8• §7Ne quitte pas le serveur");
        target.sendMessage("§8• §7Attends les consignes du staff");
        target.sendMessage("§8• §7Reste calme pendant la vérification");
        target.sendMessage("§8-----------------------------");
        target.sendMessage("");

        target.playSound(
                target.getLocation(),
                Sound.BLOCK_GLASS_BREAK,
                1f,
                0.7f
        );
    }

    //
    // 🔥 MESSAGE TARGET UNFREEZE
    //

    private void sendUnfreezeTarget(
            Player target
    ) {

        target.sendTitle(
                "§aDéfreeze",
                "§7Tu peux rebouger.",
                10,
                40,
                10
        );

        target.sendMessage("");
        target.sendMessage("§8----- §6✦ Sécurité §aMood§6Craft ✦ §8-----");
        target.sendMessage("§a✔ §fTu n'es plus freeze.");
        target.sendMessage("§8• §7Tu peux à nouveau bouger");
        target.sendMessage("§8• §7Merci d'avoir patienté");
        target.sendMessage("§8-----------------------------");
        target.sendMessage("");

        target.playSound(
                target.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                1f,
                1.2f
        );
    }

    //
    // ✅ SUCCESS
    //

    private void sendSuccess(
            CommandSender sender,
            String title,
            String message
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
        sender.sendMessage("§a✔ §f" + message);
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    //
    // ❌ ERROR
    //

    private void sendError(
            CommandSender sender,
            String title,
            String message
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
        sender.sendMessage("§c✖ §fAction refusée.");
        sender.sendMessage("§8• §7" + message);
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    //
    // ℹ INFO
    //

    private void sendInfo(
            CommandSender sender,
            String title,
            String message
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
        sender.sendMessage("§e➜ §f" + message);
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }
}
