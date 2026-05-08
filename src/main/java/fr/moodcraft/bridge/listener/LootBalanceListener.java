package fr.moodcraft.bridge.listener;

import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.Container;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryOpenEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LootBalanceListener implements Listener {

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {

        // =========================
        // 📦 INVENTAIRE
        // =========================

        Inventory inv =
                event.getInventory();

        // =========================
        // 🧱 VÉRIFIE COFFRE/BLOCK
        // =========================

        if (!(inv.getHolder() instanceof Container container)) {
            return;
        }

        Block block =
                container.getBlock();

        // =========================
        // 🏰 STRUCTURES TRIAL
        // =========================

        String blockType =
                block.getType().name().toLowerCase();

        boolean protectedLoot =

                blockType.contains("vault")
                || blockType.contains("trial");

        // ⚠️ Si ce n'est pas un vault/coffre trial
        if (!protectedLoot) {
            return;
        }

        // =========================
        // 💰 NETTOYAGE ÉCONOMIE
        // =========================

        for (ItemStack item :
                inv.getContents()) {

            if (item == null) {
                continue;
            }

            Material m =
                    item.getType();

            // =========================
            // ❌ MINERAIS BLOQUÉS
            // =========================

            boolean blocked =

                    // 💎 DIAMANTS
                    m == Material.DIAMOND
                    || m == Material.DIAMOND_BLOCK

                    || m == Material.DIAMOND_ORE
                    || m == Material.DEEPSLATE_DIAMOND_ORE

                    // 🟢 ÉMERAUDES
                    || m == Material.EMERALD
                    || m == Material.EMERALD_BLOCK

                    || m == Material.EMERALD_ORE
                    || m == Material.DEEPSLATE_EMERALD_ORE

                    // 🟡 OR
                    || m == Material.GOLD_INGOT
                    || m == Material.GOLD_BLOCK
                    || m == Material.RAW_GOLD

                    || m == Material.GOLD_ORE
                    || m == Material.DEEPSLATE_GOLD_ORE
                    || m == Material.NETHER_GOLD_ORE

                    // ⚪ FER
                    || m == Material.IRON_INGOT
                    || m == Material.IRON_BLOCK
                    || m == Material.RAW_IRON

                    || m == Material.IRON_ORE
                    || m == Material.DEEPSLATE_IRON_ORE

                    // 🟤 CUIVRE
                    || m == Material.COPPER_INGOT
                    || m == Material.COPPER_BLOCK
                    || m == Material.RAW_COPPER

                    || m == Material.COPPER_ORE
                    || m == Material.DEEPSLATE_COPPER_ORE

                    // ⚫ CHARBON
                    || m == Material.COAL
                    || m == Material.COAL_BLOCK

                    || m == Material.COAL_ORE
                    || m == Material.DEEPSLATE_COAL_ORE

                    // 🔵 LAPIS
                    || m == Material.LAPIS_LAZULI
                    || m == Material.LAPIS_BLOCK

                    || m == Material.LAPIS_ORE
                    || m == Material.DEEPSLATE_LAPIS_ORE

                    // 🔴 REDSTONE
                    || m == Material.REDSTONE
                    || m == Material.REDSTONE_BLOCK

                    || m == Material.REDSTONE_ORE
                    || m == Material.DEEPSLATE_REDSTONE_ORE

                    // 🟣 AMÉTHYSTE
                    || m == Material.AMETHYST_SHARD
                    || m == Material.AMETHYST_BLOCK

                    // ✨ QUARTZ
                    || m == Material.QUARTZ
                    || m == Material.QUARTZ_BLOCK
                    || m == Material.NETHER_QUARTZ_ORE

                    // 🔥 NETHERITE
                    || m == Material.NETHERITE_INGOT
                    || m == Material.ANCIENT_DEBRIS;

            // =========================
            // 🗑 SUPPRESSION
            // =========================

            if (blocked) {

                inv.remove(item);
            }
        }
    }
}