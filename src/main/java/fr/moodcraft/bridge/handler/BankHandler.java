package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.IbanManager;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.MainMenuGUI;
import fr.moodcraft.bridge.gui.TransactionHistoryGUI;
import fr.moodcraft.bridge.gui.TransferTypeGUI;

import fr.moodcraft.bridge.listener.BankChatInputListener;

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

            //
            // 💰 DEPOT PAR CHAT
            //

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

                BankChatInputListener.startDeposit(p);
            }

            //
            // 💸 RETRAIT PAR CHAT
            //

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

                BankChatInputListener.startWithdraw(p);
            }

            //
            // 🔁 VIREMENT
            //

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

            //
            // 🏷 IBAN
            //

            case 16 -> {

                p.closeInventory();

                String iban =
                        IbanManager.get(
                                p.getUniqueId()
                        );

                p.sendMessage("");
                p.sendMessage(
                        "§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----"
                );
                p.sendMessage(
                        "§fIdentité bancaire personnelle."
                );
                p.sendMessage("");
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

            //
            // 📖 HISTORIQUE
            //

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

            //
            // 📊 ACTIVITE
            //

            case 23 -> {

                p.closeInventory();

                p.sendMessage("");
                p.sendMessage(
                        "§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----"
                );
                p.sendMessage(
                        "§eActivité bancaire en préparation."
                );
                p.sendMessage("");
                p.sendMessage(
                        "§8• §7Volume personnel"
                );
                p.sendMessage(
                        "§8• §7Flux bancaires"
                );
                p.sendMessage(
                        "§8• §7Statistiques économiques"
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

            //
            // ↩ RETOUR
            //

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