package fr.moodcraft.bridge;

import fr.moodcraft.bank.BankAPI;
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

                p.sendMessage("");
                p.sendMessage("§8╔════════════════════════════╗");
                p.sendMessage("§8║   §e💰 Dépôt personnalisé");
                p.sendMessage("§8╠════════════════════════════╣");
                p.sendMessage("§8║ §7Entre le montant à déposer");
                p.sendMessage("§8║ §7directement dans le chat");
                p.sendMessage("§8╚════════════════════════════╝");
                p.sendMessage("");

                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }

            case 22 -> BankGUI.open(p);
        }
    }

    // =========================
    // 💰 DÉPÔT NORMAL
    // =========================
    private void deposit(Player p, double amount) {

        // 🔒 anti spam / double clic
        if (ActionLock.isLocked(p.getUniqueId(), 500)) return;

        Economy eco = VaultHook.getEconomy();
        if (eco == null) {
            p.sendMessage("§c❌ Erreur économie (Vault)");
            return;
        }

        double cash = eco.getBalance(p);

        if (cash < amount) {
            p.sendMessage("");
            p.sendMessage("§8╔════════════════════════════╗");
            p.sendMessage("§8║   §c✖ Fonds insuffisants");
            p.sendMessage("§8╠════════════════════════════╣");
            p.sendMessage("§8║ §7Liquide: §c" + SafeGUI.money(cash));
            p.sendMessage("§8║ §7Demandé: §e" + SafeGUI.money(amount));
            p.sendMessage("§8╚════════════════════════════╝");
            p.sendMessage("");

            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        // 💸 retrait cash
        eco.withdrawPlayer(p, amount);

        // 🏦 dépôt banque via API
        BankAPI.add(p.getUniqueId().toString(), amount);

        double newBalance = BankAPI.get(p.getUniqueId().toString());

        p.sendMessage("");
        p.sendMessage("§8╔════════════════════════════╗");
        p.sendMessage("§8║   §a✔ Dépôt effectué");
        p.sendMessage("§8╠════════════════════════════╣");
        p.sendMessage("§8║ §7Montant: §a+" + SafeGUI.money(amount));
        p.sendMessage("§8║");
        p.sendMessage("§8║ §7Banque: §6" + SafeGUI.money(newBalance));
        p.sendMessage("§8╚════════════════════════════╝");
        p.sendMessage("");

        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        DepositGUI.open(p);
    }

    // =========================
    // 🔥 DÉPÔT MAX
    // =========================
    private void depositAll(Player p) {

        if (ActionLock.isLocked(p.getUniqueId(), 500)) return;

        Economy eco = VaultHook.getEconomy();
        if (eco == null) {
            p.sendMessage("§c❌ Erreur économie (Vault)");
            return;
        }

        double cash = eco.getBalance(p);

        if (cash <= 0) {
            p.sendMessage("");
            p.sendMessage("§8╔════════════════════════════╗");
            p.sendMessage("§8║   §c✖ Aucun argent");
            p.sendMessage("§8╠════════════════════════════╣");
            p.sendMessage("§8║ §7Tu n'as rien à déposer");
            p.sendMessage("§8╚════════════════════════════╝");
            p.sendMessage("");

            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        eco.withdrawPlayer(p, cash);

        BankAPI.add(p.getUniqueId().toString(), cash);

        double newBalance = BankAPI.get(p.getUniqueId().toString());

        p.sendMessage("");
        p.sendMessage("§8╔════════════════════════════╗");
        p.sendMessage("§8║   §a✔ Dépôt total");
        p.sendMessage("§8╠════════════════════════════╣");
        p.sendMessage("§8║ §7Montant: §a+" + SafeGUI.money(cash));
        p.sendMessage("§8║");
        p.sendMessage("§8║ §7Banque: §6" + SafeGUI.money(newBalance));
        p.sendMessage("§8╚════════════════════════════╝");
        p.sendMessage("");

        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        DepositGUI.open(p);
    }
}