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
            // 👤 VIREMENT JOUEUR
            //

            case 2 -> {

                TransferBuilder.setAction(

                        p,

                        TransferBuilder.Action.PLAYER_TRANSFER
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fVirement Joueur"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Sélectionne un joueur"
                );

                p.sendMessage(
                        "§7connecté au réseau bancaire."
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(

                        p.getLocation(),

                        Sound.BLOCK_NOTE_BLOCK_CHIME,

                        1f,

                        1.15f
                );

                TargetPlayerGUI.open(p);
            }

            //
            // 🏦 VIREMENT IBAN
            //

            case 6 -> {

                TransferBuilder.setAction(

                        p,

                        TransferBuilder.Action.IBAN_TRANSFER
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§b✦ §fTransfert IBAN"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Connexion au réseau"
                );

                p.sendMessage(
                        "§7bancaire MoodCraft..."
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8• Compatible hors ligne"
                );

                p.sendMessage(
                        "§8• Transactions sécurisées"
                );

                p.sendMessage(
                        "§8• Validation bancaire"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(

                        p.getLocation(),

                        Sound.BLOCK_BEACON_ACTIVATE,

                        1f,

                        1f
                );

                IbanGUI.open(p);
            }

            //
            // 🔙 RETOUR
            //

            case 8,
                 31 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.8f
                );

                BankGUI.open(p);
            }
        }
    }
}