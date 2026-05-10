package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class DepositGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §aDépôt"
                );

        Economy eco =
                VaultHook.getEconomy();

        double cash =
                eco != null
                        ? eco.getBalance(p)
                        : 0;

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        double total =
                cash + bank;

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.GOLD_BLOCK,
                                "§6✦ Compte bancaire",
                                "§8----- §6Solde §8-----",
                                "§7Dépose ton argent en banque.",
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
                SafeGUI.item(
                        Material.LIME_DYE,
                        "§a✦ 100€",
                        "§7Petit dépôt rapide.",
                        "",
                        cash >= 100
                                ? "§a✔ Disponible"
                                : "§c✘ Fonds insuffisants",
                        "",
                        "§e▶ Déposer"
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.EMERALD,
                                "§a✦ 1 000€",
                                "§7Dépôt standard.",
                                "",
                                cash >= 1000
                                        ? "§a✔ Disponible"
                                        : "§c✘ Fonds insuffisants",
                                "",
                                "§e▶ Déposer"
                        )
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(
                        Material.EMERALD_BLOCK,
                        "§6✦ 10 000€",
                        "§7Gros dépôt bancaire.",
                        "",
                        cash >= 10000
                                ? "§a✔ Disponible"
                                : "§c✘ Fonds insuffisants",
                        "",
                        "§e▶ Déposer"
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.CHEST,
                                "§e✦ Tout déposer",
                                "§7Transfère tout ton liquide.",
                                "",
                                "§8• §7Disponible: §a"
                                        + SafeGUI.money(cash)
                                        + "€",
                                "",
                                cash > 0
                                        ? "§a✔ Disponible"
                                        : "§c✘ Aucun fond",
                                "",
                                "§e▶ Déposer"
                        )
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.OAK_SIGN,
                        "§d✦ Montant libre",
                        "§7Choisis ton propre montant.",
                        "",
                        "§8• §7Décimales acceptées",
                        "§8• §7Vérification auto",
                        "",
                        "§e▶ Saisir"
                )
        );

        SafeGUI.safeSet(inv, 31,
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
                "bank_deposit",
                inv
        );
    }
}