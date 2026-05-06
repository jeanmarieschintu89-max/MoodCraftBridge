package fr.moodcraft.bridge.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeCommand implements CommandExecutor {

    //
    // ❄ JOUEURS FREEZE
    //

    private static final Set<UUID> frozen =
            new HashSet<>();

    //
    // 🔍 CHECK
    //

    public static boolean isFrozen(Player p) {

        return frozen.contains(
                p.getUniqueId()
        );
    }

    //
    // ❄ FREEZE
    //

    public static void freeze(Player p) {

        frozen.add(
                p.getUniqueId()
        );
    }

    //
    // 🔥 UNFREEZE
    //

    public static void unfreeze(Player p) {

        frozen.remove(
                p.getUniqueId()
        );
    }

    //
    // 📜 COMMAND
    //

    @Override
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String label,
                             String[] args) {

        //
        // 🔒 PERMISSION
        //

        if (!sender.hasPermission("moodcraft.admin")) {

            sender.sendMessage("§c❌ Permission refusée.");
            return true;
        }

        //
        // 📛 USAGE
        //

        if (args.length < 1) {

            sender.sendMessage("§cUsage: /freeze <joueur>");
            return true;
        }

        //
        // 👤 TARGET
        //

        Player target =
                Bukkit.getPlayer(args[0]);

        if (target == null) {

            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        //
        // 🔄 TOGGLE
        //

        if (isFrozen(target)) {

            unfreeze(target);

            //
            // ✨ MESSAGE
            //

            target.sendTitle(
                    "§aDéfreeze",
                    "§7Tu peux rebouger.",
                    10,
                    40,
                    10
            );

            target.sendMessage("");
            target.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("§a✔ Tu n'es plus freeze.");
            target.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("");

            target.playSound(
                    target.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.2f
            );

            sender.sendMessage(
                    "§a✔ Joueur défreeze: §e"
                            + target.getName()
            );

        } else {

            freeze(target);

            //
            // ✨ MESSAGE
            //

            target.sendTitle(
                    "§cFreeze",
                    "§7Ne quitte pas le serveur.",
                    10,
                    999999,
                    10
            );

            target.sendMessage("");
            target.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("§c❄ Tu as été freeze.");
            target.sendMessage("§7Ne déconnecte pas.");
            target.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("");

            target.playSound(
                    target.getLocation(),
                    Sound.BLOCK_GLASS_BREAK,
                    1f,
                    0.7f
            );

            sender.sendMessage(
                    "§b❄ Joueur freeze: §e"
                            + target.getName()
            );
        }

        return true;
    }
}