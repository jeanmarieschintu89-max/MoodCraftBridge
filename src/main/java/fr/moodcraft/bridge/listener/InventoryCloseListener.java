package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.manager.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (!(e.getPlayer() instanceof Player p)) return;

        // 🔥 ignore la fermeture déclenchée par une ouverture en cours
        if (GUIManager.isOpening(p)) return;

        GUIManager.close(p);
    }
}