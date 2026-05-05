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

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    // =========================
    // 🎯 ITEM VIA MATERIAL
    // =========================
    public static ItemStack item(Material mat, String name, String... lore) {

        if (mat == null) mat = Material.BARRIER;

        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();

        if (meta == null) return it;

        meta.setDisplayName("§r" + (name == null ? "" : name));
        meta.setLore(formatLore(lore));

        hideAll(meta);

        it.setItemMeta(meta);
        return it;
    }

    // =========================
    // 🔥 ITEM VIA ITEMSTACK
    // =========================
    public static ItemStack item(ItemStack base, String name, String... lore) {

        if (base == null) return item(Material.BARRIER, " ");

        ItemStack it = base.clone();
        ItemMeta meta = it.getItemMeta();

        if (meta == null) return it;

        meta.setDisplayName("§r" + (name == null ? "" : name));
        meta.setLore(formatLore(lore));

        hideAll(meta);

        it.setItemMeta(meta);
        return it;
    }

    // =========================
    // 🧠 FORMAT LORE
    // =========================
    private static List<String> formatLore(String... lore) {

        List<String> fixed = new ArrayList<>();

        if (lore != null) {
            for (String line : lore) {
                fixed.add(line == null ? "" : "§7" + line);
            }
        }

        return fixed;
    }

    // =========================
    // 🔒 HIDE ALL FLAGS
    // =========================
    private static void hideAll(ItemMeta meta) {
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );
    }

    // =========================
    // ✨ GLOW
    // =========================
    public static ItemStack glow(ItemStack item) {

        if (item == null) return null;

        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();

        if (meta == null) return clone;

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        clone.setItemMeta(meta);
        return clone;
    }

    // =========================
    // ❌ REMOVE GLOW
    // =========================
    public static ItemStack removeGlow(ItemStack item) {

        if (item == null) return null;

        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();

        if (meta == null) return clone;

        meta.getEnchants().keySet().forEach(meta::removeEnchant);

        clone.setItemMeta(meta);
        return clone;
    }

    // =========================
    // 🛡️ SAFE SET SLOT
    // =========================
    public static void safeSet(Inventory inv, int slot, ItemStack item) {

        if (inv == null) return;

        try {
            inv.setItem(slot, item == null ? new ItemStack(Material.BARRIER) : item);
        } catch (Exception e) {
            inv.setItem(slot, new ItemStack(Material.BARRIER));
        }
    }

    // =========================
    // 🧱 BORDURES
    // =========================
    public static void fillBorders(Inventory inv, Material mat) {

        if (inv == null) return;

        ItemStack pane = item(mat, " ");
        int size = inv.getSize();

        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, pane.clone());
            }
        }
    }

    // =========================
    // 💰 FORMAT ARGENT
    // =========================
    public static String money(double v) {
        return MONEY_FORMAT.format(v);
    }
}