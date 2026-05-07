package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.IbanManager;
import fr.moodcraft.bridge.bank.TransactionManager;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.contract.Contract;

import fr.moodcraft.bridge.manager.ContractCreationManager;
import fr.moodcraft.bridge.manager.ContractManager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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

            String msg =
                    e.getMessage();

            Bukkit.getScheduler().runTask(
                    Main.getInstance(),

                    () -> {

                        AmountInputManager.Type type =
                                AmountInputManager.getType(p);

                        AmountInputManager.clear(p);

                        double amount;

                        try {

                            amount =
                                    Double.parseDouble(
                                            msg.replace(",", ".")
                                    );

                        } catch (Exception ex) {

                            error(
                                    p,
                                    "Nombre invalide"
                            );

                            return;
                        }

                        if (amount <= 0) {

                            error(
                                    p,
                                    "Montant invalide"
                            );

                            return;
                        }

                        switch (type) {

                            //
                            // 💰 DEPOT
                            //

                            case DEPOSIT -> {

                                double cash =
                                        VaultHook.getBalance(p);

                                if (cash < amount) {

                                    error(
                                            p,
                                            "Pas assez d'argent"
                                    );

                                    return;
                                }

                                VaultHook.remove(
                                        p,
                                        amount
                                );

                                BankStorage.add(
                                        p.getUniqueId().toString(),
                                        amount
                                );

                                TransactionManager.deposit(
                                        p.getUniqueId(),
                                        amount
                                );

                                success(
                                        p,
                                        "Dépôt",
                                        "+" + SafeGUI.money(amount)
                                );
                            }

                            //
                            // 💸 RETRAIT
                            //

                            case WITHDRAW -> {

                                String uuid =
                                        p.getUniqueId().toString();

                                double bank =
                                        BankStorage.get(uuid);

                                if (bank < amount) {

                                    error(
                                            p,
                                            "Fonds insuffisants"
                                    );

                                    return;
                                }

                                BankStorage.remove(
                                        uuid,
                                        amount
                                );

                                VaultHook.add(
                                        p,
                                        amount
                                );

                                TransactionManager.withdraw(
                                        p.getUniqueId(),
                                        amount
                                );

                                success(
                                        p,
                                        "Retrait",
                                        "-" + SafeGUI.money(amount)
                                );
                            }

                            //
                            // 💸 VIREMENT
                            //

                            case PLAYER_TRANSFER -> {

                                String targetIban =
                                        InputManager.getData(p);

                                if (targetIban == null) {

                                    error(
                                            p,
                                            "IBAN manquant"
                                    );

                                    return;
                                }

                                var targetUUID =
                                        IbanManager.getOwner(
                                                targetIban
                                        );

                                if (targetUUID == null) {

                                    error(
                                            p,
                                            "IBAN introuvable"
                                    );

                                    return;
                                }

                                if (targetUUID.equals(
                                        p.getUniqueId()
                                )) {

                                    error(
                                            p,
                                            "Auto-virement interdit"
                                    );

                                    return;
                                }

                                String uuid =
                                        p.getUniqueId().toString();

                                double bank =
                                        BankStorage.get(uuid);

                                if (bank < amount) {

                                    error(
                                            p,
                                            "Fonds insuffisants"
                                    );

                                    return;
                                }

                                Player target =
                                        Bukkit.getPlayer(targetUUID);

                                //
                                // 💸 RETRAIT BANQUE
                                //

                                BankStorage.remove(
                                        uuid,
                                        amount
                                );

                                //
                                // 💰 AJOUT RECEVEUR
                                //

                                BankStorage.add(
                                        targetUUID.toString(),
                                        amount
                                );

                                TransactionManager.transfer(
                                        p.getUniqueId(),
                                        targetUUID,
                                        amount
                                );

                                //
                                // ✨ ENVOYEUR
                                //

                                p.sendMessage("");

                                p.sendMessage(
                                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                                );

                                p.sendMessage(
                                        "§6✦ §fBanque MoodCraft"
                                );

                                p.sendMessage("");

                                p.sendMessage(
                                        "§a✔ §fVirement effectué"
                                );

                                p.sendMessage("");

                                p.sendMessage(
                                        "§7Montant envoyé: §c-"
                                                + SafeGUI.money(amount)
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
                                        1.2f
                                );

                                //
                                // ✨ RECEVEUR
                                //

                                if (target != null
                                        && target.isOnline()) {

                                    target.sendMessage("");

                                    target.sendMessage(
                                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                                    );

                                    target.sendMessage(
                                            "§6✦ §fBanque MoodCraft"
                                    );

                                    target.sendMessage("");

                                    target.sendMessage(
                                            "§a✔ §fVirement reçu"
                                    );

                                    target.sendMessage("");

                                    target.sendMessage(
                                            "§7Montant reçu: §a+"
                                                    + SafeGUI.money(amount)
                                    );

                                    target.sendMessage("");

                                    target.sendMessage(
                                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                                    );

                                    target.sendMessage("");

                                    target.playSound(
                                            target.getLocation(),
                                            Sound.BLOCK_NOTE_BLOCK_PLING,
                                            1f,
                                            1.2f
                                    );
                                }

                                InputManager.clearData(p);
                            }
                        }

                        BankGUI.open(p);
                    }
            );

            return;
        }

        // =========================
        // 💳 INPUT
        // =========================

        if (InputManager.has(p)) {

            e.setCancelled(true);

            String input =
                    e.getMessage()
                            .replace(" ", "")
                            .toUpperCase();

            String context =
                    InputManager.get(p);

            Bukkit.getScheduler().runTask(
                    Main.getInstance(),

                    () -> {

                        //
                        // 🏦 SET IBAN
                        //

                        if (context.equals("set_iban")) {

                            if (!input.startsWith("FR")
                                    || input.length() < 10) {

                                error(
                                        p,
                                        "IBAN invalide"
                                );

                                InputManager.clear(p);

                                return;
                            }

                            boolean ok =
                                    IbanManager.set(
                                            p.getUniqueId(),
                                            input
                                    );

                            if (!ok) {

                                error(
                                        p,
                                        "Cet IBAN est déjà utilisé !"
                                );

                                InputManager.clear(p);

                                return;
                            }

                            success(
                                    p,
                                    "IBAN enregistré",
                                    input
                            );

                            InputManager.clear(p);

                            return;
                        }

                        //
                        // 💸 VIREMENT
                        //

                        if (context.equals("transfer_iban")) {

                            var target =
                                    IbanManager.getOwner(input);

                            if (target == null) {

                                error(
                                        p,
                                        "IBAN introuvable"
                                );

                                return;
                            }

                            if (target.equals(
                                    p.getUniqueId()
                            )) {

                                error(
                                        p,
                                        "Auto-virement interdit"
                                );

                                return;
                            }

                            InputManager.setData(
                                    p,
                                    input
                            );

                            AmountInputManager.wait(
                                    p,
                                    AmountInputManager.Type.PLAYER_TRANSFER
                            );

                            p.sendMessage("");

                            p.sendMessage(
                                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                            );

                            p.sendMessage(
                                    "§6✦ §fBanque MoodCraft"
                            );

                            p.sendMessage("");

                            p.sendMessage(
                                    "§7IBAN détecté avec succès."
                            );

                            p.sendMessage(
                                    "§7Destinataire vérifié."
                            );

                            p.sendMessage("");

                            p.sendMessage(
                                    "§eEntre maintenant le montant"
                            );

                            p.sendMessage(
                                    "§7dans le chat."
                            );

                            p.sendMessage("");

                            p.sendMessage(
                                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                            );

                            p.sendMessage("");

                            p.playSound(
                                    p.getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_PLING,
                                    1f,
                                    1.2f
                            );
                        }
                    }
            );

            return;
        }
    }

    // =========================
    // ❌ ERROR
    // =========================

    private void error(Player p,
                       String msg) {

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§c✦ §fBanque MoodCraft"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7" + msg
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
    }

    // =========================
    // ✅ SUCCESS
    // =========================

    private void success(Player p,
                         String type,
                         String value) {

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fBanque MoodCraft"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7" + type + ": §e" + value
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
                1.15f
        );
    }
}