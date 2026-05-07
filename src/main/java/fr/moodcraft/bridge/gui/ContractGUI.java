package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class ContractGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §6Contrats MoodCraft"
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
        // 📜 HEADER
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.WRITABLE_BOOK,

                                "§6✦ Réseau de Contrats",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Système économique",

                                "§7de missions entre joueurs.",

                                "",

                                "§8• Livraison",

                                "§8• Réputation",

                                "§8• Paiement sécurisé",

                                "",

                                "§e▶ MoodCraft Contracts"
                        )
                )
        );

        //
        // 🌍 CONTRATS PUBLICS
        //

        SafeGUI.safeSet(inv, 11,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.BOOK,

                                "§e✦ Contrats Publics",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Consulter les contrats",

                                "§7disponibles sur le serveur.",

                                "",

                                "§8• Missions ouvertes",

                                "§8• Récompenses",

                                "§8• Réputation",

                                "",

                                "§e▶ Explorer"
                        )
                )
        );

        //
        // 📦 MES CONTRATS
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.item(

                        Material.CHEST,

                        "§b✦ Mes Contrats",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Voir tes contrats",

                        "§7actifs et terminés.",

                        "",

                        "§8• En cours",

                        "§8• Terminés",

                        "§8• Historique",

                        "",

                        "§e▶ Consulter"
                )
        );

        //
        // ➕ CREATE
        //

        SafeGUI.safeSet(inv, 15,

                SafeGUI.item(

                        Material.EMERALD,

                        "§a✦ Créer un Contrat",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Créer une mission",

                        "§7et proposer une récompense.",

                        "",

                        "§8• Livraison ressources",

                        "§8• Paiement automatique",

                        "",

                        "§e▶ Créer"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.BARRIER,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu principal.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "contracts",

                inv
        );
    }
}