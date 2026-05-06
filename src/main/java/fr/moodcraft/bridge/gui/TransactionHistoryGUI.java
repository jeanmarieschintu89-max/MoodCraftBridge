package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public class TransactionHistoryGUI {

    //
    // 📄 20 transactions par page
    //

    private static final int PAGE_SIZE = 20;

    public static void open(Player p,
                            int page) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§d✦ §0Historique bancaire"
                );

        //
        // 🖤 FILL
        //

        for (int i = 0; i < inv.getSize(); i++) {

            SafeGUI.safeSet(

                    inv,

                    i,

                    SafeGUI.item(
                            Material.BLACK_STAINED_GLASS_PANE,
                            " "
                    )
            );
        }

        //
        // 📜 DATA
        //

        List<String> history =

                TransactionManager.getHistory(
                        p.getUniqueId()
                );

        List<String> pageData =

                TransactionManager.getPage(
                        history,
                        page,
                        PAGE_SIZE
                );

        //
        // 📦 SLOTS
        //

        int[] slots = {

                10,11,12,13,14,15,16,

                19,20,21,22,23,24,25,

                28,29,30,32,33,34
        };

        //
        // 📜 TRANSACTIONS
        //

        for (int i = 0;
             i < pageData.size();
             i++) {

            String line =
                    pageData.get(i);

            Material mat =
                    Material.PAPER;

            //
            // 📥 DEPOT
            //

            if (line.contains("[DEPOSIT]")) {

                mat = Material.EMERALD;
            }

            //
            // 📤 RETRAIT
            //

            else if (line.contains("[WITHDRAW]")) {

                mat = Material.REDSTONE;
            }

            //
            // 💸 VIREMENT
            //

            else if (line.contains("[TRANSFER]")) {

                mat = Material.WRITABLE_BOOK;
            }

            //
            // 🛒 ACHAT
            //

            else if (line.contains("[MARKET_BUY]")) {

                mat = Material.CHEST_MINECART;
            }

            //
            // 💰 VENTE
            //

            else if (line.contains("[MARKET_SELL]")) {

                mat = Material.GOLD_INGOT;
            }

            SafeGUI.safeSet(

                    inv,

                    slots[i],

                    SafeGUI.item(

                            mat,

                            "§fTransaction",

                            "§8━━━━━━━━━━━━━━━━",
                            "",
                            line,
                            "",
                            "§8MoodCraft Economy"
                    )
            );
        }

        //
        // 📄 PAGE
        //

        SafeGUI.safeSet(

                inv,

                4,

                SafeGUI.item(

                        Material.BOOK,

                        "§d✦ §fHistorique bancaire",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Transactions:",
                        "§f" + history.size(),
                        "",
                        "§7Page actuelle:",
                        "§e" + page,
                        "",
                        "§8MoodCraft Financial System"
                )
        );

        //
        // ◀ PAGE PRÉCÉDENTE
        //

        if (page > 1) {

            SafeGUI.safeSet(

                    inv,

                    27,

                    SafeGUI.item(

                            Material.ARROW,

                            "§e← §fPage précédente",

                            "§7Revenir à la page",
                            "§7précédente."
                    )
            );
        }

        //
        // ▶ PAGE SUIVANTE
        //

        if (history.size() > page * PAGE_SIZE) {

            SafeGUI.safeSet(

                    inv,

                    35,

                    SafeGUI.item(

                            Material.ARROW,

                            "§e→ §fPage suivante",

                            "§7Accéder à la page",
                            "§7suivante."
                    )
            );
        }

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(

                inv,

                31,

                SafeGUI.item(

                        Material.BARRIER,

                        "§c✦ §fRetour",

                        "§7Retour à la banque"
                )
        );

        //
        // 🚀 OPEN
        //

        GUIManager.open(
                p,
                "transaction_history",
                inv
        );
    }
}