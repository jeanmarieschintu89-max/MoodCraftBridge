package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class IbanGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §bIBAN"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.WRITABLE_BOOK,
                                "§6✦ Réseau bancaire",
                                "§8----- §6IBAN MoodCraft §8-----",
                                "§7Effectue un virement via IBAN.",
                                "",
                                "§8• §7Compatible hors ligne",
                                "§8• §7Transactions sécurisées",
                                "§8• §7Historique sauvegardé"
                        )
                )
        );

        SafeGUI.safeSet(inv, 13,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§b✦ Entrer un IBAN",
                                "§7Saisis un IBAN dans le chat.",
                                "",
                                "§8• §7Validation automatique",
                                "§8• §7Format sécurisé",
                                "",
                                "§e▶ Saisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§e✦ Informations",
                        "§7Les IBAN sont liés",
                        "§7aux comptes MoodCraft.",
                        "",
                        "§8• §7Transfert distant",
                        "§8• §7Compatible contrats",
                        "§8• §7Historique complet"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au menu précédent.",
                        "",
                        "§c▶ Retour"
                )
        );

        GUIManager.open(
                p,
                "iban_gui",
                inv
        );
    }
}