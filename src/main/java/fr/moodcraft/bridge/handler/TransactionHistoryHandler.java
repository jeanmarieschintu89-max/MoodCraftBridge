package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransactionHistoryGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TransactionHistoryHandler implements GUIHandler {

    private static final int PAGE_SIZE =
            20;

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                200
        )) return;

        String title =
                p.getOpenInventory()
                        .getTitle();

        int page =
                getPage(title);

        int total =
                TransactionManager.getHistory(
                        p.getUniqueId()
                ).size();

        int maxPage =
                Math.max(
                        1,
                        (int) Math.ceil(
                                total / (double) PAGE_SIZE
                        )
                );

        if (slot == 27) {

            if (page <= 1) {

                fail(p);

                p.sendMessage("");
                p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----");
                p.sendMessage("");
                p.sendMessage("§7Première page déjà affichée.");
                p.sendMessage("");
                p.sendMessage("§8-----------------------------");
                p.sendMessage("");

                return;
            }

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    0.9f,
                    Sound.ITEM_BOOK_PAGE_TURN,
                    1.1f
            );

            TransactionHistoryGUI.open(
                    p,
                    page - 1
            );

            return;
        }

        if (slot == 35) {

            if (page >= maxPage) {

                fail(p);

                p.sendMessage("");
                p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----");
                p.sendMessage("");
                p.sendMessage("§7Dernière page déjà affichée.");
                p.sendMessage("");
                p.sendMessage("§8-----------------------------");
                p.sendMessage("");

                return;
            }

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    1.1f,
                    Sound.ITEM_BOOK_PAGE_TURN,
                    1.25f
            );

            TransactionHistoryGUI.open(
                    p,
                    page + 1
            );

            return;
        }

        if (slot == 31) {

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    0.8f,
                    Sound.BLOCK_CHEST_CLOSE,
                    1.2f
            );

            BankGUI.open(p);

            return;
        }

        boolean transactionSlot =
                (slot >= 10 && slot <= 16)
                        || (slot >= 19 && slot <= 25)
                        || (slot >= 28 && slot <= 30)
                        || (slot >= 32 && slot <= 34);

        if (transactionSlot) {

            premiumClick(
                    p,
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    1.2f,
                    Sound.ITEM_BOOK_PAGE_TURN,
                    1.35f
            );

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§7Transaction enregistrée dans l'historique.");
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");
        }
    }

    private int getPage(
            String title
    ) {

        try {

            if (title == null) {
                return 1;
            }

            if (title.contains("Page ")) {

                String raw =
                        title.substring(
                                title.indexOf("Page ") + 5
                        );

                String number =
                        raw.replaceAll("[^0-9]", "");

                if (!number.isBlank()) {

                    return Integer.parseInt(number);
                }
            }

        } catch (Exception ignored) {}

        return 1;
    }

    private void fail(Player p) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    private void premiumClick(
            Player p,
            Sound main,
            float mainPitch,
            Sound second,
            float secondPitch
    ) {

        p.playSound(
                p.getLocation(),
                main,
                0.75f,
                mainPitch
        );

        p.playSound(
                p.getLocation(),
                second,
                0.35f,
                secondPitch
        );
    }
}