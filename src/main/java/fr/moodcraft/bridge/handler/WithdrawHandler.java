package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.WithdrawGUI;

import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import fr.moodcraft.bridge.bank.BankStorage;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WithdrawHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            case 11 -> withdraw(p, 100);

            case 13 -> withdraw(p, 1000);

            case 15 -> withdraw(p, 10000);

            case 20 -> withdrawAll(p);

            case 24 -> {

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
                        "§6✦ §fRetrait bancaire"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Entre le montant à retirer"
                );

                p.sendMessage(
                        "§7directement dans le chat."
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
                        1f
                );
            }

            case 22 -> BankGUI.open(p);
        }
    }

    // =========================
    // 💸 RETRAIT NORMAL
    // =========================

    private void withdraw(Player p,
                          double amount) {

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null)
            return;

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        //
        // ❌ FONDS
        //

        if (bank < amount) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fBanque MoodCraft"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Fonds insuffisants."
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
                    1f
            );

            return;
        }

        //
        // 💸 RETRAIT
        //

        BankStorage.remove(
                p.getUniqueId().toString(),
                amount
        );

        eco.depositPlayer(
                p,
                amount
        );

        //
        // ✅ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fTransaction bancaire"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Type: §eRetrait"
        );

        p.sendMessage(
                "§7Montant: §c-"
                        + SafeGUI.money(amount)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Solde restant: §a"
                        + SafeGUI.money(
                                BankStorage.get(
                                        p.getUniqueId().toString()
                                )
                        )
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f,
                1f
        );

        WithdrawGUI.open(p);
    }

    // =========================
    // 🔥 RETRAIT MAX
    // =========================

    private void withdrawAll(Player p) {

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null)
            return;

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        //
        // ❌ VIDE
        //

        if (bank <= 0) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fBanque MoodCraft"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Tu n'as rien en banque."
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
                    1f
            );

            return;
        }

        //
        // 💸 RETRAIT TOTAL
        //

        BankStorage.remove(
                p.getUniqueId().toString(),
                bank
        );

        eco.depositPlayer(
                p,
                bank
        );

        //
        // ✅ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fTransaction bancaire"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Type: §eRetrait total"
        );

        p.sendMessage(
                "§7Montant: §c-"
                        + SafeGUI.money(bank)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Solde restant: §a0€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f,
                1f
        );

        WithdrawGUI.open(p);
    }
}