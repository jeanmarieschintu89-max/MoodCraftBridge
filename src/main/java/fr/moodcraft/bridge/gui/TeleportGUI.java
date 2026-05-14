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
                        "§6✦ §8Téléportation §6✦"
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

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.LODESTONE,
                                "§6✦ §fSpawn §6✦",
                                "§8• §7Centre de §aMood§6Craft",
                                "§8• §7Banque et services",
                                "§8• §7Retour sécurisé",
                                "",
                                "§e➜ §fSe téléporter"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.EMERALD_BLOCK,
                        "§6✦ §fAdminShop §6✦",
                        "§8• §7Boutique officielle",
                        "§8• §7Items utiles",
                        "§8• §7Prix fixes",
                        "",
                        "§e➜ §fOuvrir la boutique"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(
                        Material.NETHER_STAR,
                        "§6✦ §fMini-jeux §6✦",
                        "§8• §7Activités serveur",
                        "§8• §7Events et défis",
                        "§8• §7Récompenses",
                        "",
                        "§e➜ §fRejoindre"
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.SPYGLASS,
                        "§6✦ §fExploration §6✦",
                        "§8• §7Zone sauvage",
                        "§8• §7Ressources naturelles",
                        "§8• §7Construction libre",
                        "",
                        "§e➜ §fPartir explorer"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.MAP,
                        "§6✦ §fVille §6✦",
                        "§8• §7Spawn de ville",
                        "§8• §7Claims municipaux",
                        "§8• §7Territoire Towny",
                        "",
                        "§e➜ §fRetourner en ville"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§6✦ §fRetour §6✦",
                        "§8• §7Retour au menu principal"
                )
        );

        GUIManager.open(
                p,
                "teleport",
                inv
        );
    }
}