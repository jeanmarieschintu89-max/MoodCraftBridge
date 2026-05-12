package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class TransferConfirmGUI {

    private static final double MAX_PERSONAL_TRANSFER =
            10000.0;

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        27,
                        "§8✦ §6Confirmation"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        UUID targetUUID =
                TransferBuilder.getTarget(p);

        double amountValue =
                TransferBuilder.getAmount(p);

        boolean blocked =
                amountValue > MAX_PERSONAL_TRANSFER
                        && !p.hasPermission("moodcraftbridge.transfer.bypass")
                        && !p.hasPermission("moodbusiness.bypass");

        String targetName =
                "§7Inconnu";

        if (targetUUID != null) {

            var offline =
                    Bukkit.getOfflinePlayer(
                            targetUUID
                    );

            if (offline.getName() != null) {

                targetName =
                        "§e" + offline.getName();
            }
        }

        String amount =
                SafeGUI.money(amountValue);

        SafeGUI.safeSet(inv, 13,
                SafeGUI.glow(
                        SafeGUI.item(
                                blocked
                                        ? Material.BARRIER
                                        : Material.PAPER,
                                blocked
                                        ? "§c✦ Virement bloqué"
                                        : "§6✦ Confirmer le virement",
                                "§8----- §6Banque §aMood§6Craft §8-----",
                                "§7Destinataire: " + targetName,
                                "§7Montant: §6" + amount + "€",
                                "",
                                blocked
                                        ? "§cCe montant dépasse la limite personnelle."
                                        : "§8• §7Transaction sécurisée",
                                blocked
                                        ? "§7Limite virement personnel: §e10 000€"
                                        : "§8• §7Historique sauvegardé",
                                "",
                                blocked
                                        ? "§7Les paiements professionnels doivent"
                                        : "§e▶ Vérifie avant validation",
                                blocked
                                        ? "§7passer par un §econtrat officiel§7."
                                        : "",
                                blocked
                                        ? ""
                                        : "",
                                blocked
                                        ? "§8• §7Fonds sécurisés"
                                        : "",
                                blocked
                                        ? "§8• §7Taxe économique 20%"
                                        : "",
                                blocked
                                        ? "§8• §7Historique officiel"
                                        : "",
                                blocked
                                        ? "§8• §7Protection anti-arnaque"
                                        : "",
                                blocked
                                        ? ""
                                        : "",
                                blocked
                                        ? "§eUtilisez: §f/contrat"
                                        : ""
                        )
                )
        );

        SafeGUI.safeSet(inv, 11,
                SafeGUI.item(
                        Material.REDSTONE_BLOCK,
                        "§c✦ Annuler",
                        "§7Annule le transfert.",
                        "",
                        "§c▶ Retour"
                )
        );

        if (blocked) {

            SafeGUI.safeSet(inv, 15,
                    SafeGUI.item(
                            Material.BARRIER,
                            "§c✦ Confirmation impossible",
                            "§7Ce virement est trop élevé",
                            "§7pour un transfert personnel.",
                            "",
                            "§7Les paiements professionnels",
                            "§7doivent passer par §e/contrat§7.",
                            "",
                            "§c▶ Virement bloqué"
                    )
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.7f
            );

        } else {

            SafeGUI.safeSet(inv, 15,
                    SafeGUI.glow(
                            SafeGUI.item(
                                    Material.EMERALD_BLOCK,
                                    "§a✦ Confirmer",
                                    "§7Valide le virement.",
                                    "",
                                    "§8• §7Montant: §6" + amount + "€",
                                    "",
                                    "§a▶ Envoyer"
                            )
                    )
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    0.6f,
                    1.2f
            );
        }

        GUIManager.open(
                p,
                "transfer_confirm",
                inv
        );
    }
}