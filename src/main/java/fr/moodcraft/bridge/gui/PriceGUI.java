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
import java.util.List;

public class PriceGUI {

    public static final int RETURN_SLOT = 35;

    private static final int INVENTORY_SIZE = 36;

    private static final MarketItem[] MARKET_ITEMS = {
            new MarketItem(10, "netherite", Material.NETHERITE_INGOT, "Netherite"),
            new MarketItem(11, "emerald", Material.EMERALD, "Émeraude"),
            new MarketItem(12, "diamond", Material.DIAMOND, "Diamant"),
            new MarketItem(13, "gold", Material.GOLD_INGOT, "Or"),
            new MarketItem(14, "copper", Material.COPPER_INGOT, "Cuivre"),
            new MarketItem(15, "iron", Material.IRON_INGOT, "Fer"),
            new MarketItem(16, "glowstone", Material.GLOWSTONE_DUST, "Glowstone"),
            new MarketItem(20, "quartz", Material.QUARTZ, "Quartz"),
            new MarketItem(21, "amethyst", Material.AMETHYST_SHARD, "Améthyste"),
            new MarketItem(22, "redstone", Material.REDSTONE, "Redstone"),
            new MarketItem(23, "lapis", Material.LAPIS_LAZULI, "Lapis"),
            new MarketItem(24, "coal", Material.COAL, "Charbon")
    };

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        INVENTORY_SIZE,
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
                                "§e➜ §fClique un minerai pour tout vendre"
                        )
                )
        );

        try {

            int displayed = 0;

            if (isMarketLoaded()) {

                for (MarketItem item : MARKET_ITEMS) {

                    if (!isKnown(item)) {
                        continue;
                    }

                    SafeGUI.safeSet(
                            inv,
                            item.slot(),
                            marketItem(item)
                    );

                    displayed++;
                }
            }

            if (displayed <= 0) {
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
                RETURN_SLOT,
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

    public static MarketItem getMarketItemBySlot(int slot) {

        for (MarketItem item : MARKET_ITEMS) {

            if (item.slot() == slot) {
                return item;
            }
        }

        return null;
    }

    public static List<MarketItem> getMarketItems() {

        List<MarketItem> items = new ArrayList<>();

        for (MarketItem item : MARKET_ITEMS) {
            items.add(item);
        }

        return items;
    }

    public static boolean isKnown(MarketItem item) {

        if (item == null) {
            return false;
        }

        String id = item.id();

        return MarketState.base.containsKey(id)
                || MarketState.price.containsKey(id)
                || MarketState.stock.containsKey(id)
                || MarketState.trend.containsKey(id);
    }

    private static org.bukkit.inventory.ItemStack marketItem(MarketItem item) {

        String id = item.id();
        double price = MarketEngine.getPrice(id);
        double base = MarketState.base.getOrDefault(id, price);
        double stock = MarketState.stock.getOrDefault(id, 0.0);
        String trend = MarketState.trend.getOrDefault(id, "§7▬ Stable");

        return SafeGUI.item(
                item.material(),
                "§6✦ §f" + item.displayName() + " §6✦",
                "§8• §7Prix : §e" + SafeGUI.money(price) + "€",
                "§8• §7Base : §6" + SafeGUI.money(base) + "€",
                "§8• §7Stock : §b" + SafeGUI.money(stock),
                "§8• §7Tendance : " + trend,
                "",
                "§e➜ §fVendre cette ressource"
        );
    }

    private static boolean isMarketLoaded() {

        return !MarketState.base.isEmpty()
                || !MarketState.price.isEmpty()
                || !MarketState.stock.isEmpty();
    }

    public record MarketItem(
            int slot,
            String id,
            Material material,
            String displayName
    ) {
    }
}
