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
                        GuiTitle.of("Banque")
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
                                "§8• §7Mettre de l'argent en banque",
                                "§8• §7Saisie dans le chat",
                                "",
                                "§e➜ §fClique pour déposer"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.REDSTONE,
                        "§6✦ §fRetirer §6✦",
                        "§8• §7Sortir de l'argent",
                        "§8• §7Saisie dans le chat",
                        "",
                        "§e➜ §fClique pour retirer"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(
                        Material.PAPER,
                        "§6✦ §fVirement §6✦",
                        "§8• §7Envoyer de l'argent",
                        "§8• §7Vers un autre joueur",
                        "",
                        "§e➜ §fOuvrir"
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§6✦ §fIBAN §6✦",
                        "§8• §7Votre identité bancaire",
                        "§8• §7Utile pour les virements",
                        "",
                        "§e➜ §fAfficher"
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§6✦ §fHistorique §6✦",
                        "§8• §7Voir les mouvements",
                        "§8• §7Dépôts, retraits, virements",
                        "",
                        "§e➜ §fConsulter"
                )
        );

        SafeGUI.safeSet(inv, 23,
                SafeGUI.item(
                        Material.CLOCK,
                        "§6✦ §fActivité §6✦",
                        "§8• §7Résumé du compte",
                        "§8• §7Flux bancaires personnels",
                        "",
                        "§e➜ §fConsulter"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ §fRetour §c✦",
                        "§8• §7Menu principal",
                        "",
                        "§c✖ §fRevenir"
                )
        );

        p.openInventory(inv);

        GUIManager.set(
                p,
                new fr.moodcraft.bridge.handler.BankHandler()
        );
    }
}
