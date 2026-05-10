package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class TransferTypeGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        27,
                        "§8✦ §eVirement"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§6✦ Virement bancaire",
                                "§8----- §6Banque §8-----",
                                "§7Choisis le type de transfert.",
                                "",
                                "§8• §7Sécurisé",
                                "§8• §7Historique sauvegardé",
                                "§8• §7Vérification active"
                        )
                )
        );

        SafeGUI.safeSet(inv, 11,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PLAYER_HEAD,
                                "§a✦ Joueur",
                                "§7Envoie à un joueur connecté.",
                                "",
                                "§8• §7Rapide",
                                "§8• §7Sélection visuelle",
                                "",
                                "§e▶ Choisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 15,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§b✦ IBAN",
                        "§7Envoie avec un IBAN MoodCraft.",
                        "",
                        "§8• §7Compatible hors ligne",
                        "§8• §7Transaction sécurisée",
                        "",
                        "§e▶ Continuer"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour à la banque.",
                        "",
                        "§c▶ Retour"
                )
        );

        GUIManager.open(
                p,
                "transfer_type",
                inv
        );
    }
}