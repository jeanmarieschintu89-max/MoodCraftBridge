
package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TransferTypeGUI;

import fr.moodcraft.bridge.manager.InputManager;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class IbanHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        switch (slot) {

            //
            // 🏦 SAISIE IBAN
            //

            case 13 -> {

                p.closeInventory();

                InputManager.wait(
                        p,
                        "iban_input"
                );

                header(p);

                p.sendMessage("§a✔ §fSaisie IBAN.");
                p.sendMessage("");
                p.sendMessage("§fÉcris l'IBAN dans le chat.");
                p.sendMessage("");
                p.sendMessage("§8• §7Exemple: §eMC-1234-ABCD");
                p.sendMessage("§8• §7Le joueur peut être hors ligne");
                p.sendMessage("§8• §7Le montant sera demandé ensuite");
                p.sendMessage("");
                p.sendMessage("§7Tape §cannuler §7pour quitter.");

                footer(p);

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        1.1f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.2f
                );
            }

            //
            // ↩ RETOUR
            //

            case 31 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                TransferTypeGUI.open(p);
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