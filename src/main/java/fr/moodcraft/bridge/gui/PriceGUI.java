package fr.moodcraft.bridge.gui;

import fr.moodcraft.market.MarketAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PriceGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§3Bourse Minerais");

        try {

            // 🔙 MENU PRINCIPAL
            SafeGUI.safeSet(inv, 4, SafeGUI.item(
                    Material.NETHER_STAR,
                    "§b☰ Menu principal",
                    "§8────────────",
                    "§7Retour au menu",
                    "§7principal",
                    "",
                    "§8Clique pour ouvrir"
            ));

            set(inv, 10, "netherite", Material.NETHERITE_INGOT, "§5◆ Netherite");
            set(inv, 11, "emerald", Material.EMERALD, "§a◆ Émeraude");
            set(inv, 12, "diamond", Material.DIAMOND, "§b◆ Diamant");

            set(inv, 13, "gold", Material.GOLD_INGOT, "§6◆ Or");
            set(inv, 14, "copper", Material.COPPER_INGOT, "§6◆ Cuivre");
            set(inv, 15, "iron", Material.IRON_INGOT, "§7◆ Fer");

            set(inv, 16, "glowstone", Material.GLOWSTONE_DUST, "§e◆ Glowstone");

            set(inv, 19, "quartz", Material.QUARTZ, "§f◆ Quartz");
            set(inv, 20, "amethyst", Material.AMETHYST_SHARD, "§d◆ Améthyste");
            set(inv, 21, "redstone", Material.REDSTONE, "§c◆ Redstone");
            set(inv, 22, "lapis", Material.LAPIS_LAZULI, "§9◆ Lapis");
            set(inv, 23, "coal", Material.COAL, "§8◆ Charbon");

        } catch (Exception e) {
            inv.clear();
            SafeGUI.safeSet(inv, 13, SafeGUI.item(Material.BARRIER, "§cErreur Bourse"));
            e.printStackTrace();
        }

        GUIManager.open(p, "minerais", inv);
    }

    private static void set(Inventory inv, int slot, String id, Material mat, String name) {

        double price = MarketAPI.getPrice(id);
        String trend = MarketAPI.getTrend(id);

        SafeGUI.safeSet(inv, slot,
                SafeGUI.item(mat, name,

                        "§8────────────",
                        "§7Prix:",
                        "§f" + String.format("%.2f", price) + "€",
                        "",
                        "§7Tendance:",
                        trend,
                        "§8────────────",
                        "",
                        "§aClique pour vendre"));
    }
}