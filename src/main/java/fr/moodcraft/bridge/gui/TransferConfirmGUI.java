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
                                Material.PAPER,
                                "§6✦ Confirmer le virement",
                                "§8----- §6Résumé §8-----",
                                "§7Destinataire: " + targetName,
                                "§7Montant: §6" + amount + "€",
                                "",
                                "§8• §7Transaction sécurisée",
                                "§8• §7Historique sauvegardé",
                                "",
                                "§e▶ Vérifie avant validation"
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

        GUIManager.open(
                p,
                "transfer_confirm",
                inv
        );
    }
}