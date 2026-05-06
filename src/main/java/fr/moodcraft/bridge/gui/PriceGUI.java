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

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §3Marché MoodCraft"
        );

        try {

            //
            // 🌌 FOND
            //

            SafeGUI.fill(

                    inv,

                    Material.BLACK_STAINED_GLASS_PANE,

                    " "
            );

            //
            // 🔙 MENU
            //

            SafeGUI.safeSet(inv, 4,

                    SafeGUI.glow(

                            SafeGUI.item(

                                    Material.NETHER_STAR,

                                    "§b✦ Menu Principal",

                                    "§8━━━━━━━━━━━━━━━━",

                                    "§7Retour au centre",

                                    "§7de gestion MoodCraft.",

                                    "",

                                    "§e▶ Revenir"
                            )
                    )
            );

            //
            // 💎 LIGNE 1
            //

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

            //
            // 💎 LIGNE 2
            //

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

            //
            // 📈 INFOS MARCHÉ
            //

            SafeGUI.safeSet(inv, 31,

                    SafeGUI.item(

                            Material.BOOK,

                            "§6✦ Informations Marché",

                            "§8━━━━━━━━━━━━━━━━",

                            "§7Les prix évoluent selon:",

                            "",

                            "§8• Offre & demande",

                            "§8• Activité joueurs",

                            "§8• Rareté ressources",

                            "§8• Volume économique",

                            "",

                            "§7Le marché est synchronisé",

                            "§7avec les shops MoodCraft.",

                            "",

                            "§e▶ Économie dynamique"
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

                            "§c✦ Erreur Marché",

                            "§8━━━━━━━━━━━━━━━━",

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

    // =========================
    // 📊 ITEM
    // =========================

    private static void set(

            Inventory inv,

            int slot,

            String id,

            Material mat,

            String name
    ) {

        double price =
                MarketEngine.getPrice(id);

        //
        // 📈 TENDANCE
        //

        String trend =
                MarketState.trend.getOrDefault(
                        id,
                        "§7▬ Stable"
                );

        //
        // 💰 FORMAT
        //

        String formattedPrice =
                SafeGUI.money(price);

        //
        // 📦 STOCK
        //

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

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Valeur actuelle:",

                        "§6" + formattedPrice + "€",

                        "",

                        "§7Tendance:",

                        trend,

                        "",

                        "§7Disponibilité:",

                        stockState,

                        "",

                        "§8• Vente instantanée",

                        "§8• Marché dynamique",

                        "",

                        "§e▶ Vendre ressources"
                )
        );
    }
}