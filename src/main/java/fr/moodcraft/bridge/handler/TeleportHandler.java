package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.MainMenuGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TeleportHandler implements GUIHandler {

    @Override
    public void onClick(Player p,
                        int slot) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        switch (slot) {

            //
            // 🌲 RESSOURCES
            //

            case 10 -> {

                teleport(

                        p,

                        "warp ressources",

                        "§a✦ Zone Ressources",

                        "§7Connexion aux zones minières...",

                        Sound.BLOCK_ROOTED_DIRT_BREAK,

                        0.9f
                );
            }

            //
            // 🛒 ADMIN SHOP
            //

            case 12 -> {

                teleport(

                        p,

                        "warp shop",

                        "§6✦ Centre Commercial",

                        "§7Synchronisation des boutiques...",

                        Sound.BLOCK_NOTE_BLOCK_BELL,

                        1f
                );
            }

            //
            // 🎮 MINI-JEUX
            //

            case 14 -> {

                teleport(

                        p,

                        "warp mini-jeux",

                        "§d✦ Arcade MoodCraft",

                        "§7Chargement des activités...",

                        Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM,

                        1.15f
                );
            }

            //
            // 🌍 RTP
            //

            case 16 -> {

                teleport(

                        p,

                        "tpr",

                        "§5✦ Exploration Libre",

                        "§7Recherche d'une zone sauvage...",

                        Sound.ITEM_CHORUS_FRUIT_TELEPORT,

                        1f
                );
            }

            //
            // 🏠 SPAWN
            //

            case 20 -> {

                teleport(

                        p,

                        "spawn",

                        "§e✦ Spawn Central",

                        "§7Retour au centre principal...",

                        Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,

                        1f
                );
            }

            //
            // 🏙️ VILLE
            //

            case 24 -> {

                teleport(

                        p,

                        "t spawn",

                        "§a✦ Ville",

                        "§7Connexion au territoire urbain...",

                        Sound.BLOCK_CHAIN_PLACE,

                        0.95f
                );
            }

            //
            // 🔙 RETOUR
            //

            case 31 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.8f
                );

                MainMenuGUI.open(p);
            }
        }
    }

    //
    // ✨ TP WRAPPER
    //

    private void teleport(Player p,
                          String command,
                          String title,
                          String subtitle,
                          Sound sound,
                          float pitch) {

        //
        // 🔊 SOUND
        //

        p.playSound(

                p.getLocation(),

                sound,

                1f,

                pitch
        );

        //
        // 💬 MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                title
        );

        p.sendMessage("");

        p.sendMessage(
                subtitle
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        //
        // 🎬 TITLE
        //

        p.sendTitle(

                title,

                "§fTéléportation en cours...",

                5,

                25,

                10
        );

        //
        // 🚀 COMMAND
        //

        p.closeInventory();

        p.performCommand(command);
    }
}