package fr.moodcraft.bridge.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

public class LootBalanceListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {

        // Vérifie que la loot table existe
        if (event.getLootTable() == null) return;

        // Nom de la loot table
        String key = event.getLootTable().getKey().toString();

        // Seulement les Trial Chambers
        if (!key.contains("trial_chambers")) return;

        // Supprime les minerais dangereux pour l'économie
        event.getLoot().removeIf(item -> {

            if (item == null) return false;

            Material m = item.getType();

            return m == Material.DIAMOND
                    || m == Material.DIAMOND_BLOCK
                    || m == Material.EMERALD
                    || m == Material.EMERALD_BLOCK
                    || m == Material.GOLD_INGOT
                    || m == Material.GOLD_BLOCK
                    || m == Material.IRON_INGOT
                    || m == Material.IRON_BLOCK
                    || m == Material.NETHERITE_INGOT
                    || m == Material.ANCIENT_DEBRIS;
        });
    }
}