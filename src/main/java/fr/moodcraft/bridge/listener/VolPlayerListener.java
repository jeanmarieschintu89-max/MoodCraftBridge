package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.manager.VolManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class VolPlayerListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!VolManager.isEnabled()) return;
        if (!VolManager.isActive(event.getPlayer().getUniqueId())) return;

        VolManager.stopFlight(event.getPlayer(), true);
    }
}
