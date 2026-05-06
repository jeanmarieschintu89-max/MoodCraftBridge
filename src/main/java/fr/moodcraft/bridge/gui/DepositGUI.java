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

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §aDépôt Bancaire"
        );

        //
        // 💰 ÉCONOMIE
        //

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

        //
        // 🌌 FOND
        //

        SafeGUI.fill(

                inv,

                Material.BLACK_STAINED_GLASS_PANE,

                " "
        );

        //
        // 📊 INFOS
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.GOLD_INGOT,

                                "§6✦ Compte MoodCraft",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Liquidités: §a"
                                        + SafeGUI.money(cash)
                                        + "€",

                                "§7Banque: §6"
                                        + SafeGUI.money(bank)
                                        + "€",

                                "",

                                "§7Patrimoine total: §e"
                                        + SafeGUI.money(cash + bank)
                                        + "€"
                        )
                )
        );

        //
        // 💰 +100
        //

        SafeGUI.safeSet(inv, 11,

                SafeGUI.item(

                        Material.LIME_DYE,

                        "§a✦ Déposer 100€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Transfert bancaire rapide.",

                        "",

                        "§7Liquidités: §a"
                                + SafeGUI.money(cash)
                                + "€",

                        "",

                        cash >= 100

                                ? "§a✔ Disponible"

                                : "§c✘ Fonds insuffisants",

                        "",

                        "§e▶ Déposer"
                )
        );

        //
        // 💰 +1000
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.EMERALD,

                                "§a✦ Déposer 1 000€",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Transfert bancaire standard.",

                                "",

                                "§7Liquidités: §a"
                                        + SafeGUI.money(cash)
                                        + "€",

                                "",

                                cash >= 1000

                                        ? "§a✔ Disponible"

                                        : "§c✘ Fonds insuffisants",

                                "",

                                "§e▶ Déposer"
                        )
                )
        );

        //
        // 💰 +10000
        //

        SafeGUI.safeSet(inv, 15,

                SafeGUI.item(

                        Material.EMERALD_BLOCK,

                        "§6✦ Déposer 10 000€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Transfert bancaire important.",

                        "",

                        "§7Liquidités: §a"
                                + SafeGUI.money(cash)
                                + "€",

                        "",

                        cash >= 10000

                                ? "§a✔ Disponible"

                                : "§c✘ Fonds insuffisants",

                        "",

                        "§e▶ Déposer"
                )
        );

        //
        // 🏦 MAX
        //

        SafeGUI.safeSet(inv, 21,

                SafeGUI.item(

                        Material.CHEST,

                        "§e✦ Tout Déposer",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Dépose l'intégralité",

                        "§7de tes liquidités.",

                        "",

                        "§7Montant disponible: §a"
                                + SafeGUI.money(cash)
                                + "€",

                        "",

                        cash > 0

                                ? "§a✔ Disponible"

                                : "§c✘ Aucun fond",

                        "",

                        "§e▶ Transférer"
                )
        );

        //
        // ✍️ PERSONNALISÉ
        //

        SafeGUI.safeSet(inv, 23,

                SafeGUI.item(

                        Material.OAK_SIGN,

                        "§d✦ Montant Personnalisé",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Saisir un montant",

                        "§7manuel sécurisé.",

                        "",

                        "§8• Support décimales",

                        "§8• Vérification automatique",

                        "",

                        "§e▶ Saisir montant"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.ARROW,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour à la banque.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(
                p,
                "bank_deposit",
                inv
        );
    }
}