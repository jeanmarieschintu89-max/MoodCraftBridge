package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TargetPlayerGUI;
import fr.moodcraft.bridge.gui.TransferAmountGUI;
import fr.moodcraft.bridge.gui.TransferTypeGUI;

import fr.moodcraft.bridge.manager.ReputationManager;
import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Bukkit;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import java.util.UUID;

public class TargetPlayerHandler implements GUIHandler {

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
        // 🔙 RETOUR
        //

        if (slot == 49
                || slot == 45
                || slot == 53) {

            p.playSound(

                    p.getLocation(),

                    Sound.UI_BUTTON_CLICK,

                    1f,

                    0.8f
            );

            TransferTypeGUI.open(p);

            return;
        }

        //
        // 👤 TARGET UUID
        //

        UUID targetUUID =
                TargetPlayerGUI.getTarget(slot);

        if (targetUUID == null) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fRéseau bancaire"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Aucun joueur sélectionné."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            p.playSound(

                    p.getLocation(),

                    Sound.ENTITY_VILLAGER_NO,

                    1f,

                    1f
            );

            return;
        }

        //
        // 👤 PLAYER
        //

        Player target =
                Bukkit.getPlayer(targetUUID);

        if (target == null
                || !target.isOnline()) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fConnexion impossible"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Le joueur n'est plus"
            );

            p.sendMessage(
                    "§7connecté au réseau."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            p.playSound(

                    p.getLocation(),

                    Sound.ENTITY_VILLAGER_NO,

                    1f,

                    0.9f
            );

            return;
        }

        //
        // ❌ SELF
        //

        if (target.equals(p)) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fTransaction refusée"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Tu ne peux pas effectuer"
            );

            p.sendMessage(
                    "§7un virement vers toi-même."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            p.playSound(

                    p.getLocation(),

                    Sound.ENTITY_VILLAGER_NO,

                    1f,

                    0.8f
            );

            return;
        }

        //
        // 📊 RÉPUTATION
        //

        int rep =
                ReputationManager.get(
                        targetUUID.toString()
                );

        String rank =
                ReputationManager.getRank(rep);

        //
        // 💾 ACTION
        //

        TransferBuilder.setAction(

                p,

                TransferBuilder.Action.PLAYER_TRANSFER
        );

        //
        // 🎯 TARGET
        //

        TransferBuilder.setTarget(
                p,
                targetUUID
        );

        //
        // ✨ FEEDBACK
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fDestinataire sélectionné"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Joueur: §e"
                        + target.getName()
        );

        p.sendMessage(
                "§7Réputation: §a"
                        + rep
        );

        p.sendMessage(
                "§7Statut: "
                        + rank
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Préparation du transfert..."
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        //
        // 🔊 SOUND
        //

        p.playSound(

                p.getLocation(),

                Sound.BLOCK_NOTE_BLOCK_CHIME,

                1f,

                1.2f
        );

        //
        // 💸 NEXT
        //

        TransferAmountGUI.open(p);
    }
}