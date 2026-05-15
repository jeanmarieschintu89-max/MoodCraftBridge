package fr.moodcraft.bridge.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SafeGUI {

    private static final DecimalFormat MONEY_FORMAT =
            new DecimalFormat("#,##0.00");

    public static ItemStack item(Material mat,
                                 String name,
                                 String... lore) {

        Material finalMaterial = normalizeMaterial(mat, name);

        ItemStack it = new ItemStack(finalMaterial);

        ItemMeta meta = it.getItemMeta();

        if (meta == null)
            return it;

        meta.setDisplayName("§r" + (name == null ? "" : name));
        meta.setLore(formatLore(lore));
        hideAll(meta);
        it.setItemMeta(meta);

        return it;
    }

    public static ItemStack item(ItemStack base,
                                 String name,
                                 String... lore) {

        if (base == null)
            return item(Material.BARRIER, " ");

        ItemStack it = base.clone();

        if (isReturnButton(name)) {
            it.setType(Material.BARRIER);
        }

        ItemMeta meta = it.getItemMeta();

        if (meta == null)
            return it;

        meta.setDisplayName("§r" + (name == null ? "" : name));
        meta.setLore(formatLore(lore));
        hideAll(meta);
        it.setItemMeta(meta);

        return it;
    }

    private static List<String> formatLore(String... lore) {

        List<String> fixed = new ArrayList<>();

        if (lore != null) {
            for (String line : lore) {
                fixed.add(normalizeLoreLine(line));
            }
        }

        return fixed;
    }

    private static String normalizeLoreLine(String line) {

        if (line == null || line.isBlank()) {
            return "";
        }

        String trimmed = line.trim().replace("§c✘", "§c✖");

        if (trimmed.startsWith("§8•")
                || trimmed.startsWith("§e➜")
                || trimmed.startsWith("§a✔")
                || trimmed.startsWith("§c✖")) {
            return trimmed;
        }

        if (trimmed.startsWith("§eClique")
                || trimmed.startsWith("§eOuvrir")
                || trimmed.startsWith("§eConfirmer")) {
            return "§e➜ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§a")) {
            return "§a✔ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§c")) {
            return "§c✖ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§7") || trimmed.startsWith("§8")) {
            return "§8• §7" + cleanPrefix(trimmed);
        }

        return "§8• §7" + cleanPrefix(trimmed);
    }

    private static Material normalizeMaterial(Material material, String name) {

        if (isReturnButton(name)) {
            return Material.BARRIER;
        }

        return material == null ? Material.BARRIER : material;
    }

    private static boolean isReturnButton(String name) {

        if (name == null) {
            return false;
        }

        String clean = name
                .replaceAll("§.", "")
                .replace("✦", "")
                .trim()
                .toLowerCase();

        return clean.equals("retour")
                || clean.equals("fermer")
                || clean.equals("revenir")
                || clean.equals("annuler")
                || clean.contains("retour au menu")
                || clean.contains("fermer le menu");
    }

    private static String cleanPrefix(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replaceFirst("^§[0-9a-fk-or]", "")
                .replaceFirst("^➜\\s*", "")
                .replaceFirst("^✔\\s*", "")
                .replaceFirst("^✘\\s*", "")
                .replaceFirst("^✖\\s*", "")
                .replaceFirst("^•\\s*", "")
                .trim();
    }

    private static void hideAll(ItemMeta meta) {
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ITEM_SPECIFICS,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );
    }

    public static ItemStack glow(ItemStack item) {

        if (item == null)
            return null;

        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();

        if (meta == null)
            return clone;

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        hideAll(meta);
        clone.setItemMeta(meta);

        return clone;
    }

    public static ItemStack removeGlow(ItemStack item) {

        if (item == null)
            return null;

        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();

        if (meta == null)
            return clone;

        meta.getEnchants().keySet().forEach(meta::removeEnchant);
        clone.setItemMeta(meta);

        return clone;
    }

    public static void safeSet(Inventory inv,
                               int slot,
                               ItemStack item) {

        if (inv == null)
            return;

        try {
            inv.setItem(slot, item == null ? new ItemStack(Material.BARRIER) : item);
        } catch (Exception e) {
            inv.setItem(slot, new ItemStack(Material.BARRIER));
        }
    }

    public static void fillBorders(Inventory inv,
                                   Material mat) {

        if (inv == null)
            return;

        ItemStack pane = item(mat, " ");
        int size = inv.getSize();

        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, pane.clone());
            }
        }
    }

    public static void fill(Inventory inv) {
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, " ");
    }

    public static void fill(Inventory inv,
                            Material mat,
                            String name) {

        if (inv == null)
            return;

        ItemStack fill = item(mat, name);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, fill.clone());
            }
        }
    }

    public static String money(double v) {
        return MONEY_FORMAT.format(v);
    }
}
