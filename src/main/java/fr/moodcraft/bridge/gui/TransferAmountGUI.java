package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class TransferAmountGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §eMontant"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        UUID targetUUID =
                TransferBuilder.getTarget(p);

        String targetName =
                "§7Inconnu";

        if (targetUUID != null) {

            Player target =
                    Bukkit.getPlayer(targetUUID);

            if (target != null) {

                targetName =
                        "§e" + target.getName();

            } else {

                String offline =
                        Bukkit.getOfflinePlayer(targetUUID)
                                .getName();

                if (offline != null) {

                    targetName =
                            "§e" + offline;
                }
            }
        }

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.PAPER,
                                "§6✦ Virement bancaire",
                                "§8----- §6Destinataire §8-----",
                                "§7Joueur: " + targetName,
                                "",
                                "§7Choisis le montant à envoyer.",
                                "",
                                "§8• §7Sécurisé",
                                "§8• §7Historique sauvegardé"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.item(
                        Material.GOLD_NUGGET,
                        "§a✦ 100€",
                        "§7Petit transfert.",
                        "",
                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.GOLD_INGOT,
                        "§a✦ 1 000€",
                        "§7Virement standard.",
                        "",
                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.GOLD_BLOCK,
                                "§6✦ 10 000€",
                                "§7Transfert important.",
                                "",
                                "§e▶ Envoyer"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.EMERALD_BLOCK,
                        "§2✦ 50 000€",
                        "§7Virement haute valeur.",
                        "",
                        "§8• §7Contrôle bancaire",
                        "",
                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.DIAMOND_BLOCK,
                        "§b✦ 100 000€",
                        "§7Transfert premium.",
                        "",
                        "§8• §7Vérification renforcée",
                        "",
                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 23,
                SafeGUI.item(
                        Material.ANVIL,
                        "§d✦ Montant libre",
                        "§7Choisis ton propre montant.",
                        "",
                        "§8• §7Décimales acceptées",
                        "§8• §7Vérification auto",
                        "",
                        "§e▶ Saisir"
                )
        );

        SafeGUI.safeSet(inv, 31,
                SafeGUI.item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au choix du joueur.",
                        "",
                        "§c▶ Retour"
                )
        );

        GUIManager.open(
                p,
                "transfer_amount",
                inv
        );
    }
}