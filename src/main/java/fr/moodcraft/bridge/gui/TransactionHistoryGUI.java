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

        List<String> history =
                TransactionManager.getHistory(
                        p.getUniqueId()
                );

        int maxPage =
                Math.max(
                        1,
                        (int) Math.ceil(
                                history.size() / (double) PAGE_SIZE
                        )
                );

        if (page < 1) {
            page = 1;
        }

        if (page > maxPage) {
            page = maxPage;
        }

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §6Historique §8• §ePage " + page
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
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
                                "§6✦ §fHistorique bancaire §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Transactions: §e"
                                        + history.size(),
                                "§7Page: §e"
                                        + page
                                        + "§8/§e"
                                        + maxPage,
                                "",
                                "§8• §7Dépôts",
                                "§8• §7Retraits",
                                "§8• §7Virements",
                                "§8• §7Marché",
                                "",
                                "§eSélectionne une archive"
                        )
                )
        );

        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 32, 33, 34
        };

        if (pageData.isEmpty()) {

            SafeGUI.safeSet(inv, 22,
                    SafeGUI.item(
                            Material.PAPER,
                            "§7Aucune transaction",
                            "§7Votre historique bancaire",
                            "§7est vide pour le moment."
                    )
            );

        } else {

            for (int i = 0;
                 i < pageData.size()
                         && i < slots.length;
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

                } else if (line.contains("[WITHDRAW]")) {

                    mat = Material.REDSTONE;
                    name = "§c✦ Retrait";

                } else if (line.contains("[TRANSFER]")) {

                    mat = Material.WRITABLE_BOOK;
                    name = "§e✦ Virement";

                } else if (line.contains("[MARKET_BUY]")) {

                    mat = Material.CHEST_MINECART;
                    name = "§6✦ Achat Marché";

                } else if (line.contains("[MARKET_SELL]")) {

                    mat = Material.GOLD_INGOT;
                    name = "§b✦ Vente Marché";
                }

                SafeGUI.safeSet(
                        inv,
                        slots[i],
                        SafeGUI.item(
                                mat,
                                name,
                                "§7" + crop(line),
                                "",
                                "§8• §7Archive bancaire"
                        )
                );
            }
        }

        if (page > 1) {

            SafeGUI.safeSet(inv, 27,
                    SafeGUI.item(
                            Material.SPECTRAL_ARROW,
                            "§e✦ Page précédente",
                            "§7Page: §e" + (page - 1),
                            "",
                            "§e▶ Ouvrir"
                    )
            );

        } else {

            SafeGUI.safeSet(inv, 27,
                    SafeGUI.item(
                            Material.GRAY_DYE,
                            "§8✦ Première page",
                            "§7Aucune page précédente."
                    )
            );
        }

        if (page < maxPage) {

            SafeGUI.safeSet(inv, 35,
                    SafeGUI.item(
                            Material.SPECTRAL_ARROW,
                            "§e✦ Page suivante",
                            "§7Page: §e" + (page + 1),
                            "",
                            "§e▶ Ouvrir"
                    )
            );

        } else {

            SafeGUI.safeSet(inv, 35,
                    SafeGUI.item(
                            Material.GRAY_DYE,
                            "§8✦ Dernière page",
                            "§7Aucune page suivante."
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

    private static String crop(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return "Aucune donnée.";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= 48) {
            return clean;
        }

        return clean.substring(0, 45) + "...";
    }
}