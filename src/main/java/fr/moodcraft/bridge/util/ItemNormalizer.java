package fr.moodcraft.bridge.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class ItemNormalizer {

    public static String normalize(Material mat) {

        if (mat == null || mat == Material.AIR) return "unknown";

        return switch (mat) {

            // 💎 MINERAIS
            case DIAMOND -> "diamond";
            case EMERALD -> "emerald";
            case GOLD_INGOT -> "gold";
            case IRON_INGOT -> "iron";
            case COPPER_INGOT -> "copper";
            case REDSTONE -> "redstone";
            case LAPIS_LAZULI -> "lapis";
            case COAL, CHARCOAL -> "coal";
            case QUARTZ -> "quartz";
            case NETHERITE_INGOT -> "netherite";
            case AMETHYST_SHARD -> "amethyst";
            case GLOWSTONE_DUST -> "glowstone";

            // 🧱 BLOCS
            case DIAMOND_BLOCK -> "diamond";
            case EMERALD_BLOCK -> "emerald";
            case GOLD_BLOCK -> "gold";
            case IRON_BLOCK -> "iron";
            case COPPER_BLOCK -> "copper";
            case REDSTONE_BLOCK -> "redstone";
            case LAPIS_BLOCK -> "lapis";
            case COAL_BLOCK -> "coal";
            case QUARTZ_BLOCK -> "quartz";
            case NETHERITE_BLOCK -> "netherite";

            // 🔥 fallback intelligent
            default -> fallback(mat.name());
        };
    }

    public static String normalizeBlock(Block block) {

        if (block == null) return "unknown";

        return switch (block.getType()) {

            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> "diamond";
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> "emerald";
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> "gold";
            case IRON_ORE, DEEPSLATE_IRON_ORE -> "iron";
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> "copper";
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> "redstone";
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> "lapis";
            case COAL_ORE, DEEPSLATE_COAL_ORE -> "coal";
            case NETHER_QUARTZ_ORE -> "quartz";
            case GLOWSTONE -> "glowstone";
            case AMETHYST_CLUSTER -> "amethyst";

            default -> fallback(block.getType().name());
        };
    }

    // =========================
    // 🧠 FALLBACK CLEAN
    // =========================
    private static String fallback(String name) {
        return name.toLowerCase().replace("_", "");
    }
}