package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;

import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class TransferConfirmHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            //
            // ❌ ANNULER
            //

            case 11 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.8f
                );

                BankGUI.open(p);
            }

            //
            // ✅ CONFIRMATION
            //

            case 15 -> {

                //
                // 🔒 ANTI DOUBLE CLIC
                //

                if (p.hasMetadata(
                        "transfer_processing"
                )) return;

                //
                // 🔒 ANTI SPAM
                //

                if (ActionLock.isLocked(
                        p.getUniqueId(),
                        1200
                )) return;

                p.setMetadata(

                        "transfer_processing",

                        new FixedMetadataValue(
                                Main.getInstance(),
                                true
                        )
                );

                try {

                    //
                    // 📦 DATA
                    //

                    UUID targetUUID =
                            TransferBuilder.getTarget(p);

                    double amount =
                            TransferBuilder.getAmount(p);

                    //
                    // ❌ TARGET NULL
                    //

                    if (targetUUID == null) {

                        error(
                                p,
                                "Aucun destinataire sélectionné."
                        );

                        TransferBuilder.clear(p);

                        return;
                    }

                    //
                    // ❌ MONTANT
                    //

                    if (amount <= 0) {

                        error(
                                p,
                                "Montant invalide."
                        );

                        return;
                    }

                    //
                    // 👤 TARGET
                    //

                    Player target =
                            Bukkit.getPlayer(targetUUID);

                    if (target == null) {

                        error(
                                p,
                                "Le joueur est hors ligne."
                        );

                        return;
                    }

                    //
                    // ❌ SELF
                    //

                    if (target.equals(p)) {

                        error(
                                p,
                                "Tu ne peux pas te transférer de l'argent."
                        );

                        return;
                    }

                    //
                    // 💳 IDS
                    //

                    String senderId =
                            p.getUniqueId().toString();

                    String targetId =
                            target.getUniqueId().toString();

                    //
                    // 🏦 SOLDE
                    //

                    double senderBank =
                            BankStorage.get(senderId);

                    if (senderBank < amount) {

                        error(
                                p,
                                "Fonds bancaires insuffisants."
                        );

                        return;
                    }

                    //
                    // 💸 TRANSFERT
                    //

                    boolean success =
                            BankStorage.transfer(
                                    senderId,
                                    targetId,
                                    amount
                            );

                    if (!success) {

                        error(
                                p,
                                "Erreur système lors du transfert."
                        );

                        return;
                    }

                    //
                    // 📜 HISTORIQUE
                    //

                    TransactionManager.transfer(

                            p.getUniqueId(),

                            target.getUniqueId(),

                            amount
                    );

                    //
                    // 📊 SOLDES
                    //

                    double senderNew =
                            BankStorage.get(senderId);

                    double targetNew =
                            BankStorage.get(targetId);

                    //
                    // ✨ MESSAGE EXPÉDITEUR
                    //

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage(
                            "§6✦ §fVirement effectué"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Destinataire:"
                    );

                    p.sendMessage(
                            "§e" + target.getName()
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Montant transféré:"
                    );

                    p.sendMessage(
                            "§c-"
                                    + SafeGUI.money(amount)
                                    + "€"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Nouveau solde bancaire:"
                    );

                    p.sendMessage(
                            "§6"
                                    + SafeGUI.money(senderNew)
                                    + "€"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage("");

                    //
                    // ✨ MESSAGE RECEVEUR
                    //

                    target.sendMessage("");

                    target.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    target.sendMessage(
                            "§a✦ §fVirement reçu"
                    );

                    target.sendMessage("");

                    target.sendMessage(
                            "§7Expéditeur:"
                    );

                    target.sendMessage(
                            "§e" + p.getName()
                    );

                    target.sendMessage("");

                    target.sendMessage(
                            "§7Montant reçu:"
                    );

                    target.sendMessage(
                            "§a+"
                                    + SafeGUI.money(amount)
                                    + "€"
                    );

                    target.sendMessage("");

                    target.sendMessage(
                            "§7Nouveau solde bancaire:"
                    );

                    target.sendMessage(
                            "§6"
                                    + SafeGUI.money(targetNew)
                                    + "€"
                    );

                    target.sendMessage("");

                    target.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    target.sendMessage("");

                    //
                    // 🔊 SONS
                    //

                    p.playSound(

                            p.getLocation(),

                            Sound.UI_TOAST_CHALLENGE_COMPLETE,

                            1f,

                            1f
                    );

                    target.playSound(

                            target.getLocation(),

                            Sound.ENTITY_PLAYER_LEVELUP,

                            1f,

                            1.15f
                    );

                    //
                    // ✨ PARTICULES
                    //

                    target.getWorld().spawnParticle(

                            Particle.TOTEM_OF_UNDYING,

                            target.getLocation().add(
                                    0,
                                    1,
                                    0
                            ),

                            20,

                            0.4,

                            0.6,

                            0.4,

                            0.02
                    );

                    //
                    // 🎬 TITLES
                    //

                    p.sendTitle(

                            "§aVirement envoyé",

                            "§f-"
                                    + SafeGUI.money(amount)
                                    + "€",

                            5,

                            35,

                            10
                    );

                    target.sendTitle(

                            "§aVirement reçu",

                            "§f+"
                                    + SafeGUI.money(amount)
                                    + "€",

                            5,

                            35,

                            10
                    );

                    //
                    // 🧹 CLEAN
                    //

                    TransferBuilder.clear(p);

                    p.closeInventory();

                } finally {

                    p.removeMetadata(

                            "transfer_processing",

                            Main.getInstance()
                    );
                }
            }
        }
    }

    //
    // ❌ ERROR
    //

    private void error(Player p,
                       String msg) {

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§c✦ Transaction refusée"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7" + msg
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
    }
}