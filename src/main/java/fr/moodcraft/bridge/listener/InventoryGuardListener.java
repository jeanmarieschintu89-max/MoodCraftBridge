package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.manager.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryGuardListener implements Listener {

    @EventHandler
    public void drag(InventoryDragEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        // 🧠 si pas un GUI custom → on laisse faire
        if (GUIManager.get(p) == null) return;

        // 🔒 bloque tout drag dans nos GUI
        e.setCancelled(true);
    }
}