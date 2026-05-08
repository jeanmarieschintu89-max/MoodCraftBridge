package fr.moodcraft.bridge.listener;

import org.bukkit.Material;

import org.bukkit.entity.Item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.ItemSpawnEvent;

public class VaultItemSpawnListener
        implements Listener {

    @EventHandler
    public void onSpawn(
            ItemSpawnEvent event
    ) {

        Item item =
                event.getEntity();

        Material m =
                item.getItemStack()
                        .getType();

        //
        // ❌ ITEMS BLOQUÉS
        //

        boolean blocked =

                m == Material.DIAMOND
                || m == Material.EMERALD
                || m == Material.NETHERITE_INGOT
                || m == Material.ANCIENT_DEBRIS
                || m == Material.GOLD_INGOT
                || m == Material.IRON_INGOT
                || m == Material.RAW_GOLD
                || m == Material.RAW_IRON;

        //
        // 🗑 DELETE INSTANT
        //

        if (blocked) {

            item.remove();
        }
    }
}