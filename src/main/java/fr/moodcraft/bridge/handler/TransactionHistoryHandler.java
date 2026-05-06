package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransactionHistoryGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TransactionHistoryHandler implements GUIHandler {

    @Override
    public void onClick(Player p,
                        int slot) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                200
        )) return;

        //
        // 📄 TITRE
        //

        String title =
                p.getOpenInventory()
                        .getTitle();

        //
        // 📑 PAGE
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

            if (page <= 1) {

                p.playSound(

                        p.getLocation(),

                        Sound.ENTITY_VILLAGER_NO,

                        1f,

                        1f
                );

                p.sendMessage(
                        "§8✦ §7Tu es déjà sur la première page."
                );

                return;
            }

            p.playSound(

                    p.getLocation(),

                    Sound.UI_BUTTON_CLICK,

                    1f,

                    0.9f
            );

            p.sendMessage(
                    "§8✦ §7Chargement de l'historique..."
            );

            TransactionHistoryGUI.open(

                    p,

                    page - 1
            );

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

            p.sendMessage(
                    "§8✦ §7Synchronisation des transactions..."
            );

            TransactionHistoryGUI.open(

                    p,

                    page + 1
            );

            return;
        }

        //
        // 📜 TRANSACTION
        //

        if (slot >= 10
                && slot <= 25
                && slot != 17
                && slot != 18) {

            p.playSound(

                    p.getLocation(),

                    Sound.BLOCK_NOTE_BLOCK_CHIME,

                    1f,

                    1.15f
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§6✦ §fArchive bancaire MoodCraft"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Cette transaction est"
            );

            p.sendMessage(
                    "§7stockée dans l'historique."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8• Dépôts"
            );

            p.sendMessage(
                    "§8• Retraits"
            );

            p.sendMessage(
                    "§8• Virements"
            );

            p.sendMessage(
                    "§8• Marché économique"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            return;
        }

        //
        // 🔙 RETOUR
        //

        if (slot == 31
                || slot == 30
                || slot == 32) {

            p.playSound(

                    p.getLocation(),

                    Sound.UI_BUTTON_CLICK,

                    1f,

                    0.8f
            );

            p.sendMessage(
                    "§8✦ §7Retour au centre bancaire..."
            );

            BankGUI.open(p);
        }
    }
}