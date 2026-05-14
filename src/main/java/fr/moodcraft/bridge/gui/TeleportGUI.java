package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class TeleportGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        GuiTitle.of("Téléportation")
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.COMPASS,
                                "§6✦ §fVoyage rapide §6✦",
                                "§8• §7Déplacements utiles",
                                "§8• §7Java et Bedrock",
                                "§8• §7Menu compact",
                                "",
                                "§e➜ §fChoisis une destination"
                        )
                )
        );

        SafeGUI.safeSet(inv, 11,
                SafeGUI.item(
                        Material.GRASS_BLOCK,
                        "§6✦ §fSpawn §6✦",
                        "§8• §7Retour au spawn",
                        "",
                        "§e➜ §fTéléporter"
                )
        );

        SafeGUI.safeSet(inv, 13,
                SafeGUI.item(
                        Material.ENDER_PEARL,
                        "§6✦ §fRTP §6✦",
                        "§8• §7Téléportation aléatoire",
                        "§8• §7Monde survie",
                        "",
                        "§e➜ §fPartir explorer"
                )
        );

        SafeGUI.safeSet(inv, 15,
                SafeGUI.item(
                        Material.BELL,
                        "§6✦ §fVille §6✦",
                        "§8• §7Ouvrir le menu ville",
                        "§8• §7Towny",
                        "",
                        "§e➜ §fOuvrir"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ §fRetour §c✦",
                        "§8• §7Menu principal",
                        "",
                        "§c✖ §fRevenir"
                )
        );

        p.openInventory(inv);
        GUIManager.set(p, new fr.moodcraft.bridge.handler.TeleportHandler());
    }
}
