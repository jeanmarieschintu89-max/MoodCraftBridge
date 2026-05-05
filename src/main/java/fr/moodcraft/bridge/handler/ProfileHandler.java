package fr.moodcraft.bridge;

import fr.moodcraft.bridge.handler.GUIHandler;
import fr.moodcraft.bridge.gui.MainMenuGUI; // ✅ fallback propre

import org.bukkit.entity.Player;

public class ProfileHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        // 🔙 retour classement
        if (slot == 26) {
            MainMenuGUI.open(p); // ✅ remplace TopRepGUI
        }
    }
}