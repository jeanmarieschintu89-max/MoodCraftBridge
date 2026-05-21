package fr.moodcraft.bridge.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand implements CommandExecutor {

    private static final PotionEffectType NIGHT_VISION = PotionEffectType.NIGHT_VISION;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (!player.hasPermission("moodcraftbridge.nightvision")) {
            player.sendMessage("§8----- §6✦ §aMood§6Craft §fVision nocturne ✦ §8-----");
            player.sendMessage("§c✖ §fCommande réservée aux joueurs autorisés.");
            player.sendMessage("§8-----------------------------");
            return true;
        }

        if (player.hasPotionEffect(NIGHT_VISION)) {
            player.removePotionEffect(NIGHT_VISION);
            player.sendMessage("§8----- §6✦ §aMood§6Craft §fVision nocturne ✦ §8-----");
            player.sendMessage("§c✖ §fVision nocturne désactivée.");
            player.sendMessage("§8-----------------------------");
            return true;
        }

        player.addPotionEffect(new PotionEffect(
                NIGHT_VISION,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                true
        ));

        player.sendMessage("§8----- §6✦ §aMood§6Craft §fVision nocturne ✦ §8-----");
        player.sendMessage("§a✔ §fVision nocturne activée.");
        player.sendMessage("§8• §7Refais §e/nv §7pour la désactiver.");
        player.sendMessage("§8-----------------------------");

        return true;
    }
}