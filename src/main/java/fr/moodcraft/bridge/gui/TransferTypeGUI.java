package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class TransferTypeGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                27,

                "§8✦ §eType de Virement"
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

                                Material.PAPER,

                                "§6✦ Système Bancaire MoodCraft",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Choisis le mode",

                                "§7de transfert bancaire.",

                                "",

                                "§8• Sécurisé",

                                "§8• Historique sauvegardé",

                                "§8• Vérification active"
                        )
                )
        );

        //
        // 👤 JOUEUR
        //

        SafeGUI.safeSet(inv, 11,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.PLAYER_HEAD,

                                "§a✦ Vers un Joueur",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Envoyer un virement",

                                "§7direct à un joueur",

                                "§7connecté au serveur.",

                                "",

                                "§8• Instantané",

                                "§8• Sélection visuelle",

                                "",

                                "§e▶ Sélectionner"
                        )
                )
        );

        //
        // 🏦 IBAN
        //

        SafeGUI.safeSet(inv, 15,

                SafeGUI.item(

                        Material.WRITABLE_BOOK,

                        "§b✦ Via IBAN",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Effectuer un transfert",

                        "§7grâce à un IBAN MoodCraft.",

                        "",

                        "§8• Compatible hors ligne",

                        "§8• Transactions sécurisées",

                        "",

                        "§e▶ Continuer"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 22,

                SafeGUI.item(

                        Material.ARROW,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu banque.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "transfer_type",

                inv
        );
    }
}