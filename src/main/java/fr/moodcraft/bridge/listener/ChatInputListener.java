package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.IbanManager;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;

import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.TransactionLogger;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInputListener implements Listener {

    private static final double MAX_PERSONAL_TRANSFER =
            10000.0;

    private static final double MAX_DAILY_PERSONAL_TRANSFER =
            25000.0;

    private static final Map<UUID, Double> dailySent =
            new HashMap<>();

    private static final Map<UUID, String> dailyDate =
            new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player p =
                e.getPlayer();

        if (AmountInputManager.has(p)) {

            e.setCancelled(true);

            String msg =
                    e.getMessage();

            Bukkit.getScheduler().runTask(
                    Main.getInstance(),
                    () -> handleAmountInput(
                            p,
                            msg
                    )
            );

            return;
        }

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
                    () -> handleTextInput(
                            p,
                            context,
                            input
                    )
            );
        }
    }

    private void handleAmountInput(
            Player p,
            String msg
    ) {

        AmountInputManager.Type type =
                AmountInputManager.getType(p);

        AmountInputManager.clear(p);

        if (isCancel(msg)) {

            InputManager.clearData(p);
            InputManager.clear(p);

            info(
                    p,
                    "Opération bancaire annulée."
            );

            return;
        }

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            msg.replace(",", ".")
                    );

        } catch (Exception ex) {

            error(
                    p,
                    "Montant invalide."
            );

            return;
        }

        if (amount <= 0) {

            error(
                    p,
                    "Le montant doit être supérieur à zéro."
            );

            return;
        }

        switch (type) {

            case DEPOSIT -> {

                double cash =
                        VaultHook.getBalance(p);

                if (cash < amount) {

                    error(
                            p,
                            "Pas assez d'argent liquide."
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
                        "Dépôt effectué",
                        "§a+" + SafeGUI.money(amount) + "€"
                );
            }

            case WITHDRAW -> {

                String uuid =
                        p.getUniqueId().toString();

                double bank =
                        BankStorage.get(uuid);

                if (bank < amount) {

                    error(
                            p,
                            "Fonds bancaires insuffisants."
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
                        "Retrait effectué",
                        "§a+" + SafeGUI.money(amount) + "€"
                );
            }

            case PLAYER_TRANSFER -> {

                String targetIban =
                        InputManager.getData(p);

                if (targetIban == null) {

                    error(
                            p,
                            "IBAN manquant."
                    );

                    InputManager.clear(p);
                    InputManager.clearData(p);

                    return;
                }

                UUID targetUUID =
                        IbanManager.getOwner(
                                targetIban
                        );

                if (targetUUID == null) {

                    error(
                            p,
                            "IBAN introuvable."
                    );

                    InputManager.clear(p);
                    InputManager.clearData(p);

                    return;
                }

                if (targetUUID.equals(
                        p.getUniqueId()
                )) {

                    error(
                            p,
                            "Auto-virement interdit."
                    );

                    InputManager.clear(p);
                    InputManager.clearData(p);

                    return;
                }

                String targetName =
                        Bukkit.getOfflinePlayer(
                                targetUUID
                        ).getName();

                if (targetName == null) {
                    targetName = "Inconnu";
                }

                if (!canBypassLimit(p)) {

                    if (amount > MAX_PERSONAL_TRANSFER) {

                        TransactionLogger.log(
                                p.getUniqueId().toString(),
                                "IBAN_TRANSFER_BLOCKED_HIGH_AMOUNT",
                                amount,
                                targetName
                        );

                        denyProfessionalTransfer(
                                p,
                                targetName,
                                amount
                        );

                        InputManager.clear(p);
                        InputManager.clearData(p);

                        return;
                    }

                    resetDailyIfNeeded(
                            p.getUniqueId()
                    );

                    double today =
                            dailySent.getOrDefault(
                                    p.getUniqueId(),
                                    0.0
                            );

                    if (today + amount > MAX_DAILY_PERSONAL_TRANSFER) {

                        TransactionLogger.log(
                                p.getUniqueId().toString(),
                                "IBAN_TRANSFER_BLOCKED_DAILY_LIMIT",
                                amount,
                                targetName
                        );

                        denyDailyLimit(
                                p,
                                targetName,
                                today,
                                amount
                        );

                        InputManager.clear(p);
                        InputManager.clearData(p);

                        return;
                    }
                }

                String uuid =
                        p.getUniqueId().toString();

                double bank =
                        BankStorage.get(uuid);

                if (bank < amount) {

                    error(
                            p,
                            "Fonds bancaires insuffisants."
                    );

                    InputManager.clear(p);
                    InputManager.clearData(p);

                    return;
                }

                Player target =
                        Bukkit.getPlayer(targetUUID);

                BankStorage.remove(
                        uuid,
                        amount
                );

                BankStorage.add(
                        targetUUID.toString(),
                        amount
                );

                TransactionManager.transfer(
                        p.getUniqueId(),
                        targetUUID,
                        amount
                );

                if (!canBypassLimit(p)) {

                    resetDailyIfNeeded(
                            p.getUniqueId()
                    );

                    dailySent.put(
                            p.getUniqueId(),
                            dailySent.getOrDefault(
                                    p.getUniqueId(),
                                    0.0
                            ) + amount
                    );
                }

                TransactionLogger.log(
                        p.getUniqueId().toString(),
                        "IBAN_TRANSFER_SENT",
                        amount,
                        targetName
                );

                TransactionLogger.log(
                        targetUUID.toString(),
                        "IBAN_TRANSFER_RECEIVED",
                        amount,
                        p.getName()
                );

                header(p);

                p.sendMessage(successLine("Virement effectué."));
                p.sendMessage("");
                p.sendMessage(detail("Destinataire : §e" + targetName));
                p.sendMessage(detail("Montant : §c-" + SafeGUI.money(amount) + "€"));

                footer(p);

                p.playSound(
                        p.getLocation(),
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        1f,
                        1.2f
                );

                if (target != null && target.isOnline()) {

                    header(target);

                    target.sendMessage(successLine("Virement reçu."));
                    target.sendMessage("");
                    target.sendMessage(detail("Expéditeur : §e" + p.getName()));
                    target.sendMessage(detail("Montant : §a+" + SafeGUI.money(amount) + "€"));

                    footer(target);

                    target.playSound(
                            target.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            1f,
                            1.2f
                    );
                }

                InputManager.clear(p);
                InputManager.clearData(p);
            }
        }

        BankGUI.open(p);
    }

    private void handleTextInput(
            Player p,
            String context,
            String input
    ) {

        if (isCancel(input)) {

            InputManager.clear(p);
            InputManager.clearData(p);

            info(
                    p,
                    "Saisie annulée."
            );

            return;
        }

        if (context.equals("set_iban")) {

            if (!input.startsWith("FR")
                    || input.length() < 10) {

                error(
                        p,
                        "IBAN invalide."
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
                        "Cet IBAN est déjà utilisé."
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

        if (context.equals("transfer_iban")) {

            UUID target =
                    IbanManager.getOwner(input);

            if (target == null) {

                error(
                        p,
                        "IBAN introuvable."
                );

                return;
            }

            if (target.equals(
                    p.getUniqueId()
            )) {

                error(
                        p,
                        "Auto-virement interdit."
                );

                return;
            }

            String targetName =
                    Bukkit.getOfflinePlayer(
                            target
                    ).getName();

            if (targetName == null) {
                targetName = "Inconnu";
            }

            InputManager.setData(
                    p,
                    input
            );

            AmountInputManager.wait(
                    p,
                    AmountInputManager.Type.PLAYER_TRANSFER
            );

            header(p);

            p.sendMessage(successLine("IBAN détecté."));
            p.sendMessage("");
            p.sendMessage(detail("Destinataire : §e" + targetName));
            p.sendMessage("");
            p.sendMessage(info("Écris maintenant le montant."));
            p.sendMessage("");
            p.sendMessage(detail("Exemple : §e5000"));
            p.sendMessage(detail("Tape §cannuler §7pour quitter."));

            footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.2f
            );
        }
    }

    private void denyProfessionalTransfer(
            Player p,
            String targetName,
            double amount
    ) {

        header(p);

        p.sendMessage(errorLine("Virement refusé."));
        p.sendMessage("");
        p.sendMessage(detail("Destinataire : §e" + targetName));
        p.sendMessage(detail("Montant : §e" + SafeGUI.money(amount) + "€"));
        p.sendMessage(detail("Limite personnelle : §e" + SafeGUI.money(MAX_PERSONAL_TRANSFER) + "€"));
        p.sendMessage("");
        p.sendMessage(info("Paiement professionnel conseillé : §e/contrat"));
        p.sendMessage("");
        p.sendMessage(detail("Argent bloqué"));
        p.sendMessage(detail("Taxe 20%"));
        p.sendMessage(detail("Logs gardés"));

        footer(p);

        fail(p);
    }

    private void denyDailyLimit(
            Player p,
            String targetName,
            double already,
            double amount
    ) {

        header(p);

        p.sendMessage(errorLine("Virement refusé."));
        p.sendMessage("");
        p.sendMessage(detail("Destinataire : §e" + targetName));
        p.sendMessage(detail("Déjà envoyé : §e" + SafeGUI.money(already) + "€"));
        p.sendMessage(detail("Montant : §e" + SafeGUI.money(amount) + "€"));
        p.sendMessage(detail("Limite jour : §e" + SafeGUI.money(MAX_DAILY_PERSONAL_TRANSFER) + "€"));
        p.sendMessage("");
        p.sendMessage(info("Paiement important conseillé : §e/contrat"));

        footer(p);

        fail(p);
    }

    private void error(
            Player p,
            String msg
    ) {

        header(p);

        p.sendMessage(errorLine("Action refusée."));
        p.sendMessage("");
        p.sendMessage(detail(msg));

        footer(p);

        fail(p);
    }

    private void success(
            Player p,
            String title,
            String value
    ) {

        header(p);

        p.sendMessage(successLine(title + "."));
        p.sendMessage("");
        p.sendMessage(detail(value));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f,
                1.2f
        );
    }

    private void info(
            Player p,
            String message
    ) {

        header(p);

        p.sendMessage(infoLine(message));

        footer(p);
    }

    private void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque ✦ §8-----");
        p.sendMessage("");
    }

    private void footer(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8-----------------------------");
        p.sendMessage("");
    }

    private String info(String text) {
        return infoLine(text);
    }

    private String infoLine(String text) {
        return "§e➜ §f" + text;
    }

    private String detail(String text) {
        return "§8• §7" + text;
    }

    private String successLine(String text) {
        return "§a✔ §f" + text;
    }

    private String errorLine(String text) {
        return "§c✖ §f" + text;
    }

    private boolean isCancel(
            String text
    ) {

        return text.equalsIgnoreCase("annuler")
                || text.equalsIgnoreCase("cancel");
    }

    private void resetDailyIfNeeded(
            UUID uuid
    ) {

        String today =
                LocalDate.now().toString();

        String stored =
                dailyDate.get(uuid);

        if (!today.equals(stored)) {

            dailyDate.put(
                    uuid,
                    today
            );

            dailySent.put(
                    uuid,
                    0.0
            );
        }
    }

    private boolean canBypassLimit(
            Player p
    ) {

        return p.hasPermission("moodcraftbridge.transfer.bypass")
                || p.hasPermission("moodbusiness.bypass");
    }

    private void fail(
            Player p
    ) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                1f
        );
    }
}
