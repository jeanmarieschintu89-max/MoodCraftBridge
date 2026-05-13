package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.IbanGUI;
import fr.moodcraft.bridge.gui.TargetPlayerGUI;

import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TransferTypeHandler implements GUIHandler {

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

            //
            // 👤 JOUEUR CONNECTÉ
            //

            case 11 -> {

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.PLAYER_TRANSFER
                );

                header(p);

                p.sendMessage("§a✔ §fVirement vers joueur.");
                p.sendMessage("");
                p.sendMessage("§7Choisis un joueur connecté.");
                p.sendMessage("§8• §7Le montant sera demandé ensuite");
                p.sendMessage("§8• §7Une confirmation sera affichée");

                footer(p);

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.25f,
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        1.4f
                );

                TargetPlayerGUI.open(p);
            }

            //
            // 🏷 IBAN
            //

            case 15 -> {

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.IBAN_TRANSFER
                );

                header(p);

                p.sendMessage("§a✔ §fVirement par IBAN.");
                p.sendMessage("");
                p.sendMessage("§7Écris l'IBAN du joueur.");
                p.sendMessage("§8• §7Fonctionne même si le joueur est hors ligne");
                p.sendMessage("§8• §7Le montant sera demandé après");

                footer(p);

                premiumClick(
                        p,
                        Sound.BLOCK_BEACON_ACTIVATE,
                        1.1f,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.3f
                );

                IbanGUI.open(p);
            }

            //
            // ↩ RETOUR
            //

            case 22 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                BankGUI.open(p);
            }
        }
    }

    //
    // 🎨 HEADER
    //

    private void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----");
        p.sendMessage("");
    }

    //
    // 🎨 FOOTER
    //

    private void footer(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8-----------------------------");
        p.sendMessage("");
    }

    //
    // 🔊 SOUND
    //

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