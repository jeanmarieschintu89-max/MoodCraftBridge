package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.MainMenuGUI;

import fr.moodcraft.bridge.manager.ReputationManager;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class ProfileHandler implements GUIHandler {

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

        //
        // 📊 RÉPUTATION
        //

        int rep =
                ReputationManager.get(
                        p.getUniqueId().toString()
                );

        //
        // 🏆 RANG
        //

        String rank =
                ReputationManager.getRank(rep);

        switch (slot) {

            //
            // 👤 PROFIL CENTRAL
            //

            case 13 -> {

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fProfil MoodCraft"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Joueur:"
                );

                p.sendMessage(
                        "§e" + p.getName()
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Réputation:"
                );

                p.sendMessage(
                        "§a" + rep
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Rang économique:"
                );

                p.sendMessage(
                        rank
                );

                p.sendMessage("");

                //
                // 🌟 BONUS ÉLITE
                //

                if (rep >= 120) {

                    p.sendMessage(
                            "§6✦ Statut Élite détecté"
                    );

                    p.sendMessage(
                            "§7Accès économique avancé."
                    );

                    p.sendMessage("");
                }

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                //
                // 🔊 SOUND
                //

                p.playSound(

                        p.getLocation(),

                        Sound.BLOCK_AMETHYST_BLOCK_RESONATE,

                        1f,

                        1.1f
                );

                //
                // 🎬 TITLE
                //

                p.sendTitle(

                        "§6Profil",

                        rank,

                        5,

                        30,

                        10
                );
            }

            //
            // 🔙 RETOUR
            //

            case 26,
                 31 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.8f
                );

                p.sendMessage(
                        "§8✦ §7Retour au menu principal..."
                );

                MainMenuGUI.open(p);
            }
        }
    }
}