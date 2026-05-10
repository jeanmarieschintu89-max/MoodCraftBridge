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

            case 10 -> withdraw(p, 100);

            case 12 -> withdraw(p, 1000);

            case 14 -> withdraw(p, 10000);

            case 16 -> withdrawAll(p);

            case 22 -> {

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
                p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                p.sendMessage("§7Entre le montant à retirer.");
                p.sendMessage("§8Exemple: §e2500");
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        1.1f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.0f
                );
            }

            case 31 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                BankGUI.open(p);
            }
        }
    }

    private void withdraw(Player p, double amount) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {

            p.sendMessage("§cErreur économie Vault.");
            return;
        }

        String id =
                p.getUniqueId().toString();

        double bank =
                BankStorage.get(id);

        if (bank < amount) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Banque MoodCraft §8-----");
            p.sendMessage("§cSolde bancaire insuffisant.");
            p.sendMessage("§7Banque: §6" + SafeGUI.money(bank) + "€");
            p.sendMessage("");

            fail(p);

            return;
        }

        BankStorage.remove(
                id,
                amount
        );

        eco.depositPlayer(
                p,
                amount
        );

        TransactionManager.withdraw(
                p.getUniqueId(),
                amount
        );

        double newBank =
                BankStorage.get(id);

        p.sendMessage("");
        p.sendMessage("§8----- §6Banque MoodCraft §8-----");
        p.sendMessage("§a✔ Retrait effectué");
        p.sendMessage("§7Montant: §c-" + SafeGUI.money(amount) + "€");
        p.sendMessage("§7Banque: §6" + SafeGUI.money(newBank) + "€");
        p.sendMessage("");

        success(
                p,
                "§c-" + SafeGUI.money(amount) + "€",
                "§fRetrait effectué"
        );

        WithdrawGUI.open(p);
    }

    private void withdrawAll(Player p) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {

            p.sendMessage("§cErreur économie Vault.");
            return;
        }

        String id =
                p.getUniqueId().toString();

        double bank =
                BankStorage.get(id);

        if (bank <= 0) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Banque MoodCraft §8-----");
            p.sendMessage("§cTon compte bancaire est vide.");
            p.sendMessage("");

            fail(p);

            return;
        }

        BankStorage.remove(
                id,
                bank
        );

        eco.depositPlayer(
                p,
                bank
        );

        TransactionManager.withdraw(
                p.getUniqueId(),
                bank
        );

        p.sendMessage("");
        p.sendMessage("§8----- §6Banque MoodCraft §8-----");
        p.sendMessage("§a✔ Retrait total effectué");
        p.sendMessage("§7Montant: §c-" + SafeGUI.money(bank) + "€");
        p.sendMessage("§7Banque: §60€");
        p.sendMessage("");

        success(
                p,
                "§c-" + SafeGUI.money(bank) + "€",
                "§fTout a été retiré"
        );

        WithdrawGUI.open(p);
    }

    private void success(
            Player p,
            String title,
            String subtitle
    ) {

        premiumClick(
                p,
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1.2f,
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                1.4f
        );

        p.sendTitle(
                title,
                subtitle,
                5,
                35,
                10
        );
    }

    private void fail(Player p) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    private void premiumClick(
            Player p,
            Sound main,
            float mainPitch,
            Sound second,
            float secondPitch
    ) {

        p.playSound(
                p.getLocation(),
                main,
                0.75f,
                mainPitch
        );

        p.playSound(
                p.getLocation(),
                second,
                0.35f,
                secondPitch
        );
    }
}