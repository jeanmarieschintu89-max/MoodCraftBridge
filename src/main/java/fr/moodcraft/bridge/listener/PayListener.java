package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.util.TransactionLogger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayListener implements Listener {

    private static final Map<UUID, Double> lastBalance = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrePay(PlayerCommandPreprocessEvent e) {

        String msg = normalize(e.getMessage());
        if (!msg.startsWith("/pay ")) return;

        Economy eco = VaultHook.getEconomy();
        if (eco == null) return;

        Player p = e.getPlayer();

        // 📌 stocke AVANT
        lastBalance.put(p.getUniqueId(), eco.getBalance(p));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPay(PlayerCommandPreprocessEvent e) {

        String msg = normalize(e.getMessage());
        if (!msg.startsWith("/pay ")) return;

        Player sender = e.getPlayer();
        Economy eco = VaultHook.getEconomy();
        if (eco == null) return;

        Double before = lastBalance.remove(sender.getUniqueId());
        if (before == null) return;

        // 🔥 IMPORTANT → attendre que Vault applique le paiement
        Bukkit.getScheduler().runTaskLater(
                fr.moodcraft.bridge.Main.getInstance(),
                () -> handlePay(sender, msg, before, eco),
                1L // 1 tick après
        );
    }

    private void handlePay(Player sender, String msg, double before, Economy eco) {

        String[] args = msg.split(" ");
        if (args.length < 3) return;

        String targetName = args[1];

        double amount;
        try {
            amount = Double.parseDouble(args[2].replace(",", "."));
        } catch (Exception e) {
            return;
        }

        if (amount <= 0) return;

        double after = eco.getBalance(sender);
        double real = before - after;

        // 🔒 sécurité
        if (real <= 0) return;

        Player target = Bukkit.getPlayerExact(targetName);
        String targetFinal = target != null ? target.getName() : targetName;

        // 🧾 LOGS PROPRES
        TransactionLogger.log(
                sender.getUniqueId().toString(),
                "PAY_SENT",
                real,
                targetFinal
        );

        if (target != null) {
            TransactionLogger.log(
                    target.getUniqueId().toString(),
                    "PAY_RECEIVED",
                    real,
                    sender.getName()
            );
        }
    }

    // =========================
    // 🔧 NORMALISATION
    // =========================
    private String normalize(String raw) {
        if (raw == null) return "";
        return raw.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}