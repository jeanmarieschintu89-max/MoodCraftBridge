
package fr.moodcraft.bridge.listener;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import fr.moodcraft.bridge.manager.GUIManager;

public class GlobalGUIListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void click(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p))
            return;

        String id =
                GUIManager.get(p);

        if (id == null)
            return;

        //
        // 🌌 CREATE CONTRACT
        //

        if (id.equals("create_contract")) {

            //
            // ❌ ANTI BYPASS
            //

            if (e.isShiftClick()
                    || e.getClick().isKeyboardClick()
                    || e.getClick() == ClickType.NUMBER_KEY
                    || e.getClick() == ClickType.DOUBLE_CLICK
                    || e.getClick() == ClickType.DROP
                    || e.getClick() == ClickType.CONTROL_DROP) {

                e.setCancelled(true);

                return;
            }

            //
            // ✅ SLOT 13 LIBRE
            //

            if (e.getRawSlot() == 13) {

                e.setCancelled(false);

                return;
            }

            //
            // ✅ INVENTAIRE JOUEUR AUTORISÉ
            //

            if (e.getClickedInventory()
                    == e.getView().getBottomInventory()) {

                e.setCancelled(false);

                return;
            }
        }

        //
        // 🔥 BLOCK ALL
        //

        e.setCancelled(true);

        //
        // 🔒 UNIQUEMENT GUI TOP
        //

        if (e.getClickedInventory()
                != e.getView().getTopInventory())
            return;

        int slot =
                e.getSlot();

        //
        // 🔒 SLOT SAFE
        //

        if (slot < 0
                || slot >= e.getView()
                .getTopInventory()
                .getSize())
            return;

        //
        // 🔥 HANDLE
        //

        GUIManager.handle(
                p,
                slot
        );
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {

        if (!(e.getWhoClicked() instanceof Player p))
            return;

        String id =
                GUIManager.get(p);

        if (id == null)
            return;

        //
        // 🌌 CREATE CONTRACT
        //

        if (id.equals("create_contract")) {

            //
            // ❌ ANTI BYPASS
            //

            if (e.getRawSlots().size() > 1) {

                e.setCancelled(true);

                return;
            }

            //
            // ✅ SLOT 13 AUTORISÉ
            //

            if (e.getRawSlots().contains(13)) {

                e.setCancelled(false);

                return;
            }
        }

        //
        // 🔒 BLOCK ALL
        //

        e.setCancelled(true);
    }
}