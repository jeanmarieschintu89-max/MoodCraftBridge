package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;

import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.TransactionLogger;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.metadata.FixedMetadataValue;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransferConfirmHandler implements GUIHandler {

    //
    // 🔒 LIMITES ANTI-FRAUDE
    //

    private static final double MAX_PERSONAL_TRANSFER =
            10000.0;

    private static final double MAX_DAILY_PERSONAL_TRANSFER =
            25000.0;

    //
    // 📅 LIMITES JOURNALIÈRES
    //

    private static final Map<UUID, Double> dailySent =
            new HashMap<>();

    private static final Map<UUID, String> dailyDate =
            new HashMap<>();

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

                    //
                    // 🔒 ANTI-FRAUDE TAXE / CONTRATS
                    //

                    if (!canBypassLimit(p)) {

                        if (amount > MAX_PERSONAL_TRANSFER) {

                            denyProfessionalTransfer(
                                    p,
                                    target,
                                    amount
                            );

                            TransactionLogger.log(
                                    p.getUniqueId().toString(),
                                    "BANK_TRANSFER_BLOCKED_HIGH_AMOUNT",
                                    amount,
                                    target.getName()
                            );

                            return;
                        }

                        resetDailyIfNeeded(
                                p.getUniqueId()
                        );

                        double today =
                                dailySent.getOrDefault(
                                        p.getUniqueId(),
                                        0.0
                                );

                        if (today + amount > MAX_DAILY_PERSONAL_TRANSFER) {

                            denyDailyLimit(
                                    p,
                                    target,
                                    today,
                                    amount
                            );

                            TransactionLogger.log(
                                    p.getUniqueId().toString(),
                                    "BANK_TRANSFER_BLOCKED_DAILY_LIMIT",
                                    amount,
                                    target.getName()
                            );

                            return;
                        }
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

                    //
                    // 📅 COMPTE JOURNALIER
                    //

                    if (!canBypassLimit(p)) {

                        resetDailyIfNeeded(
                                p.getUniqueId()
                        );

                        dailySent.put(
                                p.getUniqueId(),
                                dailySent.getOrDefault(
                                        p.getUniqueId(),
                                        0.0
                                ) + amount
                        );
                    }

                    //
                    // 🧾 LOGS
                    //

                    TransactionManager.transfer(
                            p.getUniqueId(),
                            target.getUniqueId(),
                            amount
                    );

                    TransactionLogger.log(
                            p.getUniqueId().toString(),
                            "BANK_TRANSFER_SENT",
                            amount,
                            target.getName()
                    );

                    TransactionLogger.log(
                            target.getUniqueId().toString(),
                            "BANK_TRANSFER_RECEIVED",
                            amount,
                            p.getName()
                    );

                    if (amount >= MAX_PERSONAL_TRANSFER * 0.75) {

                        TransactionLogger.log(
                                p.getUniqueId().toString(),
                                "BANK_TRANSFER_HIGH_VALUE_MONITORED",
                                amount,
                                target.getName()
                        );
                    }

                    double senderNew =
                            BankStorage.get(senderId);

                    double targetNew =
                            BankStorage.get(targetId);

                    //
                    // 📩 MESSAGES PREMIUM
                    //

                    header(p);

                    p.sendMessage("§a✔ §fVirement envoyé.");
                    p.sendMessage("");
                    p.sendMessage("§7Destinataire: §e" + target.getName());
                    p.sendMessage("§7Montant: §c-" + SafeGUI.money(amount) + "€");
                    p.sendMessage("§7Banque: §6" + SafeGUI.money(senderNew) + "€");

                    footer(p);

                    header(target);

                    target.sendMessage("§a✔ §fVirement reçu.");
                    target.sendMessage("");
                    target.sendMessage("§7Expéditeur: §e" + p.getName());
                    target.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
                    target.sendMessage("§7Banque: §6" + SafeGUI.money(targetNew) + "€");

                    footer(target);

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

    //
    // 🔒 BYPASS
    //

    private boolean canBypassLimit(
            Player p
    ) {

        return p.hasPermission("moodcraftbridge.transfer.bypass")
                || p.hasPermission("moodbusiness.bypass");
    }

    //
    // ❌ REFUS GROS VIREMENT
    //

    private void denyProfessionalTransfer(
            Player p,
            Player target,
            double amount
    ) {

        header(p);

        p.sendMessage("§c✘ §fVirement refusé.");
        p.sendMessage("");
        p.sendMessage("§7Destinataire: §e" + target.getName());
        p.sendMessage("§7Montant: §e" + SafeGUI.money(amount) + "€");
        p.sendMessage("§7Limite personnelle: §e" + SafeGUI.money(MAX_PERSONAL_TRANSFER) + "€");
        p.sendMessage("");
        p.sendMessage("§7Paiement professionnel:");
        p.sendMessage("§e/contrat");
        p.sendMessage("");
        p.sendMessage("§8• §7Fonds sécurisés");
        p.sendMessage("§8• §7Taxe économique 20%");
        p.sendMessage("§8• §7Historique officiel");

        footer(p);

        fail(p);
    }

    //
    // ❌ REFUS LIMITE JOURNALIÈRE
    //

    private void denyDailyLimit(
            Player p,
            Player target,
            double already,
            double amount
    ) {

        header(p);

        p.sendMessage("§c✘ §fVirement refusé.");
        p.sendMessage("");
        p.sendMessage("§7Destinataire: §e" + target.getName());
        p.sendMessage("§7Déjà envoyé: §e" + SafeGUI.money(already) + "€");
        p.sendMessage("§7Montant: §e" + SafeGUI.money(amount) + "€");
        p.sendMessage("§7Limite jour: §e" + SafeGUI.money(MAX_DAILY_PERSONAL_TRANSFER) + "€");
        p.sendMessage("");
        p.sendMessage("§7Paiement important:");
        p.sendMessage("§e/contrat");

        footer(p);

        fail(p);
    }

    //
    // 📅 RESET JOURNALIER
    //

    private void resetDailyIfNeeded(
            UUID uuid
    ) {

        String today =
                LocalDate.now().toString();

        String stored =
                dailyDate.get(uuid);

        if (!today.equals(stored)) {

            dailyDate.put(
                    uuid,
                    today
            );

            dailySent.put(
                    uuid,
                    0.0
            );
        }
    }

    //
    // ❌ ERREUR SIMPLE
    //

    private void error(
            Player p,
            String msg
    ) {

        header(p);

        p.sendMessage("§c✘ §fTransaction refusée.");
        p.sendMessage("");
        p.sendMessage("§7" + msg);

        footer(p);

        fail(p);
    }

    //
    // 🎨 HEADER / FOOTER
    //

    private void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----");
        p.sendMessage("");
    }

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

    private void fail(
            Player p
    ) {

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