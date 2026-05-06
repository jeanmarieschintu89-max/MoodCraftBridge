package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.command.FreezeCommand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();

        if (!FreezeCommand.isFrozen(p)) return;

        if (e.getFrom().getX() != e.getTo().getX()
                || e.getFrom().getY() != e.getTo().getY()
                || e.getFrom().getZ() != e.getTo().getZ()) {

            e.setTo(e.getFrom());
        }
    }
}