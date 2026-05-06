
package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TransferAmountGUI;
import fr.moodcraft.bridge.gui.TransferTypeGUI;
import fr.moodcraft.bridge.gui.TargetPlayerGUI;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TargetPlayerHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        //
        // 🔙 RETOUR
        //

        if (slot == 49) {

            TransferTypeGUI.open(p);

            return;
        }

        //
        // 👤 TARGET
        //

        UUID targetUUID =
                TargetPlayerGUI.getTarget(slot);

        if (targetUUID == null) {

            p.sendMessage("§cAucun joueur sélectionné.");

            return;
        }

        Player target =
                Bukkit.getPlayer(targetUUID);

        if (target == null || !target.isOnline()) {

            p.sendMessage("§cJoueur introuvable.");

            return;
        }

        //
        // ❌ sécurité soi-même
        //

        if (target.equals(p)) {

            p.sendMessage("§cTu ne peux pas te transférer d'argent.");

            return;
        }

        //
        // 🔥 feedback
        //

        p.sendMessage(
                "§a✔ Joueur sélectionné: §e"
                        + target.getName()
        );

        //
        // 💾 action
        //

        TransferBuilder.setAction(
                p,
                TransferBuilder.Action.PLAYER_TRANSFER
        );

        //
        // 🎯 target
        //

        TransferBuilder.setTarget(
                p,
                targetUUID
        );

        //
        // 💸 montant
        //

        TransferAmountGUI.open(p);
    }
}