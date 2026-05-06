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

        Inventory inv = Bukkit.createInventory(

                null,

                27,

                "§8✦ §6Confirmation Bancaire"
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
        // 📄 DONNÉES
        //

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

        //
        // 💰 FORMAT
        //

        String amount =
                SafeGUI.money(amountValue);

        //
        // 📋 INFOS
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.PAPER,

                                "§6✦ Confirmation du Virement",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Destinataire:",

                                targetName,

                                "",

                                "§7Montant:",

                                "§6" + amount + "€",

                                "",

                                "§8• Transaction sécurisée",

                                "§8• Historique enregistré",

                                "§8• Validation finale",

                                "",

                                "§e▶ Vérifie avant validation"
                        )
                )
        );

        //
        // ❌ ANNULER
        //

        SafeGUI.safeSet(inv, 11,

                SafeGUI.item(

                        Material.REDSTONE_BLOCK,

                        "§c✦ Annuler",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Annuler le transfert",

                        "§7et revenir au menu.",

                        "",

                        "§e▶ Retour"
                )
        );

        //
        // ✅ CONFIRMER
        //

        SafeGUI.safeSet(inv, 15,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.EMERALD_BLOCK,

                                "§a✦ Confirmer",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Valider le transfert",

                                "§7vers le destinataire.",

                                "",

                                "§7Montant final: §6"
                                        + amount
                                        + "€",

                                "",

                                "§e▶ Valider"
                        )
                )
        );

        //
        // 🔊 SON
        //

        p.playSound(

                p.getLocation(),

                Sound.UI_BUTTON_CLICK,

                1f,

                1.2f
        );

        GUIManager.open(

                p,

                "transfer_confirm",

                inv
        );
    }
}