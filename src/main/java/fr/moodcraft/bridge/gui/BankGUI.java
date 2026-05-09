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
                        "§8✦ §6Banque"
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
                                "§6✦ Banque MoodCraft",
                                "§8----- §6Compte §8-----",
                                "§7Consulte tes fonds.",
                                "",
                                "§8• §7Liquide: §a"
                                        + SafeGUI.money(cash)
                                        + "€",
                                "§8• §7Banque: §6"
                                        + SafeGUI.money(bank)
                                        + "€",
                                "§8• §7Total: §e"
                                        + SafeGUI.money(total)
                                        + "€"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.CHEST,
                                "§a✦ Déposer",
                                "§7Ajoute de l'argent au compte.",
                                "",
                                "§e▶ Ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.HOPPER,
                        "§c✦ Retirer",
                        "§7Récupère de l'argent liquide.",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§e✦ Virement",
                                "§7Envoie de l'argent à un joueur.",
                                "",
                                "§e▶ Ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.NAME_TAG,
                        "§b✦ IBAN",
                        "§7Consulte ton identité bancaire.",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 21,
                SafeGUI.item(
                        Material.BOOK,
                        "§d✦ Historique",
                        "§7Retrouve tes transactions.",
                        "",
                        "§8• §7Dépôts",
                        "§8• §7Retraits",
                        "§8• §7Virements",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 23,
                SafeGUI.item(
                        Material.AMETHYST_SHARD,
                        "§5✦ Activité",
                        "§7Statistiques économiques.",
                        "",
                        "§8• §7Profit",
                        "§8• §7Volume",
                        "§8• §7Marché",
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