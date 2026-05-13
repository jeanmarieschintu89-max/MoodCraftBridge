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
                        "§8✦ §6IBAN §8✦"
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
                                "§6✦ §fVirement par IBAN §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Envoie de l'argent",
                                "§7avec l'IBAN d'un joueur.",
                                "",
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
                                "§7Écris l'IBAN dans le chat.",
                                "",
                                "§8• §7Exemple: §eMC-1234-ABCD",
                                "§8• §7Le compte sera vérifié",
                                "",
                                "§a✔ Cliquer pour saisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§6✦ §fInformations §6✦",
                        "§7Un IBAN identifie",
                        "§7un compte bancaire.",
                        "",
                        "§8• §7Virement à distance",
                        "§8• §7Joueur connecté ou non",
                        "§8• §7Trace dans l'historique",
                        "",
                        "§eUtilisez avec prudence"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour à la banque.",
                        "",
                        "§cClique pour revenir"
                )
        );

        GUIManager.open(
                p,
                "iban_gui",
                inv
        );
    }
}