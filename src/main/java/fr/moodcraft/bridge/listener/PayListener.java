package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.util.TransactionLogger;
import fr.moodcraft.bridge.util.VaultHook;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayListener
        implements Listener {

    //
    // 🔒 LIMITES ANTI-FRAUDE
    //

    private static final double MAX_PERSONAL_PAY =
            10000.0;

    private static final double MAX_DAILY_PERSONAL_PAY =
            25000.0;

    //
    // 📦 CACHE
    //

    private static final Map<UUID, Double> lastBalance =
            new HashMap<>();

    private static final Map<UUID, Double> dailySent =
            new HashMap<>();

    private static final Map<UUID, String> dailyDate =
            new HashMap<>();

    //
    // 🔒 PRE-CHECK /PAY
    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrePay(
            PlayerCommandPreprocessEvent e
    ) {

        String raw =
                normalizeRaw(
                        e.getMessage()
                );

        if (!isPayCommand(raw)) {
            return;
        }

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {
            return;
        }

        Player p =
                e.getPlayer();

        PayData data =
                parsePay(raw);

        if (data == null) {
            return;
        }

        if (p.hasPermission("moodcraftbridge.pay.bypass")
                || p.hasPermission("moodbusiness.bypass")) {

            lastBalance.put(
                    p.getUniqueId(),
                    eco.getBalance(p)
            );

            return;
        }

        if (data.amount() > MAX_PERSONAL_PAY) {

            e.setCancelled(true);

            denyProfessionalPayment(
                    p,
                    data.amount()
            );

            TransactionLogger.log(
                    p.getUniqueId().toString(),
                    "PAY_BLOCKED_HIGH_AMOUNT",
                    data.amount(),
                    data.targetName()
            );

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

        if (today + data.amount() > MAX_DAILY_PERSONAL_PAY) {

            e.setCancelled(true);

            denyDailyLimit(
                    p,
                    today,
                    data.amount()
            );

            TransactionLogger.log(
                    p.getUniqueId().toString(),
                    "PAY_BLOCKED_DAILY_LIMIT",
                    data.amount(),
                    data.targetName()
            );

            return;
        }

        //
        // 📌 Stocke AVANT paiement
        //

        lastBalance.put(
                p.getUniqueId(),
                eco.getBalance(p)
        );
    }

    //
    // 🧾 LOG APRÈS /PAY
    //

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onPay(
            PlayerCommandPreprocessEvent e
    ) {

        String raw =
                normalizeRaw(
                        e.getMessage()
                );

        if (!isPayCommand(raw)) {
            return;
        }

        Player sender =
                e.getPlayer();

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {
            return;
        }

        Double before =
                lastBalance.remove(
                        sender.getUniqueId()
                );

        if (before == null) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> handlePay(
                        sender,
                        raw,
                        before,
                        eco
                ),
                1L
        );
    }

    //
    // 💰 HANDLE PAY
    //

    private void handlePay(
            Player sender,
            String raw,
            double before,
            Economy eco
    ) {

        PayData data =
                parsePay(raw);

        if (data == null) {
            return;
        }

        double after =
                eco.getBalance(sender);

        double real =
                before - after;

        if (real <= 0) {
            return;
        }

        Player target =
                Bukkit.getPlayerExact(
                        data.targetName()
                );

        String targetFinal =
                target != null
                        ? target.getName()
                        : data.targetName();

        resetDailyIfNeeded(
                sender.getUniqueId()
        );

        dailySent.put(
                sender.getUniqueId(),
                dailySent.getOrDefault(
                        sender.getUniqueId(),
                        0.0
                ) + real
        );

        //
        // 🧾 LOGS PROPRES
        //

        TransactionLogger.log(
                sender.getUniqueId().toString(),
                "PAY_SENT",
                real,
                targetFinal
        );

        if (real >= MAX_PERSONAL_PAY * 0.75) {

            TransactionLogger.log(
                    sender.getUniqueId().toString(),
                    "PAY_HIGH_VALUE_MONITORED",
                    real,
                    targetFinal
            );
        }

        if (target != null) {

            TransactionLogger.log(
                    target.getUniqueId().toString(),
                    "PAY_RECEIVED",
                    real,
                    sender.getName()
            );
        }
    }

    //
    // ❌ MESSAGE GROS PAIEMENT
    //

    private void denyProfessionalPayment(
            Player p,
            double amount
    ) {

        p.sendMessage("");
        p.sendMessage(
                "§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----"
        );
        p.sendMessage(
                "§cVirement refusé."
        );
        p.sendMessage("");
        p.sendMessage(
                "§7Montant demandé: §e"
                        + format(amount)
        );
        p.sendMessage(
                "§7Limite virement personnel: §e"
                        + format(MAX_PERSONAL_PAY)
        );
        p.sendMessage("");
        p.sendMessage(
                "§7Les paiements professionnels doivent passer"
        );
        p.sendMessage(
                "§7par un §econtrat officiel§7."
        );
        p.sendMessage("");
        p.sendMessage(
                "§8• §7Fonds sécurisés"
        );
        p.sendMessage(
                "§8• §7Taxe économique 20%"
        );
        p.sendMessage(
                "§8• §7Historique officiel"
        );
        p.sendMessage(
                "§8• §7Protection anti-arnaque"
        );
        p.sendMessage("");
        p.sendMessage(
                "§eUtilisez : §f/contrat"
        );
        p.sendMessage("");
    }

    //
    // ❌ MESSAGE LIMITE JOURNALIÈRE
    //

    private void denyDailyLimit(
            Player p,
            double already,
            double amount
    ) {

        p.sendMessage("");
        p.sendMessage(
                "§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----"
        );
        p.sendMessage(
                "§cVirement refusé."
        );
        p.sendMessage("");
        p.sendMessage(
                "§7Déjà envoyé aujourd'hui: §e"
                        + format(already)
        );
        p.sendMessage(
                "§7Montant demandé: §e"
                        + format(amount)
        );
        p.sendMessage(
                "§7Limite journalière: §e"
                        + format(MAX_DAILY_PERSONAL_PAY)
        );
        p.sendMessage("");
        p.sendMessage(
                "§7Pour un paiement important ou professionnel,"
        );
        p.sendMessage(
                "§7utilisez un §econtrat officiel§7."
        );
        p.sendMessage("");
        p.sendMessage(
                "§eCommande : §f/contrat"
        );
        p.sendMessage("");
    }

    //
    // 🔎 PARSE
    //

    private PayData parsePay(
            String raw
    ) {

        String[] args =
                raw.split(" ");

        if (args.length < 3) {
            return null;
        }

        String targetName =
                args[1];

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            args[2].replace(",", ".")
                    );

        } catch (Exception e) {

            return null;
        }

        if (amount <= 0) {
            return null;
        }

        return new PayData(
                targetName,
                amount
        );
    }

    //
    // 🔎 COMMAND CHECK
    //

    private boolean isPayCommand(
            String raw
    ) {

        String lower =
                raw.toLowerCase();

        return lower.startsWith("/pay ")
                || lower.startsWith("/essentials:pay ")
                || lower.startsWith("/epay ");
    }

    //
    // 📅 DAILY RESET
    //

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

    //
    // 🔧 NORMALISATION
    //

    private String normalizeRaw(
            String raw
    ) {

        if (raw == null) {
            return "";
        }

        return raw
                .trim()
                .replaceAll("\\s+", " ");
    }

    //
    // 💶 FORMAT
    //

    private String format(
            double amount
    ) {

        return String.format(
                "%,.0f€",
                amount
        ).replace(",", " ");
    }

    //
    // 📦 DATA
    //

    private record PayData(
            String targetName,
            double amount
    ) {}
}