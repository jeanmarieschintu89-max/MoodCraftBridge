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
                        "§8✦ §bTéléportation MoodCraft"
                );

        //
        // 🌌 FOND
        //

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        //
        // 🌍 RESSOURCES
        //

        SafeGUI.safeSet(inv, 10,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.DEEPSLATE_DIAMOND_ORE,

                                "§a✦ Monde Ressources",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Extraction de ressources",

                                "§7et économie minière.",

                                "",

                                "§8• Minerais rares",

                                "§8• Farming",

                                "§8• Récolte intensive",

                                "",

                                "§e▶ Accéder"
                        )
                )
        );

        //
        // 🏪 ADMIN SHOP
        //

        SafeGUI.safeSet(inv, 12,

                SafeGUI.item(

                        Material.EMERALD_BLOCK,

                        "§6✦ AdminShop",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Boutique officielle",

                        "§7du serveur MoodCraft.",

                        "",

                        "§8• Items spéciaux",

                        "§8• Ressources",

                        "§8• Économie contrôlée",

                        "",

                        "§e▶ Ouvrir"
                )
        );

        //
        // 🎮 MINI JEUX
        //

        SafeGUI.safeSet(inv, 14,

                SafeGUI.item(

                        Material.NETHER_STAR,

                        "§d✦ Mini-Jeux",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Activités compétitives",

                        "§7et récompenses exclusives.",

                        "",

                        "§8• Events",

                        "§8• Défis",

                        "§8• Récompenses",

                        "",

                        "§e▶ Jouer"
                )
        );

        //
        // 🌌 EXPLORATION
        //

        SafeGUI.safeSet(inv, 16,

                SafeGUI.item(

                        Material.ENDER_EYE,

                        "§5✦ Exploration",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Téléportation libre",

                        "§7dans le monde sauvage.",

                        "",

                        "§8• Découverte",

                        "§8• Aventure",

                        "§8• Zones éloignées",

                        "",

                        "§e▶ Explorer"
                )
        );

        //
        // 🧭 SPAWN
        //

        SafeGUI.safeSet(inv, 20,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.COMPASS,

                                "§e✦ Spawn Central",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Retour au centre",

                                "§7économique MoodCraft.",

                                "",

                                "§8• Banque",

                                "§8• Marché",

                                "§8• Services",

                                "",

                                "§e▶ Retourner"
                        )
                )
        );

        //
        // 🏛️ VILLE
        //

        SafeGUI.safeSet(inv, 24,

                SafeGUI.item(

                        Material.BRICKS,

                        "§a✦ Ville",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Gestion territoriale",

                        "§7et développement urbain.",

                        "",

                        "§8• Town",

                        "§8• Claims",

                        "§8• Gestion économique",

                        "",

                        "§e▶ Accéder"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.ARROW,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu principal.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(
                p,
                "teleport",
                inv
        );
    }
}