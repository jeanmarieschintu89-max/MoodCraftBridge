package fr.moodcraft.bridge.listener;

import org.bukkit.Material;

import org.bukkit.block.Block;

import org.bukkit.entity.Item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.ItemSpawnEvent;

import org.bukkit.inventory.ItemStack;

public class VaultLootBlockListener
        implements Listener {

    @EventHandler
    public void onItemSpawn(
            ItemSpawnEvent e
    ) {

        Item item =
                e.getEntity();

        ItemStack stack =
                item.getItemStack();

        if (stack == null) {
            return;
        }

        if (!isBlocked(
                stack.getType()
        )) {
            return;
        }

        if (!nearVault(
                item.getLocation()
                        .getBlock()
        )) {
            return;
        }

        e.setCancelled(true);
    }

    private boolean nearVault(
            Block center
    ) {

        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {

                    Block block =
                            center.getRelative(
                                    x,
                                    y,
                                    z
                            );

                    if (block.getType()
                            == Material.VAULT) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isBlocked(
            Material m
    ) {

        return m == Material.DIAMOND
                || m == Material.DIAMOND_BLOCK
                || m == Material.DIAMOND_ORE
                || m == Material.DEEPSLATE_DIAMOND_ORE

                || m == Material.EMERALD
                || m == Material.EMERALD_BLOCK
                || m == Material.EMERALD_ORE
                || m == Material.DEEPSLATE_EMERALD_ORE

                || m == Material.GOLD_INGOT
                || m == Material.GOLD_BLOCK
                || m == Material.RAW_GOLD
                || m == Material.RAW_GOLD_BLOCK
                || m == Material.GOLD_ORE
                || m == Material.DEEPSLATE_GOLD_ORE
                || m == Material.NETHER_GOLD_ORE

                || m == Material.IRON_INGOT
                || m == Material.IRON_BLOCK
                || m == Material.RAW_IRON
                || m == Material.RAW_IRON_BLOCK
                || m == Material.IRON_ORE
                || m == Material.DEEPSLATE_IRON_ORE

                || m == Material.COPPER_INGOT
                || m == Material.COPPER_BLOCK
                || m == Material.RAW_COPPER
                || m == Material.RAW_COPPER_BLOCK
                || m == Material.COPPER_ORE
                || m == Material.DEEPSLATE_COPPER_ORE

                || m == Material.COAL
                || m == Material.COAL_BLOCK
                || m == Material.COAL_ORE
                || m == Material.DEEPSLATE_COAL_ORE

                || m == Material.LAPIS_LAZULI
                || m == Material.LAPIS_BLOCK
                || m == Material.LAPIS_ORE
                || m == Material.DEEPSLATE_LAPIS_ORE

                || m == Material.REDSTONE
                || m == Material.REDSTONE_BLOCK
                || m == Material.REDSTONE_ORE
                || m == Material.DEEPSLATE_REDSTONE_ORE

                || m == Material.AMETHYST_SHARD
                || m == Material.AMETHYST_BLOCK
                || m == Material.BUDDING_AMETHYST

                || m == Material.QUARTZ
                || m == Material.QUARTZ_BLOCK
                || m == Material.NETHER_QUARTZ_ORE

                || m == Material.NETHERITE_INGOT
                || m == Material.ANCIENT_DEBRIS;
    }
}