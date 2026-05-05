package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.*;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.entity.Player;

public class BankHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            // 📥 DEPOT
            case 10 -> {
                TransferBuilder.setAction(p, TransferBuilder.Action.DEPOSIT);
                DepositGUI.open(p); // ✅ FIX
            }

            // 📤 RETRAIT
            case 12 -> {
                TransferBuilder.setAction(p, TransferBuilder.Action.WITHDRAW);
                WithdrawGUI.open(p); // ✅ mieux aussi
            }

            // 💸 VIREMENT
            case 14 -> {
                TransferBuilder.clear(p); // 🔥 important
                TransferTypeGUI.open(p);
            }

            // 🏦 IBAN
            case 16 -> {
                IbanGUI.open(p);
            }

            // 🔙 RETOUR
            case 22 -> {
                MainMenuGUI.open(p);
            }
        }
    }
}