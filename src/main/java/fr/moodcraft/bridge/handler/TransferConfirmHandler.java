package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TransferConfirmHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        TransferBuilder data = TransferBuilder.get(p);

        switch (slot) {

            // 🔙 RETOUR
            case 3 -> {
                BankGUI.open(p);
            }

            // ✅ CONFIRMER
            case 5 -> {

                // 🔒 anti double clic
                if (p.hasMetadata("transfer_processing")) return;

                p.setMetadata("transfer_processing",
                        new FixedMetadataValue(Main.getInstance(), true));

                try {

                    // =========================
                    // 🔒 VALIDATIONS
                    // =========================

                    if (data.target == null) {
                        p.sendMessage("§cErreur: aucun joueur sélectionné.");
                        TransferBuilder.clear(p);
                        return;
                    }

                    if (data.amount <= 0) {
                        p.sendMessage("§cMontant invalide.");
                        return;
                    }

                    Player target = Bukkit.getPlayer(data.target);

                    if (target == null) {
                        p.sendMessage("§cJoueur hors ligne");
                        return;
                    }

                    if (target.equals(p)) {
                        p.sendMessage("§cTu ne peux pas te virer à toi-même");
                        return;
                    }

                    String senderId = p.getUniqueId().toString();
                    String targetId = target.getUniqueId().toString();

                    double senderBank = BankStorage.get(senderId);

                    if (senderBank < data.amount) {
                        p.sendMessage("§cFonds insuffisants");
                        return;
                    }

                    // =========================
                    // 💸 TRANSFERT
                    // =========================

                    boolean success = BankStorage.transfer(senderId, targetId, data.amount);

                    if (!success) {
                        p.sendMessage("§cErreur lors du virement.");
                        return;
                    }

                    double newBalanceSender = BankStorage.get(senderId);
                    double newBalanceTarget = BankStorage.get(targetId);

                    // =========================
                    // 💬 EXPÉDITEUR
                    // =========================

                    p.sendMessage("");
                    p.sendMessage("§8╔════════════════════════════╗");
                    p.sendMessage("§8║   §a✔ Virement effectué");
                    p.sendMessage("§8╠════════════════════════════╣");
                    p.sendMessage("§8║ §7Vers: §e" + target.getName());
                    p.sendMessage("§8║ §7Montant: §c-" + (int) data.amount + "€");
                    p.sendMessage("§8║");
                    p.sendMessage("§8║ §7Solde: §6" + (int) newBalanceSender + "€");
                    p.sendMessage("§8╚════════════════════════════╝");
                    p.sendMessage("");

                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                    // =========================
                    // 💬 RÉCEPTION
                    // =========================

                    target.sendMessage("");
                    target.sendMessage("§8╔════════════════════════════╗");
                    target.sendMessage("§8║   §a💸 Virement reçu");
                    target.sendMessage("§8╠════════════════════════════╣");
                    target.sendMessage("§8║ §7Expéditeur: §e" + p.getName());
                    target.sendMessage("§8║ §7Montant: §a+" + (int) data.amount + "€");
                    target.sendMessage("§8║");
                    target.sendMessage("§8║ §7Solde: §6" + (int) newBalanceTarget + "€");
                    target.sendMessage("§8╚════════════════════════════╝");
                    target.sendMessage("");

                    target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);

                    target.getWorld().spawnParticle(
                            Particle.VILLAGER_HAPPY,
                            target.getLocation().add(0, 1, 0),
                            25
                    );

                    // =========================
                    // 🧹 CLEAN
                    // =========================
                    TransferBuilder.clear(p);
                    p.closeInventory();

                } finally {
                    p.removeMetadata("transfer_processing", Main.getInstance());
                }
            }
        }
    }
}