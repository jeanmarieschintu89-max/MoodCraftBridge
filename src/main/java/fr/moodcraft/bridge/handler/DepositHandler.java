package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.DepositGUI;

import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;

import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class DepositHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            //
            // 💰 DÉPÔTS RAPIDES
            //

            case 11 -> {

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        1.05f
                );

                deposit(p, 100);
            }

            case 13 -> {

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        1.1f
                );

                deposit(p, 1000);
            }

            case 15 -> {

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        1.15f
                );

                deposit(p, 10000);
            }

            //
            // 🏦 TOUT DÉPOSER
            //

            case 21 -> {

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_BEACON_ACTIVATE,
                        1f,
                        1f
                );

                depositAll(p);
            }

            //
            // ✍️ PERSONNALISÉ
            //

            case 23 -> {

                p.closeInventory();

                AmountInputManager.wait(
                        p,
                        AmountInputManager.Type.DEPOSIT
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
                        "§6✦ §fDépôt Personnalisé"
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
                        "§8Exemple: §e1250"
                );

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

    //
    // 💰 DÉPÔT
    //

    private void deposit(Player p, double amount) {

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

        double cash =
                eco.getBalance(p);

        //
        // ❌ FONDS
        //

        if (cash < amount) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ Fonds insuffisants"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Liquidités disponibles:"
            );

            p.sendMessage(
                    "§a"
                            + SafeGUI.money(cash)
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
        // 💸 RETRAIT CASH
        //

        eco.withdrawPlayer(
                p,
                amount
        );

        //
        // 🏦 AJOUT BANQUE
        //

        String id =
                p.getUniqueId().toString();

        double oldBank =
                BankStorage.get(id);

        double newBank =
                oldBank + amount;

        BankStorage.set(
                id,
                newBank
        );

        //
        // 📜 HISTORIQUE
        //

        TransactionManager.deposit(
                p.getUniqueId(),
                amount
        );

        //
        // ✨ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fDépôt effectué"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Montant transféré:"
        );

        p.sendMessage(
                "§a+"
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

                Sound.BLOCK_AMETHYST_BLOCK_CHIME,

                1f,

                1.2f
        );

        p.sendTitle(

                "§a+"
                        + SafeGUI.money(amount)
                        + "€",

                "§fDépôt bancaire effectué",

                5,

                35,

                10
        );

        //
        // 🔄 REFRESH
        //

        DepositGUI.open(p);
    }

    //
    // 🏦 TOUT DÉPOSER
    //

    private void depositAll(Player p) {

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

        double cash =
                eco.getBalance(p);

        //
        // ❌ RIEN
        //

        if (cash <= 0) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ Aucun argent liquide"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Tu n'as rien"
            );

            p.sendMessage(
                    "§7à déposer."
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
        // 💸 RETRAIT
        //

        eco.withdrawPlayer(
                p,
                cash
        );

        //
        // 🏦 AJOUT
        //

        String id =
                p.getUniqueId().toString();

        double newBank =
                BankStorage.get(id) + cash;

        BankStorage.set(
                id,
                newBank
        );

        //
        // 📜 HISTORIQUE
        //

        TransactionManager.deposit(
                p.getUniqueId(),
                cash
        );

        //
        // ✨ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fDépôt total effectué"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Montant transféré:"
        );

        p.sendMessage(
                "§a+"
                        + SafeGUI.money(cash)
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

                Sound.BLOCK_BEACON_POWER_SELECT,

                1f,

                1.1f
        );

        p.sendTitle(

                "§a+"
                        + SafeGUI.money(cash)
                        + "€",

                "§fTout a été déposé",

                5,

                40,

                10
        );

        //
        // 🔄 REFRESH
        //

        DepositGUI.open(p);
    }
}