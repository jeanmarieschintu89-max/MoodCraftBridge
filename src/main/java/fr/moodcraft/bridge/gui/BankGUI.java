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
                        "§6✦ §8Banque §aMood§6Craft §6✦"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        double cash = 0;

        try {

            cash =
                    VaultHook.getBalance(p);

        } catch (Exception ignored) {}

        double total =
                bank + cash;

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.GOLD_BLOCK,
                                "§6✦ §fCompte bancaire §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Ton argent personnel.",
                                "",
                                "§8• §7Liquide: §a"
                                        + SafeGUI.money(cash)
                                        + "€",
                                "§8• §7Banque: §6"
                                        + SafeGUI.money(bank)
                                        + "€",
                                "§8• §7Total: §e"
                                        + SafeGUI.money(total)
                                        + "€",
                                "",
                                "§8• §7Pour les gros paiements:",
                                "§8• §e/contrat"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.CHEST,
                                "§6✦ §fDéposer §6✦",
                                "§7Mettre ton argent liquide",
                                "§7dans ta banque.",
                                "",
                                "§8• §7Montant dans le chat",
                                "§8• §7Exemple: §e5000",
                                "",
                                "§a✔ Clique pour saisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.HOPPER,
                        "§6✦ §fRetirer §6✦",
                        "§7Sortir de l'argent",
                        "§7de ta banque.",
                        "",
                        "§8• §7Montant dans le chat",
                        "§8• §7Exemple: §e5000",
                        "",
                        "§a✔ Clique pour saisir"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§6✦ §fVirement §6✦",
                                "§7Envoyer de l'argent",
                                "§7à un joueur.",
                                "",
                                "§8• §7Choisir le joueur",
                                "§8• §7Montant dans le chat",
                                "§8• §7Confirmation finale",
                                "",
                                "§e⚠ Paiement pro: §f/contrat",
                                "",
                                "§a✔ Ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§6✦ §fIBAN §6✦",
                        "§7Voir ton code bancaire.",
                        "",
                        "§8• §7Utile pour les virements",
                        "§8• §7À partager avec prudence",
                        "",
                        "§eClique pour voir"
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§6✦ §fHistorique §6✦",
                        "§7Voir les mouvements",
                        "§7de ton compte.",
                        "",
                        "§8• §7Dépôts",
                        "§8• §7Retraits",
                        "§8• §7Virements",
                        "",
                        "§eClique pour ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 23,
                SafeGUI.item(
                        Material.AMETHYST_SHARD,
                        "§6✦ §fActivité §6✦",
                        "§7Résumé de ton compte.",
                        "",
                        "§8• §7Volume bancaire",
                        "§8• §7Flux personnels",
                        "",
                        "§8Bientôt disponible"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ Retour",
                        "§7Retour au menu principal.",
                        "",
                        "§cClique pour revenir"
                )
        );

        GUIManager.open(
                p,
                "bank_main",
                inv
        );
    }
}