package fr.moodcraft.bridge.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand implements CommandExecutor {

    private static final PotionEffectType NIGHT_VISION = PotionEffectType.NIGHT_VISION;
    public static final String PERMISSION = "moodcraftbridge.nightvision";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        toggle(player);
        return true;
    }

    public static void toggle(Player player) {
        if (player == null) return;

        if (!player.hasPermission(PERMISSION)) {
            header(player);
            player.sendMessage("§c✖ §fCommande réservée aux joueurs autorisés.");
            footer(player);
            return;
        }

        if (player.hasPotionEffect(NIGHT_VISION)) {
            player.removePotionEffect(NIGHT_VISION);
            header(player);
            player.sendMessage("§c✖ §fVision nocturne désactivée.");
            footer(player);
            return;
        }

        player.addPotionEffect(new PotionEffect(
                NIGHT_VISION,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                true
        ));

        header(player);
        player.sendMessage("§a✔ §fVision nocturne activée.");
        player.sendMessage("§8• §7Refais §e/nv §7pour la désactiver.");
        footer(player);
    }

    private static void header(Player player) {
        player.sendMessage("§8----- §6✦ §aMood§6Craft §fVision nocturne ✦ §8-----");
    }

    private static void footer(Player player) {
        player.sendMessage("§8-----------------------------");
    }
}
