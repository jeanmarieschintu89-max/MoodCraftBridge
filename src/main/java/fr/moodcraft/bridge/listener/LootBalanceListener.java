package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.Container;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryOpenEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LootBalanceListener
        implements Listener {

    @EventHandler
    public void onOpen(
            InventoryOpenEvent event
    ) {

        Inventory inv =
                event.getInventory();

        //
        // ❌ HOLDER
        //

        if (!(inv.getHolder()
                instanceof Container container)) {
            return;
        }

        Block block =
                container.getBlock();

        String type =
                block.getType()
                        .name()
                        .toLowerCase();

        //
        // 🏰 VAULTS + TRIALS
        //

        boolean protectedLoot =

                type.contains("vault")
                || type.contains("trial");

        if (!protectedLoot) {
            return;
        }

        //
        // ⏳ ATTEND QUE MC GÉNÈRE LE LOOT
        //

        Bukkit.getScheduler()
                .runTaskLater(

                        Main.getInstance(),

                        () -> cleanInventory(inv),

                        1L
                );
    }

    //
    // 🧹 CLEAN
    //

    private void cleanInventory(
            Inventory inv
    ) {

        for (int i = 0;
             i < inv.getSize();
             i++) {

            ItemStack item =
                    inv.getItem(i);

            if (item == null) {
                continue;
            }

            Material m =
                    item.getType();

            //
            // ❌ BLOQUÉS
            //

            boolean blocked =

                    // 💎
                    m == Material.DIAMOND
                    || m == Material.DIAMOND_BLOCK
                    || m == Material.DIAMOND_ORE
                    || m == Material.DEEPSLATE_DIAMOND_ORE

                    // 🟢
                    || m == Material.EMERALD
                    || m == Material.EMERALD_BLOCK
                    || m == Material.EMERALD_ORE
                    || m == Material.DEEPSLATE_EMERALD_ORE

                    // 🟡
                    || m == Material.GOLD_INGOT
                    || m == Material.GOLD_BLOCK
                    || m == Material.RAW_GOLD

                    // ⚪
                    || m == Material.IRON_INGOT
                    || m == Material.IRON_BLOCK
                    || m == Material.RAW_IRON

                    // 🔥
                    || m == Material.NETHERITE_INGOT
                    || m == Material.ANCIENT_DEBRIS;

            //
            // 🗑 REMOVE
            //

            if (blocked) {

                inv.setItem(i, null);
            }
        }
    }
}