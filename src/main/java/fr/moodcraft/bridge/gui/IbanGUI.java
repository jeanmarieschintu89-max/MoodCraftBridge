package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class IbanGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §bVirement IBAN"
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
        // 📄 HEADER
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.WRITABLE_BOOK,

                                "§6✦ Réseau Bancaire IBAN",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Le système IBAN permet",

                                "§7les transferts avancés",

                                "§7entre comptes MoodCraft.",

                                "",

                                "§8• Compatible hors ligne",

                                "§8• Transactions sécurisées",

                                "§8• Vérification bancaire"
                        )
                )
        );

        //
        // 💳 INPUT
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.PAPER,

                                "§b✦ Entrer un IBAN",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Clique puis écris",

                                "§7l'identifiant IBAN",

                                "§7dans le chat.",

                                "",

                                "§8• Format sécurisé",

                                "§8• Validation automatique",

                                "",

                                "§e▶ Saisir IBAN"
                        )
                )
        );

        //
        // 📘 INFO
        //

        SafeGUI.safeSet(inv, 21,

                SafeGUI.item(

                        Material.BOOK,

                        "§e✦ Informations IBAN",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Les IBAN MoodCraft",

                        "§7sont uniques et liés",

                        "§7aux comptes bancaires.",

                        "",

                        "§8• Transfert distant",

                        "§8• Historique sauvegardé",

                        "§8• Compatible contrats",

                        "",

                        "§e▶ Réseau économique"
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

                        "§7Retour au menu précédent.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "iban_gui",

                inv
        );
    }
}