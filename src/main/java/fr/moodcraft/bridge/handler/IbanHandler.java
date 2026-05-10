package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TransferTypeGUI;

import fr.moodcraft.bridge.manager.InputManager;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class IbanHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        switch (slot) {

            case 13 -> {

                p.closeInventory();

                InputManager.wait(
                        p,
                        "iban_input"
                );

                p.sendMessage("");
                p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                p.sendMessage("§b✔ Saisie IBAN");
                p.sendMessage("§7Entre l'IBAN dans le chat.");
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        1.1f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.2f
                );
            }

            case 31 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                TransferTypeGUI.open(p);
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