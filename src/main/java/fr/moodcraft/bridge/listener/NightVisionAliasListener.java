package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.command.NightVisionCommand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class NightVisionAliasListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message == null || message.isBlank()) return;

        String command = message.substring(1).split(" ")[0].toLowerCase();

        if (!command.equals("nv")
                && !command.equals("nightvision")
                && !command.equals("visionnuit")
                && !command.equals("visionnocturne")) {
            return;
        }

        event.setCancelled(true);
        NightVisionCommand.toggle(event.getPlayer());
    }
}
