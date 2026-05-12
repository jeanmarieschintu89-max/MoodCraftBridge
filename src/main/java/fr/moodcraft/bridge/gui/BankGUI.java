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
                        "§8✦ §6Banque §aMood§6Craft"
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
                                "§7Consulte tes fonds personnels.",
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
                                "§7Les gros paiements professionnels",
                                "§7doivent passer par §e/contrat§7."
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.CHEST,
                                "§6✦ §fDéposer des fonds §6✦",
                                "§7Transférer ton argent liquide",
                                "§7vers ta banque personnelle.",
                                "",
                                "§8• §7Saisie directe dans le chat",
                                "§8• §7Exemple: §e5000",
                                "§8• §7Historique sauvegardé",
                                "",
                                "§a✔ Cliquer pour saisir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.HOPPER,
                        "§6✦ §fRetirer des fonds §6✦",
                        "§7Récupérer de l'argent",
                        "§7depuis ta banque personnelle.",
                        "",
                        "§8• §7Saisie directe dans le chat",
                        "§8• §7Exemple: §e5000",
                        "§8• §7Historique sauvegardé",
                        "",
                        "§a✔ Cliquer pour saisir"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§6✦ §fVirement personnel §6✦",
                                "§7Envoyer de l'argent à un joueur.",
                                "",
                                "§8• §7Choisir le joueur",
                                "§8• §7Écrire le montant dans le chat",
                                "§8• §7Confirmer le virement",
                                "",
                                "§e⚠ Limite anti-fraude active",
                                "§7Les paiements professionnels",
                                "§7doivent passer par §e/contrat§7.",
                                "",
                                "§a✔ Ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§6✦ §fIBAN §6✦",
                        "§7Consulte ton identité bancaire.",
                        "",
                        "§8• §7Nom du compte",
                        "§8• §7Identifiant bancaire",
                        "§8• §7Informations personnelles",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§6✦ §fHistorique §6✦",
                        "§7Retrouve tes transactions.",
                        "",
                        "§8• §7Dépôts",
                        "§8• §7Retraits",
                        "§8• §7Virements",
                        "§8• §7Paiements surveillés",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 23,
                SafeGUI.item(
                        Material.AMETHYST_SHARD,
                        "§6✦ §fActivité bancaire §6✦",
                        "§7Statistiques économiques.",
                        "",
                        "§8• §7Volume bancaire",
                        "§8• §7Flux personnels",
                        "§8• §7Activité du compte",
                        "",
                        "§8▶ Bientôt"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ Retour",
                        "§7Retour au menu principal.",
                        "",
                        "§c▶ Retour"
                )
        );

        GUIManager.open(
                p,
                "bank_main",
                inv
        );
    }
}