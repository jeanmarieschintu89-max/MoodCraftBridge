package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.IbanManager;

import fr.moodcraft.bridge.gui.*;

import fr.moodcraft.bridge.manager.TransferBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class BankHandler implements GUIHandler {

    @Override
    public void onClick(Player p,
                        int slot) {

        switch (slot) {

            //
            // 📥 DEPOT
            //

            case 10 -> {

                //
                // 🔥 RESET
                //

                TransferBuilder.clear(p);

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.DEPOSIT
                );

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        1.1f
                );

                DepositGUI.open(p);
            }

            //
            // 📤 RETRAIT
            //

            case 12 -> {

                //
                // 🔥 RESET
                //

                TransferBuilder.clear(p);

                TransferBuilder.setAction(
                        p,
                        TransferBuilder.Action.WITHDRAW
                );

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        0.9f
                );

                WithdrawGUI.open(p);
            }

            //
            // 💸 VIREMENT
            //

            case 14 -> {

                //
                // 🔥 RESET TRANSFERT
                //

                TransferBuilder.clear(p);

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        1.2f
                );

                TransferTypeGUI.open(p);
            }

            //
            // 🏦 IBAN
            //

            case 16 -> {

                p.closeInventory();

                String iban =
                        IbanManager.get(
                                p.getUniqueId()
                        );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6🏦 §fBanque MoodCraft"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7IBAN associé:"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§e" + iban
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Utilisable pour:"
                );

                p.sendMessage(
                        "§8• Virements"
                );

                p.sendMessage(
                        "§8• Contrats"
                );

                p.sendMessage(
                        "§8• Transactions sécurisées"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_PLING,
                        1f,
                        1.2f
                );
            }

            //
            // 📜 HISTORIQUE
            //

            case 20 -> {

                p.closeInventory();

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§d✦ §fHistorique bancaire"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Commande disponible:"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§e/banque historique"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Tu peux consulter:"
                );

                p.sendMessage(
                        "§8• Dépôts"
                );

                p.sendMessage(
                        "§8• Retraits"
                );

                p.sendMessage(
                        "§8• Virements"
                );

                p.sendMessage(
                        "§8• Achats"
                );

                p.sendMessage(
                        "§8• Ventes"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1f,
                        1.2f
                );
            }

            //
            // 📈 ACTIVITÉ
            //

            case 24 -> {

                p.closeInventory();

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fActivité économique"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Le système d'analyse"
                );

                p.sendMessage(
                        "§7économique arrive bientôt."
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8• Volume marché"
                );

                p.sendMessage(
                        "§8• Profit total"
                );

                p.sendMessage(
                        "§8• Classement trader"
                );

                p.sendMessage(
                        "§8• Historique économique"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1f,
                        1.1f
                );
            }

            //
            // 🔙 RETOUR
            //

            case 31 -> {

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        0.8f
                );

                MainMenuGUI.open(p);
            }
        }
    }
}