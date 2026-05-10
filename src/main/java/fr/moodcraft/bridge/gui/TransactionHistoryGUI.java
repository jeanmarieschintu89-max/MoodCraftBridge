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

    private static final int PAGE_SIZE = 20;

    public static void open(
            Player p,
            int page
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §dHistorique Bancaire"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

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

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.KNOWLEDGE_BOOK,
                                "§d✦ Historique",
                                "§8----- §dTransactions §8-----",
                                "§7Toutes les opérations",
                                "§7économiques enregistrées.",
                                "",
                                "§8• §7Entrées: §a"
                                        + history.size(),
                                "§8• §7Page: §e"
                                        + page,
                                "",
                                "§e▶ Registre bancaire"
                        )
                )
        );

        int[] slots = {

                10,11,12,13,14,15,16,

                19,20,21,22,23,24,25,

                28,29,30,32,33,34
        };

        for (int i = 0;
             i < pageData.size();
             i++) {

            String line =
                    pageData.get(i);

            Material mat =
                    Material.PAPER;

            String name =
                    "§fTransaction";

            if (line.contains("[DEPOSIT]")) {

                mat = Material.EMERALD;
                name = "§a✦ Dépôt";
            }

            else if (line.contains("[WITHDRAW]")) {

                mat = Material.REDSTONE;
                name = "§c✦ Retrait";
            }

            else if (line.contains("[TRANSFER]")) {

                mat = Material.WRITABLE_BOOK;
                name = "§e✦ Virement";
            }

            else if (line.contains("[MARKET_BUY]")) {

                mat = Material.CHEST_MINECART;
                name = "§6✦ Achat Marché";
            }

            else if (line.contains("[MARKET_SELL]")) {

                mat = Material.GOLD_INGOT;
                name = "§b✦ Vente Marché";
            }

            SafeGUI.safeSet(
                    inv,
                    slots[i],
                    SafeGUI.item(
                            mat,
                            name,
                            "§7" + line,
                            "",
                            "§8▶ Archive bancaire"
                    )
            );
        }

        if (page > 1) {

            SafeGUI.safeSet(inv, 27,
                    SafeGUI.item(
                            Material.SPECTRAL_ARROW,
                            "§e✦ Page précédente",
                            "§7Revenir à la page précédente.",
                            "",
                            "§e▶ Ouvrir"
                    )
            );
        }

        if (history.size() > page * PAGE_SIZE) {

            SafeGUI.safeSet(inv, 35,
                    SafeGUI.item(
                            Material.SPECTRAL_ARROW,
                            "§e✦ Page suivante",
                            "§7Afficher plus de transactions.",
                            "",
                            "§e▶ Ouvrir"
                    )
            );
        }

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
                "transaction_history",
                inv
        );
    }
}