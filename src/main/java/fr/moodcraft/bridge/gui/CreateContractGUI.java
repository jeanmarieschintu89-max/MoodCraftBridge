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

                                "§6✦ Création de Contrat",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Dépose un item",

                                "§7dans l'emplacement central.",

                                "",

                                "§8• Tous les items vanilla",

                                "§8• Récompense personnalisée",

                                "§8• Livraison sécurisée",

                                "",

                                "§e▶ MoodCraft Contracts"
                        )
                )
        );

        //
        // 📦 SLOT ITEM
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.item(

                        Material.CHEST,

                        "§e✦ Déposer un Item",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Place un item ici",

                        "§7pour créer un contrat.",

                        "",

                        "§8Exemples:",

                        "§8• Minerais",

                        "§8• Blocs",

                        "§8• Farming",

                        "§8• Redstone",

                        "",

                        "§e▶ Déposer"
                )
        );

        //
        // ✅ CONTINUER
        //

        SafeGUI.safeSet(inv, 22,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.EMERALD,

                                "§a✦ Continuer",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Valider l'item",

                                "§7et continuer.",

                                "",

                                "§e▶ Continuer"
                        )
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