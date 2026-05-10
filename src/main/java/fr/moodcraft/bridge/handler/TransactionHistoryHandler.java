package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransactionHistoryGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TransactionHistoryHandler implements GUIHandler {

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

        if (slot == 27) {

            if (page <= 1) {

                fail(p);

                p.sendMessage(
                        "§8✦ §7Première page déjà affichée."
                );

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
            p.sendMessage("§8----- §6Archive bancaire §8-----");
            p.sendMessage("§7Transaction enregistrée dans l'historique.");
            p.sendMessage("");

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
        }
    }

    private int getPage(
            String title
    ) {

        try {

            if (title.contains("Page ")) {

                String raw =
                        title.substring(
                                title.indexOf("Page ") + 5
                        );

                return Integer.parseInt(
                        raw.replaceAll("[^0-9]", "")
                );
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