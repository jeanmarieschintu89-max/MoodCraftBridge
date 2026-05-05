package fr.moodcraft.bridge;

import org.bukkit.entity.Player;

public class ProfileHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        // 🔙 retour classement
        if (slot == 26) {
            TopRepGUI.open(p);
        }
    }
}