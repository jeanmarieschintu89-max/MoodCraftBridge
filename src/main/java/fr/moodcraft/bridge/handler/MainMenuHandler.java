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
    public void onClick(Player p,
                        int slot) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                250
        )) return;

        switch (slot) {

            //
            // 👤 PROFIL
            //

            case 4 -> {

                feedback(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.15f
                );

                openNext(

                        p,

                        () -> ProfileGUI.open(
                                p,
                                p.getUniqueId()
                        )
                );
            }

            //
            // 🏦 BANQUE
            //

            case 10 -> {

                feedback(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_BELL,
                        1f
                );

                p.sendMessage(
                        "§8✦ §7Connexion au réseau bancaire..."
                );

                openNext(
                        p,
                        () -> BankGUI.open(p)
                );
            }

            //
            // 📊 BOURSE
            //

            case 14 -> {

                feedback(
                        p,
                        Sound.BLOCK_BEACON_AMBIENT,
                        0.9f
                );

                p.sendMessage(
                        "§8✦ §7Synchronisation du marché..."
                );

                openNext(
                        p,
                        () -> PriceGUI.open(p)
                );
            }

            //
            // 🧭 TÉLÉPORTATION
            //

            case 16 -> {

                feedback(
                        p,
                        Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                        1f
                );

                openNext(
                        p,
                        () -> TeleportGUI.open(p)
                );
            }

            //
            // 🏙️ VILLE
            //

            case 19 -> {

                feedback(
                        p,
                        Sound.BLOCK_CHAIN_PLACE,
                        0.95f
                );

                p.sendMessage(
                        "§8✦ §7Ouverture du système territorial..."
                );

                openNext(
                        p,
                        () -> p.performCommand("townmenu")
                );
            }

            //
            // ⛏️ MÉTIERS
            //

            case 21 -> {

                feedback(
                        p,
                        Sound.ENTITY_VILLAGER_WORK_TOOLSMITH,
                        1f
                );

                p.sendMessage(
                        "§8✦ §7Chargement des métiers..."
                );

                openNext(
                        p,
                        () -> p.performCommand("jobs join")
                );
            }

            //
            // ❌ FERMER
            //

            case 26 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.7f
                );

                p.closeInventory();

                p.sendMessage(
                        "§8✦ §7Interface MoodCraft fermée."
                );
            }
        }
    }

    //
    // 🔊 FEEDBACK
    //

    private void feedback(Player p,
                          Sound sound,
                          float pitch) {

        p.playSound(

                p.getLocation(),

                sound,

                1f,

                pitch
        );
    }

    //
    // 🔄 OPEN SAFE
    //

    private void openNext(Player p,
                          Runnable action) {

        p.closeInventory();

        Bukkit.getScheduler().runTaskLater(

                Main.getInstance(),

                action,

                1L
        );
    }
}