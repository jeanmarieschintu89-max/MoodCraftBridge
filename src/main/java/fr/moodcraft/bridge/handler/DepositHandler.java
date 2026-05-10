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

            case 10 -> deposit(p, 100);

            case 12 -> deposit(p, 1000);

            case 14 -> deposit(p, 10000);

            case 16 -> depositAll(p);

            case 22 -> {

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
                p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                p.sendMessage("§7Entre le montant à déposer.");
                p.sendMessage("§8Exemple: §e1250");
                p.sendMessage("");

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        1.2f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.1f
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

    private void deposit(Player p, double amount) {

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

        double cash =
                eco.getBalance(p);

        if (cash < amount) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Banque MoodCraft §8-----");
            p.sendMessage("§cFonds insuffisants.");
            p.sendMessage("§7Liquide: §a" + SafeGUI.money(cash) + "€");
            p.sendMessage("");

            fail(p);

            return;
        }

        eco.withdrawPlayer(
                p,
                amount
        );

        String id =
                p.getUniqueId().toString();

        double newBank =
                BankStorage.get(id) + amount;

        BankStorage.set(
                id,
                newBank
        );

        TransactionManager.deposit(
                p.getUniqueId(),
                amount
        );

        p.sendMessage("");
        p.sendMessage("§8----- §6Banque MoodCraft §8-----");
        p.sendMessage("§a✔ Dépôt effectué");
        p.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
        p.sendMessage("§7Banque: §6" + SafeGUI.money(newBank) + "€");
        p.sendMessage("");

        success(
                p,
                "§a+" + SafeGUI.money(amount) + "€",
                "§fDépôt effectué"
        );

        DepositGUI.open(p);
    }

    private void depositAll(Player p) {

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

        double cash =
                eco.getBalance(p);

        if (cash <= 0) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Banque MoodCraft §8-----");
            p.sendMessage("§cAucun argent liquide à déposer.");
            p.sendMessage("");

            fail(p);

            return;
        }

        eco.withdrawPlayer(
                p,
                cash
        );

        String id =
                p.getUniqueId().toString();

        double newBank =
                BankStorage.get(id) + cash;

        BankStorage.set(
                id,
                newBank
        );

        TransactionManager.deposit(
                p.getUniqueId(),
                cash
        );

        p.sendMessage("");
        p.sendMessage("§8----- §6Banque MoodCraft §8-----");
        p.sendMessage("§a✔ Dépôt total effectué");
        p.sendMessage("§7Montant: §a+" + SafeGUI.money(cash) + "€");
        p.sendMessage("§7Banque: §6" + SafeGUI.money(newBank) + "€");
        p.sendMessage("");

        success(
                p,
                "§a+" + SafeGUI.money(cash) + "€",
                "§fTout a été déposé"
        );

        DepositGUI.open(p);
    }

    private void success(
            Player p,
            String title,
            String subtitle
    ) {

        premiumClick(
                p,
                Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                1.25f,
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
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