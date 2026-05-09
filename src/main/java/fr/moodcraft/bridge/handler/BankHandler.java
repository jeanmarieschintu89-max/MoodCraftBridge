package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.IbanManager;

import fr.moodcraft.bridge.gui.*;

import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class BankHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        switch (slot) {

            case 10 -> {

                TransferBuilder.clear(p);

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.DEPOSIT
                );

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.25f,
                        Sound.BLOCK_CHEST_OPEN,
                        1.4f
                );

                DepositGUI.open(p);
            }

            case 12 -> {

                TransferBuilder.clear(p);

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.WITHDRAW
                );

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_BASS,
                        1.1f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                WithdrawGUI.open(p);
            }

            case 14 -> {

                TransferBuilder.clear(p);

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_PLING,
                        1.35f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.2f
                );

                TransferTypeGUI.open(p);
            }

            case 16 -> {

                p.closeInventory();

                String iban =
                        IbanManager.get(
                                p.getUniqueId()
                        );

                p.sendMessage("");
                p.sendMessage(
                        "§8----- §6Banque MoodCraft §8-----"
                );
                p.sendMessage(
                        "§7IBAN: §e" + iban
                );
                p.sendMessage(
                        "§7Utilisable pour les virements."
                );
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_PLING,
                        1.4f,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.2f
                );
            }

            case 21 -> {

                premiumClick(
                        p,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.2f,
                        Sound.UI_BUTTON_CLICK,
                        1.4f
                );

                TransactionHistoryGUI.open(
                        p,
                        1
                );
            }

            case 23 -> {

                p.closeInventory();

                p.sendMessage("");
                p.sendMessage(
                        "§8----- §6Activité économique §8-----"
                );
                p.sendMessage(
                        "§7Les statistiques arrivent bientôt."
                );
                p.sendMessage(
                        "§8• §7Profit"
                );
                p.sendMessage(
                        "§8• §7Volume"
                );
                p.sendMessage(
                        "§8• §7Classement"
                );
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.2f,
                        Sound.BLOCK_BEACON_AMBIENT,
                        1.4f
                );
            }

            case 31 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.3f
                );

                MainMenuGUI.open(p);
            }
        }
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