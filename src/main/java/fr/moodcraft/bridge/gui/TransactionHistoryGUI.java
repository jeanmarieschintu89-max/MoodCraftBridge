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
                        GuiTitle.of("Historique §8• §ePage " + page)
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
                                "§8• §7Mouvements : §e" + history.size(),
                                "§8• §7Page : §e" + page + "§8/§e" + maxPage,
                                "",
                                "§8• §7Dépôts",
                                "§8• §7Retraits",
                                "§8• §7Virements",
                                "§8• §7Marché",
                                "",
                                "§e➜ §fClique une archive"
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
                            "§6✦ §fAucun mouvement §6✦",
                            "§8• §7Historique vide",
                            "§8• §7Dépôts, retraits",
                            "§8• §7et virements ici"
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
                        "§6✦ §fMouvement §6✦";

                if (line.contains("[DEPOSIT]")) {

                    mat = Material.EMERALD;
                    name = "§6✦ §aDépôt §6✦";

                } else if (line.contains("[WITHDRAW]")) {

                    mat = Material.REDSTONE;
                    name = "§6✦ §cRetrait §6✦";

                } else if (line.contains("[TRANSFER]")) {

                    mat = Material.WRITABLE_BOOK;
                    name = "§6✦ §eVirement §6✦";

                } else if (line.contains("[MARKET_BUY]")) {

                    mat = Material.CHEST_MINECART;
                    name = "§6✦ §fAchat marché §6✦";

                } else if (line.contains("[MARKET_SELL]")) {

                    mat = Material.GOLD_INGOT;
                    name = "§6✦ §bVente marché §6✦";
                }

                SafeGUI.safeSet(
                        inv,
                        slots[i],
                        SafeGUI.item(
                                mat,
                                name,
                                "§8• §7" + crop(line),
                                "§8• §7Mouvement enregistré",
                                "§8• §7Archive bancaire"
                        )
                );
            }
        }

        if (page > 1) {

            SafeGUI.safeSet(inv, 27,
                    SafeGUI.item(
                            Material.SPECTRAL_ARROW,
                            "§6✦ §fPage précédente §6✦",
                            "§8• §7Page §e" + (page - 1),
                            "",
                            "§e➜ §fOuvrir"
                    )
            );

        } else {

            SafeGUI.safeSet(inv, 27,
                    SafeGUI.item(
                            Material.GRAY_DYE,
                            "§6✦ §fPremière page §6✦",
                            "§8• §7Aucune page avant"
                    )
            );
        }

        if (page < maxPage) {

            SafeGUI.safeSet(inv, 35,
                    SafeGUI.item(
                            Material.SPECTRAL_ARROW,
                            "§6✦ §fPage suivante §6✦",
                            "§8• §7Page §e" + (page + 1),
                            "",
                            "§e➜ §fOuvrir"
                    )
            );

        } else {

            SafeGUI.safeSet(inv, 35,
                    SafeGUI.item(
                            Material.GRAY_DYE,
                            "§6✦ §fDernière page §6✦",
                            "§8• §7Aucune page après"
                    )
            );
        }

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.BARRIER,
                        "§6✦ §fRetour §6✦",
                        "§8• §7Retour à la banque",
                        "",
                        "§c✖ §fRevenir"
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

        if (clean.length() <= 42) {
            return clean;
        }

        return clean.substring(0, 39) + "...";
    }
}