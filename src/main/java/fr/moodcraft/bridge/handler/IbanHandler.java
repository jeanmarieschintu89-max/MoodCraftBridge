package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.util.InputManager;
import fr.moodcraft.bridge.handler.GUIHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class IbanHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            case 13 -> {
                p.closeInventory();

                InputManager.wait(p, "iban_input");

                p.sendMessage("§e✏ Entre l'IBAN dans le chat");
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }

            case 22 -> {
                // 👉 à adapter selon ton menu
                p.closeInventory();
            }
        }
    }
}