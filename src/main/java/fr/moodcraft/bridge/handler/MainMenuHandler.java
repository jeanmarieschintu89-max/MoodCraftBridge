package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.PriceGUI;
import fr.moodcraft.bridge.gui.ProfileGUI;
import fr.moodcraft.bridge.gui.TeleportGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class MainMenuHandler implements GUIHandler {

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

            case 4 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.25f,
                        Sound.UI_BUTTON_CLICK,
                        1.6f
                );

                openNext(
                        p,
                        () -> ProfileGUI.open(
                                p,
                                p.getUniqueId()
                        )
                );
            }

            case 10 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.2f,
                        Sound.BLOCK_ENDER_CHEST_OPEN,
                        1.4f
                );

                p.sendMessage(
                        "§8✦ §6Banque §8• §7Ouverture..."
                );

                openNext(
                        p,
                        () -> BankGUI.open(p)
                );
            }

            case 12 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.35f,
                        Sound.BLOCK_BEACON_AMBIENT,
                        1.2f
                );

                p.sendMessage(
                        "§8✦ §eMarché §8• §7Actualisation..."
                );

                openNext(
                        p,
                        () -> PriceGUI.open(p)
                );
            }

            case 14 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_BEACON_ACTIVATE,
                        1.15f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.2f
                );

                p.sendMessage(
                        "§8✦ §bProjets §8• §7Ouverture..."
                );

                openNext(
                        p,
                        () -> p.performCommand("projet")
                );
            }

            case 16 -> {

                premiumClick(
                        p,
                        Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                        1.25f,
                        Sound.BLOCK_PORTAL_AMBIENT,
                        1.6f
                );

                p.sendMessage(
                        "§8✦ §bTéléportation §8• §7Choisis ta destination."
                );

                openNext(
                        p,
                        () -> TeleportGUI.open(p)
                );
            }

            case 21 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_CHAIN_PLACE,
                        1.1f,
                        Sound.BLOCK_STONE_BUTTON_CLICK_ON,
                        1.5f
                );

                p.sendMessage(
                        "§8✦ §aVille §8• §7Ouverture..."
                );

                openNext(
                        p,
                        () -> p.performCommand("townmenu")
                );
            }

            case 23 -> {

                premiumClick(
                        p,
                        Sound.ENTITY_VILLAGER_WORK_TOOLSMITH,
                        1.05f,
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        1.4f
                );

                p.sendMessage(
                        "§8✦ §dMétiers §8• §7Chargement..."
                );

                openNext(
                        p,
                        () -> p.performCommand("jobs join")
                );
            }

            case 31 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.75f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                p.closeInventory();

                p.sendMessage(
                        "§8✦ §7Menu fermé."
                );
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

    private void openNext(
            Player p,
            Runnable action
    ) {

        p.closeInventory();

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                action,
                1L
        );
    }
}