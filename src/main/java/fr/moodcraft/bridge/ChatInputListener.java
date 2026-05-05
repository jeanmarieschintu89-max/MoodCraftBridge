package fr.moodcraft.bridge;

import fr.moodcraft.bank.BankAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        // 🔥 on utilise UNIQUEMENT AmountInputManager
        if (!AmountInputManager.has(p)) return;

        e.setCancelled(true);

        String input = e.getMessage();

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

            AmountInputManager.Type type = AmountInputManager.getType(p);
            AmountInputManager.clear(p);

            double amount;

            try {
                amount = Double.parseDouble(input.replace(",", "."));
            } catch (Exception ex) {
                error(p, "Montant invalide");
                return;
            }

            if (amount <= 0) {
                error(p, "Montant invalide");
                return;
            }

            switch (type) {

                // =========================
                // 💰 DEPOT
                // =========================
                case DEPOSIT -> handleDeposit(p, amount);

                // =========================
                // 💸 RETRAIT (préparé)
                // =========================
                case WITHDRAW -> handleWithdraw(p, amount);

                // =========================
                // 🔁 VIREMENT (placeholder)
                // =========================
                case PLAYER_TRANSFER -> {
                    p.sendMessage("§c🚧 Transfert en cours de dev");
                }
            }
        });
    }

    // =========================
    // 💰 DEPOT
    // =========================
    private void handleDeposit(Player p, double amount) {

        Economy eco = VaultHook.getEconomy();

        if (eco == null) {
            error(p, "Vault non trouvé");
            return;
        }

        double cash = eco.getBalance(p);

        if (cash < amount) {
            error(p, "Pas assez d'argent");
            return;
        }

        eco.withdrawPlayer(p, amount);
        BankAPI.add(p.getUniqueId().toString(), amount);

        success(p, "Dépôt personnalisé", amount,
                BankAPI.get(p.getUniqueId().toString()));

        DepositGUI.open(p);
    }

    // =========================
    // 💸 RETRAIT
    // =========================
    private void handleWithdraw(Player p, double amount) {

        Economy eco = VaultHook.getEconomy();

        if (eco == null) {
            error(p, "Vault non trouvé");
            return;
        }

        double bank = BankAPI.get(p.getUniqueId().toString());

        if (bank < amount) {
            error(p, "Pas assez d'argent en banque");
            return;
        }

        BankAPI.remove(p.getUniqueId().toString(), amount);
        eco.depositPlayer(p, amount);

        success(p, "Retrait personnalisé", amount,
                BankAPI.get(p.getUniqueId().toString()));

        WithdrawGUI.open(p);
    }

    // =========================
    // ❌ ERROR UI
    // =========================
    private void error(Player p, String msg) {
        p.sendMessage("§c❌ " + msg);
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    // =========================
    // ✅ SUCCESS UI
    // =========================
    private void success(Player p, String title, double amount, double newBalance) {

        p.sendMessage("");
        p.sendMessage("§8╔════════════════════════════╗");
        p.sendMessage("§8║   §a✔ " + title);
        p.sendMessage("§8╠════════════════════════════╣");
        p.sendMessage("§8║ §7Montant: §a+" + SafeGUI.money(amount));
        p.sendMessage("§8║");
        p.sendMessage("§8║ §7Banque: §6" + SafeGUI.money(newBalance));
        p.sendMessage("§8╚════════════════════════════╝");
        p.sendMessage("");

        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }
}