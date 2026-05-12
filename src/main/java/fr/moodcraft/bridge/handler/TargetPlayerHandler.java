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

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
            p.sendMessage("§cAucun joueur sélectionné.");
            p.sendMessage("");

            fail(p);

            return;
        }

        Player target =
                Bukkit.getPlayer(targetUUID);

        if (target == null
                || !target.isOnline()) {

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
            p.sendMessage("§cCe joueur n'est plus connecté.");
            p.sendMessage("");

            fail(p);

            return;
        }

        if (target.equals(p)) {

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
            p.sendMessage("§cTu ne peux pas t'envoyer un virement.");
            p.sendMessage("");

            fail(p);

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

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§a✔ Destinataire sélectionné");
        p.sendMessage("§7Joueur: §e" + target.getName());
        p.sendMessage("§7Réputation: §a" + rep + " §8• " + rank);
        p.sendMessage("");
        p.sendMessage("§7Écris maintenant le montant du virement.");
        p.sendMessage("§8Exemple: §e5000");
        p.sendMessage("§8Tape §cannuler §8pour quitter.");
        p.sendMessage("");

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

    private void fail(Player p) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
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