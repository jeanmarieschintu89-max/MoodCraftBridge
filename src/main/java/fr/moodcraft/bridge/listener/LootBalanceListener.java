package fr.moodcraft.bridge.listener;

import org.bukkit.Material;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.world.LootGenerateEvent;

public class LootBalanceListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {

        // =========================
        // ✅ VÉRIFICATION LOOT TABLE
        // =========================

        if (event.getLootTable() == null) {
            return;
        }

        // =========================
        // 📦 NOM LOOT TABLE
        // =========================

        String key =
                event.getLootTable()
                        .getKey()
                        .toString();

        // =========================
        // 🔍 DEBUG CONSOLE
        // =========================

        System.out.println(
                "[MoodCraftLoot] LootTable => " + key
        );

        // =========================
        // 🏰 TRIAL CHAMBERS UNIQUEMENT
        // =========================

        boolean protectedStructure =

                key.contains("trial")
                || key.contains("chamber")
                || key.contains("vault");

        if (!protectedStructure) {
            return;
        }

        // =========================
        // 💰 ANTI-LOOT ÉCONOMIE
        // =========================

        event.getLoot().removeIf(item -> {

            if (item == null) {
                return false;
            }

            Material m = item.getType();

            return

                    // =========================
                    // 💎 DIAMANTS
                    // =========================

                    m == Material.DIAMOND
                    || m == Material.DIAMOND_BLOCK

                    || m == Material.DIAMOND_ORE
                    || m == Material.DEEPSLATE_DIAMOND_ORE

                    // =========================
                    // 🟢 ÉMERAUDES
                    // =========================

                    || m == Material.EMERALD
                    || m == Material.EMERALD_BLOCK

                    || m == Material.EMERALD_ORE
                    || m == Material.DEEPSLATE_EMERALD_ORE

                    // =========================
                    // 🟡 OR
                    // =========================

                    || m == Material.GOLD_INGOT
                    || m == Material.GOLD_BLOCK
                    || m == Material.RAW_GOLD

                    || m == Material.GOLD_ORE
                    || m == Material.DEEPSLATE_GOLD_ORE
                    || m == Material.NETHER_GOLD_ORE

                    // =========================
                    // ⚪ FER
                    // =========================

                    || m == Material.IRON_INGOT
                    || m == Material.IRON_BLOCK
                    || m == Material.RAW_IRON

                    || m == Material.IRON_ORE
                    || m == Material.DEEPSLATE_IRON_ORE

                    // =========================
                    // 🟤 CUIVRE
                    // =========================

                    || m == Material.COPPER_INGOT
                    || m == Material.COPPER_BLOCK
                    || m == Material.RAW_COPPER

                    || m == Material.COPPER_ORE
                    || m == Material.DEEPSLATE_COPPER_ORE

                    // =========================
                    // ⚫ CHARBON
                    // =========================

                    || m == Material.COAL
                    || m == Material.COAL_BLOCK

                    || m == Material.COAL_ORE
                    || m == Material.DEEPSLATE_COAL_ORE

                    // =========================
                    // 🔵 LAPIS
                    // =========================

                    || m == Material.LAPIS_LAZULI
                    || m == Material.LAPIS_BLOCK

                    || m == Material.LAPIS_ORE
                    || m == Material.DEEPSLATE_LAPIS_ORE

                    // =========================
                    // 🔴 REDSTONE
                    // =========================

                    || m == Material.REDSTONE
                    || m == Material.REDSTONE_BLOCK

                    || m == Material.REDSTONE_ORE
                    || m == Material.DEEPSLATE_REDSTONE_ORE

                    // =========================
                    // 🟣 AMÉTHYSTE
                    // =========================

                    || m == Material.AMETHYST_SHARD
                    || m == Material.AMETHYST_BLOCK

                    // =========================
                    // ✨ QUARTZ
                    // =========================

                    || m == Material.QUARTZ
                    || m == Material.QUARTZ_BLOCK
                    || m == Material.NETHER_QUARTZ_ORE

                    // =========================
                    // 🔥 NETHERITE
                    // =========================

                    || m == Material.NETHERITE_INGOT
                    || m == Material.ANCIENT_DEBRIS;
        });
    }
}