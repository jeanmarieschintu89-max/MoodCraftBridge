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
                                "§8• §7Accès rapides aux zones importantes",
                                "§8• §7Spawn, shop, mini jeux et exploration",
                                "§8• §7Menu clair et compact",
                                "",
                                "§e➜ §fChoisis une destination"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.item(
                        Material.GRASS_BLOCK,
                        "§6✦ §fSpawn §6✦",
                        "§8• §7Retour au spawn",
                        "§8• §7Centre de §aMood§6Craft",
                        "",
                        "§e➜ §fTéléporter"
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.EMERALD,
                        "§6✦ §fAdminShop §6✦",
                        "§8• §7Boutique officielle",
                        "§8• §7Achat et repères utiles",
                        "",
                        "§e➜ §fOuvrir le warp"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(
                        Material.SLIME_BALL,
                        "§6✦ §fMini jeux §6✦",
                        "§8• §7Zone d'activités",
                        "§8• §7Jeux et événements",
                        "",
                        "§e➜ §fOuvrir /warp minijeux"
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.ENDER_PEARL,
                        "§6✦ §fExploration §6✦",
                        "§8• §7Téléportation aléatoire",
                        "§8• §7Départ en monde survie",
                        "",
                        "§e➜ §fPartir explorer"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.BELL,
                        "§6✦ §fVille §6✦",
                        "§8• §7Retour à votre ville",
                        "§8• §7Spawn municipal Towny",
                        "",
                        "§e➜ §fTéléporter"
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
