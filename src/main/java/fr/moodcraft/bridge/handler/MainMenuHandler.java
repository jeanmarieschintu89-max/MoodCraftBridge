package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.gui.*;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MainMenuHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);

        switch (slot) {

            // 👤 PROFIL
            case 4 -> openNext(p, () ->
                    ProfileGUI.open(p, p.getUniqueId())
            );

            // 💰 BANQUE
            case 10 -> openNext(p, () ->
                    BankGUI.open(p)
            );

            // 💼 BOURSE
            case 14 -> openNext(p, () ->
                    PriceGUI.open(p)
            );

            // 🧭 TELEPORT
            case 16 -> openNext(p, () ->
                    TeleportGUI.open(p)
            );

            // 🗺️ VILLE
            case 19 -> openNext(p, () ->
                    p.performCommand("townmenu")
            );

            // ⛏ MÉTIERS
            case 21 -> openNext(p, () ->
                    p.performCommand("jobs join")
            );

            // ❌ FERMER
            case 26 -> p.closeInventory();
        }
    }

    private void openNext(Player p, Runnable action) {

        p.closeInventory();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), action, 1L);
    }
}