package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.IbanManager; // 🔥 IMPORT IMPORTANT
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

public class ChatInputListener implements Listener {

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
        // 💳 IBAN INPUT (ACTIF 🔥)
        // =========================
        if (InputManager.has(p)) {

            e.setCancelled(true);

            String input = e.getMessage().replace(" ", "").toUpperCase();

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

                // ❌ validation simple
                if (!input.startsWith("FR") || input.length() < 10) {
                    error(p, "IBAN invalide");
                    InputManager.remove(p);
                    return;
                }

                // 🔒 vérification unicité + save
                boolean ok = IbanManager.set(p.getUniqueId(), input);

                if (!ok) {
                    error(p, "Cet IBAN est déjà utilisé !");
                    InputManager.remove(p);
                    return;
                }

                p.sendMessage("§a✔ IBAN enregistré: §e" + input);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                // 🔥 sortie mode input
                InputManager.remove(p);
            });

            return;
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