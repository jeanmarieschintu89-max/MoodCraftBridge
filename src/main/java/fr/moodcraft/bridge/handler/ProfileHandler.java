package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.MainMenuGUI;

import fr.moodcraft.bridge.manager.ReputationManager;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class ProfileHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                250
        )) return;

        int rep =
                ReputationManager.get(
                        p.getUniqueId().toString()
                );

        String rank =
                ReputationManager.getRank(rep);

        switch (slot) {

            case 13 -> {

                p.sendMessage("");
                p.sendMessage("§8----- §6Profil MoodCraft §8-----");
                p.sendMessage("§7Joueur: §e" + p.getName());
                p.sendMessage("§7Réputation: §a" + rep);
                p.sendMessage("§7Rang: " + rank);

                if (rep >= 120) {

                    p.sendMessage("§6✦ Statut Élite");
                }

                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_RESONATE,
                        1.15f,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.35f
                );

                p.sendTitle(
                        "§6Profil",
                        rank,
                        5,
                        30,
                        10
                );
            }

            case 21 -> {

                p.sendMessage("");
                p.sendMessage("§8----- §6Économie §8-----");
                p.sendMessage("§7Résumé bancaire disponible dans la banque.");
                p.sendMessage("§7Réputation: §a" + rep);
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.2f,
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        1.4f
                );
            }

            case 23 -> {

                p.sendMessage("");
                p.sendMessage("§8----- §6Réputation §8-----");
                p.sendMessage("§7Score: §a" + rep);
                p.sendMessage("§7Rang: " + rank);
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.25f,
                        Sound.UI_BUTTON_CLICK,
                        1.5f
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

                MainMenuGUI.open(p);
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