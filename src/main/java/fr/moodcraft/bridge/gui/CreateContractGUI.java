package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class CreateContractGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §6Créer un Contrat"
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

                                "§6✦ Terminal Contrats MoodCraft",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Le réseau économique MoodCraft",

                                "§7permet aux joueurs de publier",

                                "§7des demandes de ressources.",

                                "",

                                "§8• Paiement automatisé",

                                "§8• Livraison sécurisée",

                                "§8• Réputation économique",

                                "",

                                "§e▶ Réseau économique"
                        )
                )
        );

        //
        // 📦 SLOT ITEM
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.CHEST,

                                "§e✦ Dépôt Ressource",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Dépose une ressource",

                                "§7dans l'emplacement central.",

                                "",

                                "§7Exemples compatibles :",

                                "§8• Minerais",

                                "§8• Farming",

                                "§8• Redstone",

                                "§8• Ressources Nether",

                                "",

                                "§e▶ Déposer une ressource"
                        )
                )
        );

        //
        // 📘 EXPLICATION
        //

        SafeGUI.safeSet(inv, 20,

                SafeGUI.item(

                        Material.PAPER,

                        "§b✦ Fonctionnement du Réseau",

                        "§8━━━━━━━━━━━━━━━━",

                        "§71. Déposer une ressource",

                        "§72. Définir une quantité",

                        "§73. Choisir une récompense",

                        "",

                        "§7Le contrat sera publié",

                        "§7sur le réseau MoodCraft.",

                        "",

                        "§7Les joueurs pourront livrer",

                        "§7automatiquement les ressources.",

                        "",

                        "§e▶ Système automatisé"
                )
        );

        //
        // ✅ CONTINUER
        //

        SafeGUI.safeSet(inv, 22,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.EMERALD,

                                "§a✦ Publier le Contrat",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Validation du contrat",

                                "§7et publication réseau.",

                                "",

                                "§8• Analyse automatique",

                                "§8• Vérification sécurisée",

                                "",

                                "§e▶ Continuer"
                        )
                )
        );

        //
        // 🌍 INFOS ÉCO
        //

        SafeGUI.safeSet(inv, 24,

                SafeGUI.item(

                        Material.BEACON,

                        "§6✦ Réseau Économique",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Les contrats influencent",

                        "§7l'économie globale du serveur.",

                        "",

                        "§8• Commerce joueur",

                        "§8• Logistique",

                        "§8• Réputation",

                        "§8• Production",

                        "",

                        "§e▶ MoodCraft Economy"
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

                        "§7Retour au réseau contrats.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "create_contract",

                inv
        );
    }
}