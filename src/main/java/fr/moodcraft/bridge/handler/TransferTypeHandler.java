package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.IbanGUI;
import fr.moodcraft.bridge.gui.TargetPlayerGUI;

import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TransferTypeHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                250
        )) return;

        switch (slot) {

            case 11 -> {

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.PLAYER_TRANSFER
                );

                p.sendMessage("");
                p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                p.sendMessage("§a✔ Virement joueur sélectionné");
                p.sendMessage("§7Choisis un joueur connecté.");
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.25f,
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        1.4f
                );

                TargetPlayerGUI.open(p);
            }

            case 15 -> {

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.IBAN_TRANSFER
                );

                p.sendMessage("");
                p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                p.sendMessage("§b✔ Virement IBAN sélectionné");
                p.sendMessage("§7Entre un IBAN MoodCraft.");
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_BEACON_ACTIVATE,
                        1.1f,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.3f
                );

                IbanGUI.open(p);
            }

            case 22 -> {

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