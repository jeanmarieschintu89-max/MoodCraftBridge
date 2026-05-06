package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransactionHistoryGUI;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TransactionHistoryHandler implements GUIHandler {

    @Override
    public void onClick(Player p,
                        int slot) {

        String title =
                p.getOpenInventory()
                        .getTitle();

        //
        // 📄 PAGE ACTUELLE
        //

        int page = 1;

        try {

            if (title.contains("Page ")) {

                String raw =
                        title.substring(
                                title.indexOf("Page ") + 5
                        );

                page =
                        Integer.parseInt(
                                raw.replaceAll("[^0-9]", "")
                        );
            }

        } catch (Exception ignored) {}

        //
        // ◀ PAGE PRÉCÉDENTE
        //

        if (slot == 27) {

            if (page > 1) {

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        0.9f
                );

                TransactionHistoryGUI.open(
                        p,
                        page - 1
                );
            }

            return;
        }

        //
        // ▶ PAGE SUIVANTE
        //

        if (slot == 35) {

            p.playSound(
                    p.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    1f,
                    1.1f
            );

            TransactionHistoryGUI.open(
                    p,
                    page + 1
            );

            return;
        }

        //
        // 🔙 RETOUR
        //

        if (slot == 31) {

            p.playSound(
                    p.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    1f,
                    0.8f
            );

            BankGUI.open(p);
        }
    }
}