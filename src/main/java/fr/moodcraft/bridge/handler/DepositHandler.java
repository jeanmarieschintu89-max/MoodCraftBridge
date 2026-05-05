package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.DepositGUI;
import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;
import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.hook.VaultHook;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DepositHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            case 11 -> deposit(p, 100);
            case 13 -> deposit(p, 1000);
            case 15 -> deposit(p, 10000);

            case 20 -> depositAll(p);

            case 24 -> {
                p.closeInventory();

                AmountInputManager.wait(p, AmountInputManager.Type.DEPOSIT);
                InputManager.wait(p, "amount_input");

                p.sendMessage("§eEntre le montant dans le chat.");
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }

            case 22 -> BankGUI.open(p);
        }
    }

    private void deposit(Player p, double amount) {

        if (ActionLock.isLocked(p.getUniqueId(), 500)) return;

        Economy eco = VaultHook.getEconomy();
        if (eco == null) {
            p.sendMessage("§cErreur Vault");
            return;
        }

        double cash = eco.getBalance(p);

        if (cash < amount) {
            p.sendMessage("§cFonds insuffisants");
            return;
        }

        eco.withdrawPlayer(p, amount);

        String id = p.getUniqueId().toString();
        BankStorage.set(id, BankStorage.get(id) + amount);

        p.sendMessage("§a+ " + SafeGUI.money(amount) + "€ en banque");

        DepositGUI.open(p);
    }

    private void depositAll(Player p) {

        if (ActionLock.isLocked(p.getUniqueId(), 500)) return;

        Economy eco = VaultHook.getEconomy();
        if (eco == null) {
            p.sendMessage("§cErreur Vault");
            return;
        }

        double cash = eco.getBalance(p);

        if (cash <= 0) {
            p.sendMessage("§cAucun argent");
            return;
        }

        eco.withdrawPlayer(p, cash);

        String id = p.getUniqueId().toString();
        BankStorage.set(id, BankStorage.get(id) + cash);

        p.sendMessage("§aTout déposé (" + SafeGUI.money(cash) + "€)");

        DepositGUI.open(p);
    }
}