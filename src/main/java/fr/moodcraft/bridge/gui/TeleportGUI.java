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
                        "§8✦ §6Téléportation §aMood§6Craft §8✦"
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
                                "§8----- §6✦ §aMood§6Craft §fTéléportation §6✦ §8-----",
                                "",
                                "§7Choisis où aller.",
                                "",
                                "§8• §7Spawn",
                                "§8• §7Boutique",
                                "§8• §7Mini-jeux",
                                "§8• §7Exploration",
                                "§8• §7Ville"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.LODESTONE,
                                "§6✦ §fSpawn §6✦",
                                "§7Retour au centre",
                                "§7de §aMood§6Craft§7.",
                                "",
                                "§8• §7Banque",
                                "§8• §7Marché",
                                "§8• §7Services",
                                "",
                                "§eClique pour y aller"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.EMERALD_BLOCK,
                        "§6✦ §fAdminShop §6✦",
                        "§7Boutique officielle",
                        "§7du serveur.",
                        "",
                        "§8• §7Items utiles",
                        "§8• §7Prix fixes",
                        "",
                        "§eClique pour ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(
                        Material.NETHER_STAR,
                        "§6✦ §fMini-jeux §6✦",
                        "§7Rejoins les activités",
                        "§7du serveur.",
                        "",
                        "§8• §7Défis",
                        "§8• §7Events",
                        "§8• §7Récompenses",
                        "",
                        "§eClique pour jouer"
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.SPYGLASS,
                        "§6✦ §fExploration §6✦",
                        "§7Pars loin du spawn",
                        "§7pour commencer l'aventure.",
                        "",
                        "§8• §7Zone sauvage",
                        "§8• §7Ressources",
                        "§8• §7Construction libre",
                        "",
                        "§eClique pour explorer"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.MAP,
                        "§6✦ §fVille §6✦",
                        "§7Retourne dans",
                        "§7ta ville.",
                        "",
                        "§8• §7Spawn de ville",
                        "§8• §7Claims",
                        "§8• §7Nation",
                        "",
                        "§eClique pour y aller"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au menu principal.",
                        "",
                        "§cClique pour revenir"
                )
        );

        GUIManager.open(
                p,
                "teleport",
                inv
        );
    }
}