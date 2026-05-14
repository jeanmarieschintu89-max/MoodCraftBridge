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
                        GuiTitle.of("Bourses MoodCraft")
                );

        try {

            SafeGUI.fill(
                    inv,
                    Material.BLACK_STAINED_GLASS_PANE,
                    " "
            );

            for (MarketState state : MarketEngine.all()) {

                SafeGUI.safeSet(
                        inv,
                        state.slot(),
                        MarketEngine.item(state)
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
}
