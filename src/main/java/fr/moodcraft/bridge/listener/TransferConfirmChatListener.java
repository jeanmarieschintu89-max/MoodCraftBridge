package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.handler.TransferConfirmHandler;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TransferConfirmChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(
            AsyncPlayerChatEvent event
    ) {

        Player player =
                event.getPlayer();

        if (!isPendingTransfer(player)) {
            return;
        }

        String message =
                event.getMessage()
                        .trim()
                        .toLowerCase();

        if (message.equals("confirmer")
                || message.equals("confirm")
                || message.equals("oui")) {

            event.setCancelled(true);

            Bukkit.getScheduler().runTask(
                    fr.moodcraft.bridge.Main.getInstance(),
                    () -> new TransferConfirmHandler().onClick(
                            player,
                            15
                    )
            );

            return;
        }

        if (message.equals("annuler")
                || message.equals("cancel")
                || message.equals("non")) {

            event.setCancelled(true);

            Bukkit.getScheduler().runTask(
                    fr.moodcraft.bridge.Main.getInstance(),
                    () -> {

                        TransferBuilder.clear(player);

                        player.sendMessage("");
                        player.sendMessage("§8----- §6✦ Banque §aMood§6Craft ✦ §8-----");
                        player.sendMessage("§e➜ §fVirement annulé.");
                        player.sendMessage("§8• §7Aucun argent n'a été envoyé.");
                        player.sendMessage("§8-----------------------------");
                        player.sendMessage("");

                        player.playSound(
                                player.getLocation(),
                                Sound.ENTITY_VILLAGER_NO,
                                1f,
                                0.85f
                        );
                    }
            );
        }
    }

    private boolean isPendingTransfer(
            Player player
    ) {

        return player != null
                && TransferBuilder.getAction(player) == TransferBuilder.Action.PLAYER_TRANSFER
                && TransferBuilder.hasTarget(player)
                && TransferBuilder.hasAmount(player);
    }
}
