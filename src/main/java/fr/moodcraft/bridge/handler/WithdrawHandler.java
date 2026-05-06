package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.WithdrawGUI;

import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;

import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class WithdrawHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            //
            // 💸 RETRAITS RAPIDES
            //

            case 11 -> withdraw(p, 100);

            case 13 -> withdraw(p, 1000);

            case 15 -> withdraw(p, 10000);

            //
            // 🏦 RETRAIT TOTAL
            //

            case 21 -> withdrawAll(p);

            //
            // ✍️ PERSONNALISÉ
            //

            case 23 -> {

                p.closeInventory();

                AmountInputManager.wait(

                        p,

                        AmountInputManager.Type.WITHDRAW
                );

                InputManager.wait(

                        p,

                        "amount_input"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fRetrait Personnalisé"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Entre le montant"
                );

                p.sendMessage(
                        "§7à retirer dans le chat."
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8Exemple: §e2500"
                );

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        1f
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

                BankGUI.open(p);
            }
        }
    }

    // =========================
    // 💸 RETRAIT NORMAL
    // =========================

    private void withdraw(Player p,
                          double amount) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {

            p.sendMessage(
                    "§cErreur économie Vault."
            );

            return;
        }

        String id =
                p.getUniqueId().toString();

        double bank =
                BankStorage.get(id);

        //
        // ❌ FONDS
        //

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
                    "§7Solde bancaire disponible:"
            );

            p.sendMessage(
                    "§6"
                            + SafeGUI.money(bank)
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
        // 💸 RETRAIT BANQUE
        //

        BankStorage.remove(
                id,
                amount
        );

        //
        // 💰 AJOUT CASH
        //

        eco.depositPlayer(
                p,
                amount
        );

        //
        // 📜 HISTORIQUE
        //

        TransactionManager.withdraw(
                p.getUniqueId(),
                amount
        );

        //
        // 💳 NOUVEAU SOLDE
        //

        double newBank =
                BankStorage.get(id);

        //
        // ✨ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fRetrait effectué"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Montant transféré:"
        );

        p.sendMessage(
                "§c-"
                        + SafeGUI.money(amount)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Nouveau solde bancaire:"
        );

        p.sendMessage(
                "§6"
                        + SafeGUI.money(newBank)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        //
        // 🔊 FEEDBACK
        //

        p.playSound(

                p.getLocation(),

                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,

                1f,

                1.15f
        );

        p.sendTitle(

                "§c-"
                        + SafeGUI.money(amount)
                        + "€",

                "§fRetrait bancaire effectué",

                5,

                35,

                10
        );

        //
        // 🔄 REFRESH
        //

        WithdrawGUI.open(p);
    }

    // =========================
    // 🏦 RETRAIT TOTAL
    // =========================

    private void withdrawAll(Player p) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {

            p.sendMessage(
                    "§cErreur économie Vault."
            );

            return;
        }

        String id =
                p.getUniqueId().toString();

        double bank =
                BankStorage.get(id);

        //
        // ❌ VIDE
        //

        if (bank <= 0) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ Compte bancaire vide"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Aucun fond disponible."
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

                    0.8f
            );

            return;
        }

        //
        // 💸 RESET BANQUE
        //

        BankStorage.remove(
                id,
                bank
        );

        //
        // 💰 CASH
        //

        eco.depositPlayer(
                p,
                bank
        );

        //
        // 📜 HISTORIQUE
        //

        TransactionManager.withdraw(
                p.getUniqueId(),
                bank
        );

        //
        // ✨ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fRetrait total effectué"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Montant transféré:"
        );

        p.sendMessage(
                "§c-"
                        + SafeGUI.money(bank)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Solde bancaire:"
        );

        p.sendMessage(
                "§60€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        //
        // 🔊 FEEDBACK
        //

        p.playSound(

                p.getLocation(),

                Sound.BLOCK_BEACON_DEACTIVATE,

                1f,

                1f
        );

        p.sendTitle(

                "§c-"
                        + SafeGUI.money(bank)
                        + "€",

                "§fTout a été retiré",

                5,

                40,

                10
        );

        //
        // 🔄 REFRESH
        //

        WithdrawGUI.open(p);
    }
}