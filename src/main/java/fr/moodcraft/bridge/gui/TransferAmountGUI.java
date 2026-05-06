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

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §aVirement Bancaire"
        );

        //
        // 🌌 FOND
        //

        SafeGUI.fill(

                inv,

                Material.BLACK_STAINED_GLASS_PANE,

                " "
        );

        //
        // 🎯 DESTINATAIRE
        //

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

        //
        // 📄 INFO
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.PAPER,

                                "§6✦ Virement MoodCraft",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Destinataire:",

                                targetName,

                                "",

                                "§7Sélectionne un montant",

                                "§7à transférer.",

                                "",

                                "§8• Transaction sécurisée",

                                "§8• Historique sauvegardé",

                                "§8• Vérification bancaire"
                        )
                )
        );

        //
        // 💰 MONTANTS
        //

        SafeGUI.safeSet(inv, 10,

                SafeGUI.item(

                        Material.GOLD_NUGGET,

                        "§a✦ 100€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Petit transfert bancaire.",

                        "",

                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 11,

                SafeGUI.item(

                        Material.GOLD_INGOT,

                        "§a✦ 1 000€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Virement standard.",

                        "",

                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 12,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.GOLD_BLOCK,

                                "§6✦ 10 000€",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Transfert économique",

                                "§7important.",

                                "",

                                "§e▶ Envoyer"
                        )
                )
        );

        //
        // 💎 GROS TRANSFERTS
        //

        SafeGUI.safeSet(inv, 14,

                SafeGUI.item(

                        Material.EMERALD_BLOCK,

                        "§2✦ 50 000€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Virement haute valeur.",

                        "",

                        "§8• Analyse anti fraude",

                        "",

                        "§e▶ Envoyer"
                )
        );

        SafeGUI.safeSet(inv, 15,

                SafeGUI.item(

                        Material.DIAMOND_BLOCK,

                        "§b✦ 100 000€",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Transfert bancaire massif.",

                        "",

                        "§8• Transaction premium",

                        "§8• Vérification sécurisée",

                        "",

                        "§e▶ Envoyer"
                )
        );

        //
        // ✍️ PERSONNALISÉ
        //

        SafeGUI.safeSet(inv, 23,

                SafeGUI.item(

                        Material.ANVIL,

                        "§d✦ Montant Personnalisé",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Saisir un montant",

                        "§7manuel dans le chat.",

                        "",

                        "§8• Support décimales",

                        "§8• Contrôle automatique",

                        "",

                        "§e▶ Saisir montant"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.ARROW,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu précédent.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "transfer_amount",

                inv
        );
    }
}