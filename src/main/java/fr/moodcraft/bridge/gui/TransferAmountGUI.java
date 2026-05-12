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
                        "§8✦ §6Virement §aMood§6Craft"
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
                                "§6✦ §fVirement personnel §6✦",
                                "§8----- §6Banque §aMood§6Craft §8-----",
                                "§7Joueur: " + targetName,
                                "",
                                "§7Les virements sont réservés",
                                "§7aux transferts personnels simples.",
                                "",
                                "§8• §7Historique sauvegardé",
                                "§8• §7Montants élevés surveillés",
                                "",
                                "§ePour un service ou une entreprise:",
                                "§f/contrat"
                        )
                )
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.item(
                        Material.GOLD_NUGGET,
                        "§a✦ 100€",
                        "§7Petit transfert personnel.",
                        "",
                        "§a✔ Autorisé",
                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(
                        Material.GOLD_INGOT,
                        "§a✦ 1 000€",
                        "§7Virement personnel standard.",
                        "",
                        "§a✔ Autorisé",
                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.GOLD_BLOCK,
                                "§6✦ 10 000€",
                                "§7Transfert personnel important.",
                                "",
                                "§e⚠ Montant surveillé",
                                "§7Pour un achat professionnel,",
                                "§7utilisez plutôt §e/contrat§7.",
                                "",
                                "§e▶ Envoyer"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ 50 000€ bloqué",
                        "§7Les gros paiements directs",
                        "§7peuvent contourner les taxes",
                        "§7et les contrats officiels.",
                        "",
                        "§c✘ Virement déconseillé",
                        "§eUtilisez: §f/contrat"
                )
        );

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✦ 100 000€ bloqué",
                        "§7Ce montant doit passer par",
                        "§7un contrat sécurisé.",
                        "",
                        "§8• §7Fonds bloqués",
                        "§8• §7Taxe économique 20%",
                        "§8• §7Historique officiel",
                        "§8• §7Protection anti-arnaque",
                        "",
                        "§eUtilisez: §f/contrat"
                )
        );

        SafeGUI.safeSet(inv, 23,
                SafeGUI.item(
                        Material.ANVIL,
                        "§d✦ Montant libre",
                        "§7Choisis ton propre montant.",
                        "",
                        "§e⚠ Limite anti-fraude appliquée",
                        "§7Les paiements professionnels",
                        "§7doivent passer par §e/contrat§7.",
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