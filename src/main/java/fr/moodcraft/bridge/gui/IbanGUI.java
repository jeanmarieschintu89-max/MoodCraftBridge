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
                        "§6✦ §8IBAN §6✦"
                );

        SafeGUI.fill(inv, Material.BLACK_STAINED_GLASS_PANE, " ");

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.WRITABLE_BOOK,
                                "§6✦ §fVirement par IBAN §6✦",
                                "§8• §7Envoie de l'argent avec l'IBAN",
                                "§8• §7Fonctionne hors ligne",
                                "§8• §7Historique sauvegardé",
                                "§8• §7Confirmation avant envoi"
                        )
                )
        );

        SafeGUI.safeSet(inv, 13,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§6✦ §fEntrer un IBAN §6✦",
                                "§8• §7Écris l'IBAN dans le chat",
                                "§8• §7Exemple : §eMC-1234-ABCD",
                                "§8• §7Le compte sera vérifié",
                                "",
                                "§e➜ §fSaisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§6✦ §fInformations §6✦",
                        "§8• §7Virement à distance",
                        "§8• §7Joueur connecté ou non",
                        "§8• §7Trace dans l'historique",
                        "",
                        "§e➜ §fUtiliser avec prudence"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ §fRetour §c✦",
                        "§8• §7Retour à la banque",
                        "",
                        "§c✖ §fRevenir"
                )
        );

        GUIManager.open(p, "iban_gui", inv);
    }
}
