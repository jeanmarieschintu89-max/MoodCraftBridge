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

public class WithdrawGUI {

    public static final String ID =
            "bank_withdraw";

    //
    // 📤 OPEN
    //

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §cRetrait Bancaire"
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
        // 🏦 INFOS
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
        // 💸 -100
        //

        SafeGUI.safeSet(inv, 11,

                SafeGUI.item(

                        Material.REDSTONE,

                        "§c✦ Retirer 100€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retrait bancaire rapide.",

                        "",

                        "§7Solde bancaire: §6"
                                + SafeGUI.money(bank)
                                + "€",

                        "",

                        bank >= 100

                                ? "§a✔ Disponible"

                                : "§c✘ Solde insuffisant",

                        "",

                        "§e▶ Retirer"
                )
        );

        //
        // 💸 -1000
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.REDSTONE_BLOCK,

                                "§c✦ Retirer 1 000€",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Retrait bancaire standard.",

                                "",

                                "§7Solde bancaire: §6"
                                        + SafeGUI.money(bank)
                                        + "€",

                                "",

                                bank >= 1000

                                        ? "§a✔ Disponible"

                                        : "§c✘ Solde insuffisant",

                                "",

                                "§e▶ Retirer"
                        )
                )
        );

        //
        // 💸 -10000
        //

        SafeGUI.safeSet(inv, 15,

                SafeGUI.item(

                        Material.NETHERITE_BLOCK,

                        "§4✦ Retirer 10 000€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retrait bancaire majeur.",

                        "",

                        "§7Solde bancaire: §6"
                                + SafeGUI.money(bank)
                                + "€",

                        "",

                        bank >= 10000

                                ? "§a✔ Disponible"

                                : "§c✘ Solde insuffisant",

                        "",

                        "§e▶ Retirer"
                )
        );

        //
        // 💰 TOUT RETIRER
        //

        SafeGUI.safeSet(inv, 21,

                SafeGUI.item(

                        Material.GOLD_BLOCK,

                        "§e✦ Tout Retirer",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retire l'intégralité",

                        "§7du compte bancaire.",

                        "",

                        "§7Disponible: §6"
                                + SafeGUI.money(bank)
                                + "€",

                        "",

                        bank > 0

                                ? "§a✔ Disponible"

                                : "§c✘ Aucun fond",

                        "",

                        "§e▶ Retirer"
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

                        "§7Effectuer un retrait",

                        "§7manuel sécurisé.",

                        "",

                        "§8• Support décimales",

                        "§8• Vérification bancaire",

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
                ID,
                inv
        );
    }
}