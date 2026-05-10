package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class PriceGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §eMarché"
                );

        try {

            SafeGUI.fill(
                    inv,
                    Material.BLACK_STAINED_GLASS_PANE,
                    " "
            );

            SafeGUI.safeSet(inv, 4,
                    SafeGUI.glow(
                            SafeGUI.item(
                                    Material.BOOK,
                                    "§6✦ Marché des minerais",
                                    "§8----- §6Économie §8-----",
                                    "§7Vends tes ressources au prix actuel.",
                                    "",
                                    "§8• §7Prix dynamiques",
                                    "§8• §7Offre et demande",
                                    "§8• §7Synchronisé aux shops",
                                    "",
                                    "§e▶ Choisis un minerai"
                            )
                    )
            );

            set(inv, 10,
                    "netherite",
                    Material.NETHERITE_INGOT,
                    "§5✦ Netherite"
            );

            set(inv, 11,
                    "emerald",
                    Material.EMERALD,
                    "§a✦ Émeraude"
            );

            set(inv, 12,
                    "diamond",
                    Material.DIAMOND,
                    "§b✦ Diamant"
            );

            set(inv, 13,
                    "gold",
                    Material.GOLD_INGOT,
                    "§6✦ Or"
            );

            set(inv, 14,
                    "copper",
                    Material.COPPER_INGOT,
                    "§6✦ Cuivre"
            );

            set(inv, 15,
                    "iron",
                    Material.IRON_INGOT,
                    "§f✦ Fer"
            );

            set(inv, 16,
                    "glowstone",
                    Material.GLOWSTONE_DUST,
                    "§e✦ Glowstone"
            );

            set(inv, 20,
                    "quartz",
                    Material.QUARTZ,
                    "§f✦ Quartz"
            );

            set(inv, 21,
                    "amethyst",
                    Material.AMETHYST_SHARD,
                    "§d✦ Améthyste"
            );

            set(inv, 22,
                    "redstone",
                    Material.REDSTONE,
                    "§c✦ Redstone"
            );

            set(inv, 23,
                    "lapis",
                    Material.LAPIS_LAZULI,
                    "§9✦ Lapis"
            );

            set(inv, 24,
                    "coal",
                    Material.COAL,
                    "§8✦ Charbon"
            );

            SafeGUI.safeSet(inv, 31,
                    SafeGUI.item(
                            Material.BARRIER,
                            "§c✦ Retour",
                            "§7Retour au menu principal.",
                            "",
                            "§c▶ Retour"
                    )
            );

        } catch (Exception e) {

            inv.clear();

            SafeGUI.fill(
                    inv,
                    Material.BLACK_STAINED_GLASS_PANE,
                    " "
            );

            SafeGUI.safeSet(inv, 13,
                    SafeGUI.item(
                            Material.BARRIER,
                            "§c✦ Erreur marché",
                            "§7Impossible de charger",
                            "§7les données économiques."
                    )
            );

            e.printStackTrace();
        }

        GUIManager.open(
                p,
                "minerais",
                inv
        );
    }

    private static void set(
            Inventory inv,
            int slot,
            String id,
            Material mat,
            String name
    ) {

        double price =
                MarketEngine.getPrice(id);

        String trend =
                MarketState.trend.getOrDefault(
                        id,
                        "§7▬ Stable"
                );

        String formattedPrice =
                SafeGUI.money(price);

        double stock =
                MarketState.stock.getOrDefault(
                        id,
                        0.0
                );

        String stockState;

        if (stock <= -100) {

            stockState = "§cTrès faible";

        } else if (stock <= 0) {

            stockState = "§6Faible";

        } else if (stock <= 150) {

            stockState = "§aStable";

        } else {

            stockState = "§2Élevé";
        }

        SafeGUI.safeSet(inv, slot,
                SafeGUI.item(
                        mat,
                        name,
                        "§7Prix: §6" + formattedPrice + "€",
                        "§7Tendance: " + trend,
                        "§7Stock: " + stockState,
                        "",
                        "§8• §7Vente instantanée",
                        "§8• §7Marché dynamique",
                        "",
                        "§e▶ Vendre"
                )
        );
    }
}