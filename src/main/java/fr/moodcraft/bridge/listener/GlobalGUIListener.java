package fr.moodcraft.bridge.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class GlobalGUIListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void click(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        String id = GUIManager.get(p);
        if (id == null) return;

        // 🔥 bloque TOUT
        e.setCancelled(true);

        // 🔒 uniquement GUI du haut
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        int slot = e.getSlot();

        // 🔒 sécurité slot
        if (slot < 0 || slot >= e.getView().getTopInventory().getSize()) return;

        // 🔒 anti bypass complet
        if (e.isShiftClick()
                || e.getClick().isKeyboardClick()
                || e.getClick() == ClickType.NUMBER_KEY
                || e.getClick() == ClickType.DOUBLE_CLICK
                || e.getClick() == ClickType.DROP
                || e.getClick() == ClickType.CONTROL_DROP) {
            return;
        }

        // 🔥 handle sécurisé
        GUIManager.handle(p, slot);
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (GUIManager.get(p) != null) {
            e.setCancelled(true);
        }
    }
}