package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.contract.Contract;

import fr.moodcraft.bridge.gui.ContractGUI;
import fr.moodcraft.bridge.gui.PublicContractsGUI;

import fr.moodcraft.bridge.manager.ContractManager;

import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class PublicContractsHandler implements GUIHandler {

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

        if (slot == 49) {

            p.playSound(

                    p.getLocation(),

                    Sound.UI_BUTTON_CLICK,

                    1f,

                    0.8f
            );

            ContractGUI.open(p);

            return;
        }

        //
        // 📦 ZONE CONTRATS
        //

        if (slot < 19
                || slot > 43)
            return;

        //
        // 🔍 RÉCUP
        //

        var item =
                p.getOpenInventory()
                        .getItem(slot);

        if (item == null)
            return;

        //
        // 📜 NOM
        //

        if (!item.hasItemMeta())
            return;

        var meta =
                item.getItemMeta();

        if (meta == null
                || !meta.hasDisplayName())
            return;

        String name =
                meta.getDisplayName();

        //
        // 🔍 ID
        //

        if (!name.contains("#"))
            return;

        String id =
                name.substring(
                        name.indexOf("#") + 1
                );

        Contract contract =
                ContractManager.get(id);

        if (contract == null) {

            p.sendMessage(
                    "§cContrat introuvable."
            );

            return;
        }

        //
        // 🔒 STATUS
        //

        if (contract.getStatus()
                != Contract.Status.OPEN) {

            p.sendMessage(
                    "§cCe contrat n'est plus disponible."
            );

            return;
        }

        //
        // ❌ OWN CONTRACT
        //

        if (contract.getOwner()
                .equals(p.getUniqueId())) {

            p.sendMessage(
                    "§cTu ne peux pas accepter ton propre contrat."
            );

            return;
        }

        //
        // 🤝 ACCEPT
        //

        contract.setWorker(
                p.getUniqueId()
        );

        contract.setStatus(
                Contract.Status.IN_PROGRESS
        );

        //
        // ✨ FEEDBACK
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fContrat accepté"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Objet: §f"
                        + contract.getItem().name()
        );

        p.sendMessage(
                "§7Quantité: §e"
                        + contract.getAmount()
        );

        p.sendMessage(
                "§7Récompense: §a"
                        + SafeGUI.money(
                        contract.getReward()
                )
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        p.playSound(

                p.getLocation(),

                Sound.ENTITY_PLAYER_LEVELUP,

                1f,

                1.15f
        );

        //
        // 🔄 REFRESH
        //

        PublicContractsGUI.open(p);
    }
}