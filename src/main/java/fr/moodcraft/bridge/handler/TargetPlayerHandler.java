package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TargetPlayerGUI;
import fr.moodcraft.bridge.gui.TransferTypeGUI;

import fr.moodcraft.bridge.listener.BankChatInputListener;

import fr.moodcraft.bridge.manager.ReputationManager;
import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import java.util.UUID;

public class TargetPlayerHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                250
        )) return;

        //
        // ↩ RETOUR / NAVIGATION
        //

        if (slot == 45
                || slot == 49
                || slot == 53) {

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    0.8f,
                    Sound.BLOCK_CHEST_CLOSE,
                    1.2f
            );

            TransferTypeGUI.open(p);

            return;
        }

        UUID targetUUID =
                TargetPlayerGUI.getTarget(slot);

        if (targetUUID == null) {

            error(
                    p,
                    "Aucun joueur sélectionné."
            );

            return;
        }

        Player target =
                Bukkit.getPlayer(targetUUID);

        if (target == null
                || !target.isOnline()) {

            error(
                    p,
                    "Ce joueur n'est plus connecté."
            );

            return;
        }

        if (target.equals(p)) {

            error(
                    p,
                    "Tu ne peux pas t'envoyer un virement."
            );

            return;
        }

        int rep =
                ReputationManager.get(
                        targetUUID.toString()
                );

        String rank =
                ReputationManager.getRank(rep);

        TransferBuilder.setAction(
                p,
                TransferBuilder.Action.PLAYER_TRANSFER
        );

        TransferBuilder.setTarget(
                p,
                targetUUID
        );

        p.closeInventory();

        header(p);

        p.sendMessage("§a✔ §fDestinataire sélectionné.");
        p.sendMessage("");
        p.sendMessage("§7Joueur: §e" + target.getName());
        p.sendMessage("§7Réputation: §a" + rep + " §8• " + rank);
        p.sendMessage("");
        p.sendMessage("§fÉcris le montant du virement.");
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple: §e5000");
        p.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

        footer(p);

        premiumClick(
                p,
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                1.25f,
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1.4f
        );

        BankChatInputListener.startTransferAmount(
                p,
                targetUUID
        );
    }

    //
    // ❌ ERROR
    //

    private void error(
            Player p,
            String message
    ) {

        header(p);

        p.sendMessage("§c✘ §fAction refusée.");
        p.sendMessage("");
        p.sendMessage("§7" + message);

        footer(p);

        fail(p);
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
    // 🔊 FAIL
    //

    private void fail(Player p) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    //
    // 🔊 CLICK PREMIUM
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