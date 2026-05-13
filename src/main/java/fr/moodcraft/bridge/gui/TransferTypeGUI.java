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
                        "§8✦ §6Virement §8✦"
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
                                "§6✦ §fVirement bancaire §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Choisis comment envoyer",
                                "§7ton argent.",
                                "",
                                "§8• §7Joueur connecté",
                                "§8• §7Ou IBAN",
                                "§8• §7Confirmation finale"
                        )
                )
        );

        SafeGUI.safeSet(inv, 11,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PLAYER_HEAD,
                                "§6✦ §fJoueur connecté §6✦",
                                "§7Envoyer à un joueur",
                                "§7actuellement en ligne.",
                                "",
                                "§8• §7Choisis le joueur",
                                "§8• §7Montant dans le chat",
                                "",
                                "§eClique pour choisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 15,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§6✦ §fIBAN §6✦",
                        "§7Envoyer avec le code",
                        "§7bancaire d'un joueur.",
                        "",
                        "§8• §7Fonctionne hors ligne",
                        "§8• §7Historique gardé",
                        "",
                        "§eClique pour continuer"
                )
        );

        SafeGUI.safeSet(inv, 22,
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
                "transfer_type",
                inv
        );
    }
}