package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class BankGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§6✦ §8Banque §6✦"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        double bank = BankStorage.get(p.getUniqueId().toString());

        double cash = 0;

        try {
            cash = VaultHook.getBalance(p);
        } catch (Exception ignored) {
        }

        double total = bank + cash;

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.GOLD_BLOCK,
                                "§6✦ §fCompte bancaire §6✦",
                                "§8• §7Liquide : §a" + SafeGUI.money(cash) + "€",
                                "§8• §7Banque : §6" + SafeGUI.money(bank) + "€",
                                "§8• §7Total : §e" + SafeGUI.money(total) + "€",
                                "",
                                "§e➜ §fGère ton argent personnel"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.EMERALD,
                                "§6✦ §fDéposer §6✦",
                                "§8• §7Liquide vers banque",
                                "§8• §7Montant dans le chat",
                                "§8• §7Exemple : §e5000",
                                "",
                                "§e➜ §fSaisir un dépôt"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.HOPPER,
                        "§6✦ §fRetirer §6✦",
                        "§8• §7Banque vers liquide",
                        "§8• §7Montant dans le chat",
                        "§8• §7Exemple : §e5000",
                        "",
                        "§e➜ §fSaisir un retrait"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§6✦ §fVirement §6✦",
                                "§8• §7Envoyer à un joueur",
                                "§8• §7Choix du joueur",
                                "§8• §7Confirmation finale",
                                "§8• §7Paiement pro : §e/contrat",
                                "",
                                "§e➜ §fPréparer un virement"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§6✦ §fIBAN §6✦",
                        "§8• §7Code bancaire personnel",
                        "§8• §7Utile pour les virements",
                        "§8• §7À partager avec prudence",
                        "",
                        "§e➜ §fVoir mon IBAN"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.BOOK,
                        "§6✦ §fHistorique §6✦",
                        "§8• §7Dépôts",
                        "§8• §7Retraits",
                        "§8• §7Virements",
                        "",
                        "§e➜ §fVoir les mouvements"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§6✦ §fRetour §6✦",
                        "§8• §7Retour au menu principal",
                        "",
                        "§e➜ §fOuvrir /menu"
                )
        );

        GUIManager.open(
                p,
                "bank_main",
                inv
        );
    }
}
