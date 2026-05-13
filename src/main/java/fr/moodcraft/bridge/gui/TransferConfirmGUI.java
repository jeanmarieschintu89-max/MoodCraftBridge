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
                "Inconnu";

        if (targetUUID != null) {

            var offline =
                    Bukkit.getOfflinePlayer(
                            targetUUID
                    );

            if (offline.getName() != null) {

                targetName =
                        offline.getName();
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
                                        : "§6✦ §fConfirmer §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Joueur: §e" + shortText(targetName, 16),
                                "§7Montant: §6" + amount + "€",
                                "",
                                blocked
                                        ? "§cMontant trop élevé"
                                        : "§8• §7Vérifie bien",
                                blocked
                                        ? "§7Limite: §e10 000€"
                                        : "§8• §7puis confirme",
                                "",
                                blocked
                                        ? "§7Paiement pro:"
                                        : "§a✔ Prêt à envoyer",
                                blocked
                                        ? "§e/contrat"
                                        : ""
                        )
                )
        );

        SafeGUI.safeSet(inv, 11,
                SafeGUI.item(
                        Material.REDSTONE_BLOCK,
                        "§c✦ Annuler",
                        "§7Annule le virement.",
                        "",
                        "§cClique pour revenir"
                )
        );

        if (blocked) {

            SafeGUI.safeSet(inv, 15,
                    SafeGUI.item(
                            Material.BARRIER,
                            "§c✦ Impossible",
                            "§7Ce virement dépasse",
                            "§7la limite personnelle.",
                            "",
                            "§8• §7Pour un gros paiement",
                            "§8• §7utilise §e/contrat",
                            "",
                            "§cVirement bloqué"
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
                                    "§7Envoie le virement.",
                                    "",
                                    "§8• §7Montant: §6" + amount + "€",
                                    "§8• §7Historique sauvegardé",
                                    "",
                                    "§aClique pour envoyer"
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

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(
                0,
                Math.max(1, max - 3)
        ) + "...";
    }
}