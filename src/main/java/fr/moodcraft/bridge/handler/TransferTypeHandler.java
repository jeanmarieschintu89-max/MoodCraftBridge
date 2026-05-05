package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TargetPlayerGUI;
import fr.moodcraft.bridge.gui.IbanGUI;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.entity.Player;

public class TransferTypeHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            // 👤 joueur
            case 2 -> {
                TransferBuilder.setAction(p, TransferBuilder.Action.PLAYER_TRANSFER);
                TargetPlayerGUI.open(p);
            }

            // 🏦 IBAN
            case 6 -> {
                TransferBuilder.setAction(p, TransferBuilder.Action.IBAN_TRANSFER);
                IbanGUI.open(p);
            }

            // 🔙 retour
            case 8 -> {
                BankGUI.open(p);
            }
        }
    }
}