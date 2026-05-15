package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriceGUI {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
    };

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        GuiTitle.of("Bourses MoodCraft")
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.DIAMOND,
                                "§6✦ §fMarché dynamique §6✦",
                                "§8• §7Prix synchronisés",
                                "§8• §7Tendance par ressource",
                                "§8• §7Économie §aMood§6Craft",
                                "",
                                "§e➜ §fConsulte les prix"
                        )
                )
        );

        try {
            List<String> items = new ArrayList<>(MarketState.base.keySet());
            items.sort(Comparator.naturalOrder());

            int index = 0;

            for (String item : items) {

                if (index >= SLOTS.length) {
                    break;
                }

                SafeGUI.safeSet(
                        inv,
                        SLOTS[index],
                        marketItem(item)
                );

                index++;
            }

            if (items.isEmpty()) {
                SafeGUI.safeSet(
                        inv,
                        13,
                        SafeGUI.item(
                                Material.BARRIER,
                                "§c✦ §fAucun prix §c✦",
                                "§8• §7Marché non chargé",
                                "§8• §7Vérifiez la config"
                        )
                );
            }

        } catch (Exception e) {

            SafeGUI.safeSet(
                    inv,
                    13,
                    SafeGUI.item(
                            Material.BARRIER,
                            "§c✦ §fMarché indisponible §c✦",
                            "§8• §7Les prix ne peuvent pas être chargés",
                            "§8• §7Réessayez dans quelques secondes"
                    )
            );
        }

        SafeGUI.safeSet(
                inv,
                31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ §fRetour §c✦",
                        "§8• §7Menu principal",
                        "",
                        "§c✖ §fRevenir"
                )
        );

        p.openInventory(inv);
        GUIManager.set(p, new fr.moodcraft.bridge.handler.PriceHandler());
    }

    private static org.bukkit.inventory.ItemStack marketItem(String item) {

        double price = MarketEngine.getPrice(item);
        double base = MarketState.base.getOrDefault(item, price);
        double stock = MarketState.stock.getOrDefault(item, 0.0);
        String trend = MarketState.trend.getOrDefault(item, "§7▬ Stable");

        return SafeGUI.item(
                materialFor(item),
                "§6✦ §f" + displayName(item) + " §6✦",
                "§8• §7Prix : §e" + SafeGUI.money(price) + "€",
                "§8• §7Base : §6" + SafeGUI.money(base) + "€",
                "§8• §7Stock : §b" + SafeGUI.money(stock),
                "§8• §7Tendance : " + trend,
                "",
                "§e➜ §fPrix dynamique"
        );
    }

    private static Material materialFor(String item) {

        return switch (item.toLowerCase()) {
            case "diamond", "diamant" -> Material.DIAMOND;
            case "emerald", "emeraude", "émeraude" -> Material.EMERALD;
            case "gold", "gold_ingot", "or" -> Material.GOLD_INGOT;
            case "iron", "iron_ingot", "fer" -> Material.IRON_INGOT;
            case "copper", "copper_ingot", "cuivre" -> Material.COPPER_INGOT;
            case "coal", "charbon" -> Material.COAL;
            case "redstone" -> Material.REDSTONE;
            case "lapis", "lapis_lazuli" -> Material.LAPIS_LAZULI;
            case "quartz" -> Material.QUARTZ;
            case "netherite", "netherite_ingot" -> Material.NETHERITE_INGOT;
            case "amethyst", "amethyst_shard", "amethyste", "améthyste" -> Material.AMETHYST_SHARD;
            case "glowstone" -> Material.GLOWSTONE_DUST;
            default -> Material.PAPER;
        };
    }

    private static String displayName(String item) {

        if (item == null || item.isBlank()) {
            return "Inconnu";
        }

        String clean = item
                .replace('_', ' ')
                .trim();

        return clean.substring(0, 1).toUpperCase() + clean.substring(1);
    }
}
