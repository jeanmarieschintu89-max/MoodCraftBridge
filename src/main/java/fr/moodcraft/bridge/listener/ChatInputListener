package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInputListener implements Listener {

    private static final Map<UUID, String> ibanCache = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        // =========================
        // 💰 AMOUNT INPUT
        // =========================
        if (AmountInputManager.has(p)) {

            e.setCancelled(true);

            String msg = e.getMessage();

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

                AmountInputManager.Type type = AmountInputManager.getType(p);
                AmountInputManager.clear(p);

                double amount;

                try {
                    amount = Double.parseDouble(msg.replace(",", "."));
                } catch (Exception ex) {
                    error(p, "Nombre invalide");
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
                    case DEPOSIT -> {

                        double cash = VaultHook.getBalance(p);

                        if (cash < amount) {
                            error(p, "Pas assez d'argent");
                            return;
                        }

                        VaultHook.remove(p, amount);
                        BankStorage.add(p.getUniqueId().toString(), amount);

                        success(p, "Dépôt", amount);
                    }

                    // =========================
                    // 💸 RETRAIT
                    // =========================
                    case WITHDRAW -> {

                        String uuid = p.getUniqueId().toString();
                        double bank = BankStorage.get(uuid);

                        if (bank < amount) {
                            error(p, "Fonds insuffisants");
                            return;
                        }

                        BankStorage.remove(uuid, amount);
                        VaultHook.add(p, amount);

                        success(p, "Retrait", amount);
                    }
                }

                BankGUI.open(p);
            });

            return;
        }

        // =========================
        // 💳 IBAN INPUT (TEMPORAIREMENT OFF)
        // =========================
        if (InputManager.has(p)) {

            e.setCancelled(true);

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

                p.sendMessage("§c❌ Système IBAN en cours de reconstruction");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            });
        }
    }

    private void error(Player p, String msg) {
        p.sendMessage("§c❌ " + msg);
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    private void success(Player p, String type, double amount) {
        p.sendMessage("§a✔ " + type + ": §e" + SafeGUI.money(amount) + "€");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }
}