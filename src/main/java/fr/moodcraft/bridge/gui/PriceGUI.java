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
                        "§6✦ §8Bourses §aMood§6Craft §6✦"
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
                                    "§6✦ §fBourses des minerais §6✦",
                                    "§8----- §6✦ Bourses §aMood§6Craft §6✦ §8-----",
                                    "",
                                    "§8• §7Vends tes ressources",
                                    "§8• §7Prix dynamique du serveur",
                                    "§8• §7Vente instantanée",
                                    "",
                                    "§e➜ §fClique un minerai"
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
                            "§8• §7Retour au menu principal",
                            "",
                            "§c✖ Clique pour revenir"
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
                            "§c✦ Erreur bourses",
                            "§c✖ §fImpossible de charger les prix.",
                            "",
                            "§8• §7Réessayez dans un instant"
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
                        "§7Stable"
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

            stockState = "§cTrès bas";

        } else if (stock <= 0) {

            stockState = "§6Bas";

        } else if (stock <= 150) {

            stockState = "§aNormal";

        } else {

            stockState = "§2Haut";
        }

        SafeGUI.safeSet(inv, slot,
                SafeGUI.item(
                        mat,
                        name,
                        "§8• §7Prix: §6" + formattedPrice + "€",
                        "§8• §7Tendance: " + cleanTrend(trend),
                        "§8• §7Stock: " + stockState,
                        "",
                        "§8• §7Vend tout ce minerai",
                        "§8• §7dans ton inventaire",
                        "",
                        "§e➜ §fClique pour vendre"
                )
        );
    }

    private static String cleanTrend(
            String trend
    ) {

        if (trend == null || trend.isBlank()) {
            return "§7Stable";
        }

        return trend
                .replace("▬", "")
                .trim();
    }
}