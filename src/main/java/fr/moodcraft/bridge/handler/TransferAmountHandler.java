
package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransferConfirmGUI;

import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.metadata.FixedMetadataValue;

public class TransferAmountHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        //
        // 🔙 RETOUR
        //

        if (slot == 22 || slot == 31) {

            p.playSound(

                    p.getLocation(),

                    Sound.UI_BUTTON_CLICK,

                    1f,

                    0.8f
            );

            p.closeInventory();

            BankGUI.open(p);

            return;
        }

        //
        // 💰 MONTANTS
        //

        double amount = switch (slot) {

            case 10 -> 100;

            case 11 -> 1000;

            case 12 -> 10000;

            case 14 -> 50000;

            case 15 -> 100000;

            default -> 0;
        };

        //
        // ✍️ PERSONNALISÉ
        //

        if (slot == 16 || slot == 23) {

            p.closeInventory();

            p.setMetadata(

                    "input_active",

                    new FixedMetadataValue(
                            Main.getInstance(),
                            true
                    )
            );

            AmountInputManager.wait(

                    p,

                    AmountInputManager.Type.PLAYER_TRANSFER
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§6✦ §fMontant personnalisé"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Entre le montant"
            );

            p.sendMessage(
                    "§7dans le chat."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8Exemple: §e25000"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            p.playSound(

                    p.getLocation(),

                    Sound.UI_BUTTON_CLICK,

                    1f,

                    1.1f
            );

            return;
        }

        //
        // ❌ INVALID
        //

        if (amount <= 0)
            return;

        //
        // 💾 SAVE
        //

        TransferBuilder.setAmount(
                p,
                amount
        );

        //
        // 🔎 ACTION
        //

        TransferBuilder.Action action =
                TransferBuilder.getAction(p);

        if (action == null) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ Erreur bancaire"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Action inconnue."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            p.playSound(

                    p.getLocation(),

                    Sound.ENTITY_VILLAGER_NO,

                    1f,

                    0.9f
            );

            return;
        }

        //
        // 💳 VIREMENTS
        //

        switch (action) {

            //
            // 👤 PLAYER
            // 🏦 IBAN
            //

            case PLAYER_TRANSFER,
                 IBAN_TRANSFER -> {

                //
                // 💰 CHECK SOLDE
                //

                double bank =
                        BankStorage.get(
                                p.getUniqueId().toString()
                        );

                if (bank < amount) {

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage(
                            "§c✦ Fonds insuffisants"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Solde bancaire:"
                    );

                    p.sendMessage(
                            "§6"
                                    + (int) bank
                                    + "€"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage("");

                    p.playSound(

                            p.getLocation(),

                            Sound.ENTITY_VILLAGER_NO,

                            1f,

                            0.9f
                    );

                    return;
                }

                //
                // 🌟 FEEDBACK GROS VIREMENT
                //

                if (amount >= 50000) {

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage(
                            "§6✦ Transfert important détecté"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Montant:"
                    );

                    p.sendMessage(
                            "§e"
                                    + (int) amount
                                    + "€"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Validation bancaire requise."
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage("");

                    p.playSound(

                            p.getLocation(),

                            Sound.BLOCK_BEACON_AMBIENT,

                            1f,

                            0.9f
                    );
                }

                //
                // 🔊 NORMAL
                //

                else {

                    p.playSound(

                            p.getLocation(),

                            Sound.UI_BUTTON_CLICK,

                            1f,

                            1.2f
                    );
                }

                //
                // 📂 CONFIRMATION
                //

                p.closeInventory();

                TransferConfirmGUI.open(p);
            }

            //
            // ❌ FALLBACK
            //

            default -> {

                p.sendMessage(
                        "§cAction bancaire invalide."
                );

                p.playSound(

                        p.getLocation(),

                        Sound.ENTITY_VILLAGER_NO,

                        1f,

                        0.8f
                );
            }
        }
    }
}