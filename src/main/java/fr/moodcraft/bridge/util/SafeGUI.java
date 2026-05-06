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
                        "§6✦ §0Banque MoodCraft"
                );

        //
        // 🖤 BORDURES
        //

        SafeGUI.fillBorders(
                inv,
                Material.BLACK_STAINED_GLASS_PANE
        );

        //
        // 💰 SOLDES
        //

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        double cash = 0;

        try {

            cash =
                    VaultHook.getBalance(p);

        } catch (Exception ignored) {}

        //
        // 💰 ARGENT
        //

        SafeGUI.safeSet(

                inv,

                4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.GOLD_INGOT,

                                "§6✦ §fCompte bancaire",

                                "§8━━━━━━━━━━━━━━━━",
                                "",
                                "§7Liquide:",
                                "§a" + SafeGUI.money(cash) + "€",
                                "",
                                "§7Banque:",
                                "§6" + SafeGUI.money(bank) + "€",
                                "",
                                "§8MoodCraft Financial System"
                        )
                )
        );

        //
        // 📥 DEPOT
        //

        SafeGUI.safeSet(

                inv,

                10,

                SafeGUI.item(

                        Material.CHEST,

                        "§a✦ §fDéposer",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Déposer de l'argent",
                        "§7sur ton compte bancaire.",
                        "",
                        "§e▶ Cliquer"
                )
        );

        //
        // 📤 RETRAIT
        //

        SafeGUI.safeSet(

                inv,

                12,

                SafeGUI.item(

                        Material.HOPPER,

                        "§c✦ §fRetirer",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Retirer de l'argent",
                        "§7depuis la banque.",
                        "",
                        "§e▶ Cliquer"
                )
        );

        //
        // 💸 VIREMENT
        //

        SafeGUI.safeSet(

                inv,

                14,

                SafeGUI.item(

                        Material.WRITABLE_BOOK,

                        "§e✦ §fVirement",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Envoyer de l'argent",
                        "§7à un autre joueur.",
                        "",
                        "§e▶ Cliquer"
                )
        );

        //
        // 🏦 IBAN
        //

        SafeGUI.safeSet(

                inv,

                16,

                SafeGUI.item(

                        Material.BOOK,

                        "§b✦ §fIBAN",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Consulter ton",
                        "§7RIB bancaire.",
                        "",
                        "§e▶ Cliquer"
                )
        );

        //
        // 📜 HISTORIQUE
        //

        SafeGUI.safeSet(

                inv,

                20,

                SafeGUI.item(

                        Material.KNOWLEDGE_BOOK,

                        "§d✦ §fHistorique",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Consulter toutes",
                        "§7tes transactions.",
                        "",
                        "§8• Dépôts",
                        "§8• Retraits",
                        "§8• Virements",
                        "§8• Marché",
                        "",
                        "§e▶ Cliquer"
                )
        );

        //
        // 📈 ACTIVITÉ
        //

        SafeGUI.safeSet(

                inv,

                24,

                SafeGUI.item(

                        Material.AMETHYST_SHARD,

                        "§5✦ §fActivité",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Statistiques",
                        "§7économiques avancées.",
                        "",
                        "§8• Profit",
                        "§8• Volume",
                        "§8• Trading",
                        "",
                        "§e▶ Bientôt"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(

                inv,

                31,

                SafeGUI.item(

                        Material.BARRIER,

                        "§c✦ §fRetour",

                        "§7Retour au menu principal."
                )
        );

        //
        // 🚀 OPEN
        //

        GUIManager.open(
                p,
                "bank_main",
                inv
        );
    }
}