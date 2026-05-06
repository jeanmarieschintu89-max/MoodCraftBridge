package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.*;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BankHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            //
            // 📥 DEPOT
            //

            case 10 -> {

                // 🔥 reset sécurité
                TransferBuilder.clear(p);

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.DEPOSIT
                );

                DepositGUI.open(p);
            }

            //
            // 📤 RETRAIT
            //

            case 12 -> {

                // 🔥 reset sécurité
                TransferBuilder.clear(p);

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.WITHDRAW
                );

                WithdrawGUI.open(p);
            }

            //
            // 💸 VIREMENT
            //

            case 14 -> {

                // 🔥 reset ancien transfert
                TransferBuilder.clear(p);

                TransferTypeGUI.open(p);
            }

            //
            // 🏦 VOIR MON IBAN
            //

            case 16 -> {

                p.closeInventory();

                p.sendMessage("§8§m-----------------------------");
                p.sendMessage("§6🏦 Ton IBAN MoodCraft");
                p.sendMessage("");

                p.sendMessage(
                        "§e"
                                + fr.moodcraft.bridge.manager.IbanManager.get(p)
                );

                p.sendMessage("");
                p.sendMessage("§7Utilisable pour les virements.");
                p.sendMessage("§8§m-----------------------------");

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_PLING,
                        1f,
                        1.2f
                );
            }

            //
            // 🔙 RETOUR
            //

            case 22 -> {

                MainMenuGUI.open(p);
            }
        }
    }
}