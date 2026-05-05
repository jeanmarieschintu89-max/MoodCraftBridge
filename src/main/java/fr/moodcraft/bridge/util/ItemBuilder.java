package fr.moodcraft.bridge.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    public static ItemStack of(Material mat, String name, String... lore) {

        if (mat == null) mat = Material.BARRIER;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName("§r" + clean(name));

        if (lore != null && lore.length > 0) {

            List<String> safeLore = new ArrayList<>();

            for (String line : lore) {

                if (line == null) continue;

                String cleaned = "§7" + clean(line);

                if (cleaned.trim().isEmpty()) {
                    safeLore.add(" ");
                } else {
                    safeLore.add(cleaned);
                }
            }

            meta.setLore(safeLore);
        }

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );

        item.setItemMeta(meta);
        return item;
    }

    // =========================
    // 🔧 CLEAN BEDROCK
    // =========================
    private static String clean(String text) {

        if (text == null) return "";

        return text
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("ç", "c")
                .replace("ô", "o")
                .replace("→", ">")
                .replace("↑", "+")
                .replace("↓", "-");
    }
}