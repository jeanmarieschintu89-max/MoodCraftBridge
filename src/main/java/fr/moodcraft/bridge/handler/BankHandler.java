package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.*;
import fr.moodcraft.bridge.GUIHandler; ✅
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.entity.Player;

public class BankHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            // 📥 DEPOT
            case 10 -> {
                TransferBuilder.setAction(p, TransferBuilder.Action.DEPOSIT);
                TransferAmountGUI.open(p);
            }

            // 📤 RETRAIT
            case 12 -> {
                TransferBuilder.setAction(p, TransferBuilder.Action.WITHDRAW);
                TransferAmountGUI.open(p);
            }

            // 💸 VIREMENT
            case 14 -> {
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