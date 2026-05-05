package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.TargetPlayerGUI;
import fr.moodcraft.bridge.gui.TransferAmountGUI;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TargetPlayerHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        UUID targetUUID = TargetPlayerGUI.getTarget(slot);

        if (targetUUID == null) return;

        Player target = Bukkit.getPlayer(targetUUID);

        if (target == null) {
            p.sendMessage("§cJoueur introuvable.");
            return;
        }

        // 🔥 feedback
        p.sendMessage("§a✔ Joueur sélectionné: §e" + target.getName());

        // 🔥 action
        TransferBuilder.setAction(p, TransferBuilder.Action.PLAYER_TRANSFER);

        // 🔥 target
        TransferBuilder.setTarget(p, targetUUID);

        // 👉 montant
        TransferAmountGUI.open(p);
    }
}