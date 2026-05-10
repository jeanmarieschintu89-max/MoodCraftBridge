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

            case 11 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                BankGUI.open(p);
            }

            case 15 -> {

                if (p.hasMetadata(
                        "transfer_processing"
                )) return;

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

                    UUID targetUUID =
                            TransferBuilder.getTarget(p);

                    double amount =
                            TransferBuilder.getAmount(p);

                    if (targetUUID == null) {

                        error(
                                p,
                                "Aucun destinataire sélectionné."
                        );

                        TransferBuilder.clear(p);

                        return;
                    }

                    if (amount <= 0) {

                        error(
                                p,
                                "Montant invalide."
                        );

                        return;
                    }

                    Player target =
                            Bukkit.getPlayer(targetUUID);

                    if (target == null) {

                        error(
                                p,
                                "Le joueur est hors ligne."
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

                    String senderId =
                            p.getUniqueId().toString();

                    String targetId =
                            target.getUniqueId().toString();

                    double senderBank =
                            BankStorage.get(senderId);

                    if (senderBank < amount) {

                        error(
                                p,
                                "Fonds bancaires insuffisants."
                        );

                        return;
                    }

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

                    TransactionManager.transfer(
                            p.getUniqueId(),
                            target.getUniqueId(),
                            amount
                    );

                    double senderNew =
                            BankStorage.get(senderId);

                    double targetNew =
                            BankStorage.get(targetId);

                    p.sendMessage("");
                    p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                    p.sendMessage("§a✔ Virement envoyé");
                    p.sendMessage("§7Destinataire: §e" + target.getName());
                    p.sendMessage("§7Montant: §c-" + SafeGUI.money(amount) + "€");
                    p.sendMessage("§7Banque: §6" + SafeGUI.money(senderNew) + "€");
                    p.sendMessage("");

                    target.sendMessage("");
                    target.sendMessage("§8----- §6Banque MoodCraft §8-----");
                    target.sendMessage("§a✔ Virement reçu");
                    target.sendMessage("§7Expéditeur: §e" + p.getName());
                    target.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
                    target.sendMessage("§7Banque: §6" + SafeGUI.money(targetNew) + "€");
                    target.sendMessage("");

                    p.playSound(
                            p.getLocation(),
                            Sound.UI_TOAST_CHALLENGE_COMPLETE,
                            0.8f,
                            1f
                    );

                    p.playSound(
                            p.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_CHIME,
                            0.5f,
                            1.4f
                    );

                    target.playSound(
                            target.getLocation(),
                            Sound.ENTITY_PLAYER_LEVELUP,
                            0.8f,
                            1.15f
                    );

                    target.playSound(
                            target.getLocation(),
                            Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                            0.5f,
                            1.4f
                    );

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

                    p.sendTitle(
                            "§aVirement envoyé",
                            "§f-" + SafeGUI.money(amount) + "€",
                            5,
                            35,
                            10
                    );

                    target.sendTitle(
                            "§aVirement reçu",
                            "§f+" + SafeGUI.money(amount) + "€",
                            5,
                            35,
                            10
                    );

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

    private void error(
            Player p,
            String msg
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6Banque MoodCraft §8-----");
        p.sendMessage("§cTransaction refusée.");
        p.sendMessage("§7" + msg);
        p.sendMessage("");

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