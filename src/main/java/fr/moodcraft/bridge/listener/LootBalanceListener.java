package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.Container;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LootBalanceListener
        implements Listener {

    @EventHandler
    public void onOpen(
            InventoryOpenEvent event
    ) {

        String title =
                event.getView()
                        .getTitle();

        if (isPluginMenu(title)) {
            return;
        }

        Inventory inv =
                event.getInventory();

        if (!(inv.getHolder()
                instanceof Container)) {
            return;
        }

        Bukkit.getScheduler()
                .runTaskLater(
                        Main.getInstance(),
                        () -> cleanInventory(inv),
                        2L
                );
    }

    @EventHandler
    public void onVaultUse(
            PlayerInteractEvent event
    ) {

        if (event.getAction()
                != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block =
                event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (block.getType()
                != Material.VAULT) {
            return;
        }

        scheduleVaultClean(
                block.getLocation()
                        .add(0.5, 0.5, 0.5)
        );
    }

    private void scheduleVaultClean(
            Location center
    ) {

        long[] delays = {
                1L,
                3L,
                6L,
                10L,
                20L
        };

        for (long delay : delays) {

            Bukkit.getScheduler()
                    .runTaskLater(
                            Main.getInstance(),
                            () -> cleanDroppedItems(center),
                            delay
                    );
        }
    }

    private void cleanDroppedItems(
            Location center
    ) {

        World world =
                center.getWorld();

        if (world == null) {
            return;
        }

        for (Entity entity :
                world.getNearbyEntities(
                        center,
                        5,
                        5,
                        5
                )) {

            if (!(entity instanceof Item item)) {
                continue;
            }

            ItemStack stack =
                    item.getItemStack();

            if (stack == null) {
                continue;
            }

            if (isBlocked(
                    stack.getType()
            )) {

                item.remove();
            }
        }
    }

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

            if (isBlocked(
                    item.getType()
            )) {

                inv.setItem(i, null);
            }
        }
    }

    private boolean isPluginMenu(
            String title
    ) {

        return title.contains("Menu")
                || title.contains("Banque")
                || title.contains("Bourse")
                || title.contains("Minerais")
                || title.contains("Virement")
                || title.contains("Contrat")
                || title.contains("Historique")
                || title.contains("Profil")
                || title.contains("Téléportation");
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