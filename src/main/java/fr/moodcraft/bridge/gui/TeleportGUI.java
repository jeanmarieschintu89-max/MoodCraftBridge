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
                        "§8✦ §bTéléportation"
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
                                "§b✦ Menu Téléportation",
                                "§8----- §bVoyage §8-----",
                                "§7Choisis une destination.",
                                "",
                                "§8• §7Spawn",
                                "§8• §7Boutique",
                                "§8• §7Events",
                                "§8• §7Exploration",
                                "§8• §7Ville"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.LODESTONE,
                                "§e✦ Spawn",
                                "§7Retour au centre MoodCraft.",
                                "",
                                "§8• §7Banque",
                                "§8• §7Marché",
                                "§8• §7Services",
                                "",
                                "§e▶ Voyager"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.EMERALD_BLOCK,
                        "§6✦ AdminShop",
                        "§7Boutique officielle du serveur.",
                        "",
                        "§8• §7Items utiles",
                        "§8• §7Économie contrôlée",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(
                        Material.NETHER_STAR,
                        "§d✦ Mini-jeux",
                        "§7Events et activités serveur.",
                        "",
                        "§8• §7Défis",
                        "§8• §7Récompenses",
                        "",
                        "§e▶ Jouer"
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.SPYGLASS,
                        "§5✦ Exploration",
                        "§7Pars en aventure libre.",
                        "",
                        "§8• §7Découverte",
                        "§8• §7Zones sauvages",
                        "",
                        "§e▶ Explorer"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.MAP,
                        "§a✦ Ville",
                        "§7Accède à ton territoire.",
                        "",
                        "§8• §7Town",
                        "§8• §7Claims",
                        "§8• §7Nation",
                        "",
                        "§e▶ Accéder"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au menu principal.",
                        "",
                        "§c▶ Retour"
                )
        );

        GUIManager.open(
                p,
                "teleport",
                inv
        );
    }
}