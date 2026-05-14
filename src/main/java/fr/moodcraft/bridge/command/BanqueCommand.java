package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.IbanManager;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;

import fr.moodcraft.bridge.manager.ReputationManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.TransactionLogger;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BanqueCommand implements CommandExecutor {

    private static final int PAGE_SIZE =
            8;

    //
    // 🔒 LIMITES VIREMENT DIRECT
    //

    private static final double MAX_PERSONAL_TRANSFER =
            10000.0;

    private static final double MAX_DAILY_PERSONAL_TRANSFER =
            25000.0;

    private static final Map<UUID, Double> dailySent =
            new HashMap<>();

    private static final Map<UUID, String> dailyDate =
            new HashMap<>();

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {
            return true;
        }

        //
        // 📥 /depot
        //

        if (label.equalsIgnoreCase("depot")) {

            if (args.length < 1) {

                return usage(
                        p,
                        "/depot <montant>"
                );
            }

            return deposit(
                    p,
                    args[0]
            );
        }

        //
        // 📤 /retrait
        //

        if (label.equalsIgnoreCase("retrait")) {

            if (args.length < 1) {

                return usage(
                        p,
                        "/retrait <montant>"
                );
            }

            return withdraw(
                    p,
                    args[0]
            );
        }

        //
        // 🏦 /rib
        //

        if (label.equalsIgnoreCase("rib")) {

            sendIban(p);

            return true;
        }

        //
        // 💸 /virement
        //

        if (label.equalsIgnoreCase("virement")) {

            if (args.length < 2) {

                return usage(
                        p,
                        "/virement <iban> <montant>"
                );
            }

            return directTransfer(
                    p,
                    args[0],
                    args[1]
            );
        }

        //
        // 🏦 /banque
        //

        if (args.length == 0) {

            BankGUI.open(p);

            return true;
        }

        String sub =
                args[0].toLowerCase();

        switch (sub) {

            //
            // 📥 /banque depot <montant>
            //

            case "depot", "dépôt", "deposit" -> {

                if (args.length < 2) {

                    return usage(
                            p,
                            "/banque depot <montant>"
                    );
                }

                return deposit(
                        p,
                        args[1]
                );
            }

            //
            // 📤 /banque retrait <montant>
            //

            case "retrait", "withdraw" -> {

                if (args.length < 2) {

                    return usage(
                            p,
                            "/banque retrait <montant>"
                    );
                }

                return withdraw(
                        p,
                        args[1]
                );
            }

            //
            // 💸 /banque virement
            //

            case "virement", "transfer" -> {

                return usage(
                        p,
                        "/virement <iban> <montant>"
                );
            }

            //
            // 📜 HISTORIQUE
            //

            case "historique", "history" -> {

                int page =
                        1;

                String filter =
                        null;

                if (args.length >= 2) {

                    filter =
                            translate(args[1]);
                }

                if (args.length >= 3
                        && isNumber(args[2])) {

                    page =
                            Integer.parseInt(args[2]);
                }

                sendHistory(
                        p,
                        filter,
                        page
                );

                return true;
            }

            //
            // 🏦 IBAN
            //

            case "iban" -> {

                sendIban(p);

                return true;
            }

            //
            // 🔒 ADMIN
            //

            case "admin" -> {

                if (!p.hasPermission("moodcraft.admin")) {

                    return error(
                            p,
                            "Permission refusée."
                    );
                }

                header(p);

                p.sendMessage("§a✔ §fAdministration bancaire active.");
                p.sendMessage("");
                p.sendMessage(detail("Utilisez les sous-commandes réservées au staff."));

                footer(p);

                return true;
            }

            //
            // 📜 LOGS
            //

            case "logs" -> {

                if (!p.hasPermission("moodcraft.admin")) {

                    return error(
                            p,
                            "Permission refusée."
                    );
                }

                sendLogs(p);

                return true;
            }

            default -> {

                return error(
                        p,
                        "Sous-commande inconnue."
                );
            }
        }
    }

    //
    // 📥 DEPOT
    //

    private boolean deposit(
            Player p,
            String rawAmount
    ) {

        double amount =
                parseAmount(
                        p,
                        rawAmount
                );

        if (amount <= 0) {
            return true;
        }

        if (VaultHook.getBalance(p) < amount) {

            return error(
                    p,
                    "Pas assez d'argent liquide."
            );
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

        return success(
                p,
                "Dépôt",
                "§a+" + SafeGUI.money(amount) + "€"
        );
    }

    //
    // 📤 RETRAIT
    //

    private boolean withdraw(
            Player p,
            String rawAmount
    ) {

        double amount =
                parseAmount(
                        p,
                        rawAmount
                );

        if (amount <= 0) {
            return true;
        }

        String uuid =
                p.getUniqueId().toString();

        if (BankStorage.get(uuid) < amount) {

            return error(
                    p,
                    "Fonds bancaires insuffisants."
            );
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

        return success(
                p,
                "Retrait",
                "§a+" + SafeGUI.money(amount) + "€"
        );
    }

    //
    // 💸 VIREMENT DIRECT PAR IBAN
    //

    private boolean directTransfer(
            Player p,
            String rawIban,
            String rawAmount
    ) {

        String iban =
                rawIban
                        .replace(" ", "")
                        .toUpperCase();

        double amount =
                parseAmount(
                        p,
                        rawAmount
                );

        if (amount <= 0) {
            return true;
        }

        UUID targetUUID =
                IbanManager.getOwner(
                        iban
                );

        if (targetUUID == null) {

            return error(
                    p,
                    "IBAN introuvable."
            );
        }

        if (targetUUID.equals(
                p.getUniqueId()
        )) {

            return error(
                    p,
                    "Auto-virement interdit."
            );
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

                return denyProfessionalTransfer(
                        p,
                        targetName,
                        amount
                );
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

                return denyDailyLimit(
                        p,
                        targetName,
                        today,
                        amount
                );
            }
        }

        String senderUUID =
                p.getUniqueId().toString();

        if (BankStorage.get(senderUUID) < amount) {

            return error(
                    p,
                    "Fonds bancaires insuffisants."
            );
        }

        Player target =
                Bukkit.getPlayer(targetUUID);

        int targetRep =
                ReputationManager.get(
                        targetUUID.toString()
                );

        String targetRank =
                ReputationManager.getRank(
                        targetRep
                );

        int senderRep =
                ReputationManager.get(
                        p.getUniqueId().toString()
                );

        String senderRank =
                ReputationManager.getRank(
                        senderRep
                );

        //
        // 💸 TRANSACTION
        //

        BankStorage.remove(
                senderUUID,
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

        //
        // ✨ ENVOYEUR
        //

        header(p);

        p.sendMessage("§a✔ §fVirement effectué.");
        p.sendMessage("");
        p.sendMessage(detail("Destinataire : §e" + targetName));
        p.sendMessage(detail("Réputation : §a" + targetRep + " §8• " + targetRank));
        p.sendMessage(detail("Montant : §c-" + SafeGUI.money(amount) + "€"));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f,
                1.2f
        );

        //
        // ✨ DESTINATAIRE
        //

        if (target != null && target.isOnline()) {

            header(target);

            target.sendMessage("§a✔ §fVirement reçu.");
            target.sendMessage("");
            target.sendMessage(detail("Expéditeur : §e" + p.getName()));
            target.sendMessage(detail("Réputation : §a" + senderRep + " §8• " + senderRank));
            target.sendMessage(detail("Montant : §a+" + SafeGUI.money(amount) + "€"));

            footer(target);

            target.playSound(
                    target.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.3f
            );
        }

        return true;
    }

    //
    // 🏦 IBAN
    //

    private void sendIban(
            Player p
    ) {

        String iban =
                IbanManager.get(
                        p.getUniqueId()
                );

        header(p);

        p.sendMessage("§e➜ §fCompte bancaire personnel.");
        p.sendMessage("");
        p.sendMessage(detail("IBAN : §e" + iban));
        p.sendMessage("");
        p.sendMessage(detail("Utilisable pour les virements"));
        p.sendMessage(detail("À partager seulement aux joueurs de confiance"));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                1f,
                1.2f
        );
    }

    //
    // 📜 HISTORIQUE
    //

    private void sendHistory(
            Player p,
            String filter,
            int page
    ) {

        List<String> list =
                TransactionManager.getFiltered(
                        p.getUniqueId(),
                        filter,
                        null
                );

        List<String> pageData =
                TransactionManager.getPage(
                        list,
                        page,
                        PAGE_SIZE
                );

        header(p);

        p.sendMessage("§e➜ §fHistorique bancaire.");
        p.sendMessage("");
        p.sendMessage(detail("Page : §e" + page));
        p.sendMessage(detail("Entrées : §e" + list.size()));
        p.sendMessage("");

        if (pageData.isEmpty()) {

            p.sendMessage(detail("Aucune transaction."));

        } else {

            pageData.forEach(
                    line -> p.sendMessage(detail(line))
            );
        }

        footer(p);
    }

    //
    // 📜 LOGS ADMIN
    //

    private void sendLogs(
            Player p
    ) {

        header(p);

        p.sendMessage("§e➜ §fLogs bancaires.");
        p.sendMessage("");
        p.sendMessage(detail("Dernières lignes :"));

        p.sendMessage("");

        TransactionManager.getGlobal()
                .stream()
                .limit(10)
                .forEach(
                        line -> p.sendMessage(detail(line))
                );

        footer(p);
    }

    //
    // ❌ VIREMENT REFUSÉ PRO
    //

    private boolean denyProfessionalTransfer(
            Player p,
            String targetName,
            double amount
    ) {

        header(p);

        p.sendMessage("§c✖ §fVirement refusé.");
        p.sendMessage("");
        p.sendMessage(detail("Destinataire : §e" + targetName));
        p.sendMessage(detail("Montant : §e" + SafeGUI.money(amount) + "€"));
        p.sendMessage(detail("Limite personnelle : §e" + SafeGUI.money(MAX_PERSONAL_TRANSFER) + "€"));
        p.sendMessage("");
        p.sendMessage("§e➜ §fPaiement professionnel conseillé : §e/contrat");
        p.sendMessage("");
        p.sendMessage(detail("Argent bloqué"));
        p.sendMessage(detail("Taxe économique 20%"));
        p.sendMessage(detail("Logs officiels"));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                1f
        );

        return true;
    }

    //
    // ❌ LIMITE JOURNALIÈRE
    //

    private boolean denyDailyLimit(
            Player p,
            String targetName,
            double already,
            double amount
    ) {

        header(p);

        p.sendMessage("§c✖ §fVirement refusé.");
        p.sendMessage("");
        p.sendMessage(detail("Destinataire : §e" + targetName));
        p.sendMessage(detail("Déjà envoyé : §e" + SafeGUI.money(already) + "€"));
        p.sendMessage(detail("Montant : §e" + SafeGUI.money(amount) + "€"));
        p.sendMessage(detail("Limite jour : §e" + SafeGUI.money(MAX_DAILY_PERSONAL_TRANSFER) + "€"));
        p.sendMessage("");
        p.sendMessage("§e➜ §fPaiement important conseillé : §e/contrat");

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                1f
        );

        return true;
    }

    //
    // 🔢 PARSE
    //

    private double parseAmount(
            Player p,
            String s
    ) {

        try {

            return Double.parseDouble(
                    s.replace(",", ".")
            );

        } catch (Exception e) {

            error(
                    p,
                    "Montant invalide."
            );

            return -1;
        }
    }

    //
    // 🔢 NUMBER
    //

    private boolean isNumber(
            String s
    ) {

        try {

            Integer.parseInt(s);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    //
    // ❌ ERROR
    //

    private boolean error(
            Player p,
            String msg
    ) {

        header(p);

        p.sendMessage("§c✖ §fAction refusée.");
        p.sendMessage("");
        p.sendMessage(detail(msg));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                1f
        );

        return true;
    }

    //
    // ✅ SUCCESS
    //

    private boolean success(
            Player p,
            String type,
            String amount
    ) {

        header(p);

        p.sendMessage("§a✔ §fTransaction bancaire.");
        p.sendMessage("");
        p.sendMessage(detail("Type : §e" + type));
        p.sendMessage(detail("Montant : " + amount));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f,
                1.15f
        );

        return true;
    }

    //
    // 📘 USAGE
    //

    private boolean usage(
            Player p,
            String msg
    ) {

        header(p);

        p.sendMessage("§e➜ §fCommande bancaire.");
        p.sendMessage("");
        p.sendMessage(detail("Usage : §e" + msg));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                1f,
                0.8f
        );

        return true;
    }

    //
    // 🎨 HEADER / FOOTER
    //

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

    private String detail(String text) {
        return "§8• §7" + text;
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
    // 🔒 BYPASS
    //

    private boolean canBypassLimit(
            Player p
    ) {

        return p.hasPermission("moodcraftbridge.transfer.bypass")
                || p.hasPermission("moodbusiness.bypass");
    }

    //
    // 🌍 FILTER
    //

    private String translate(
            String s
    ) {

        return switch (s.toLowerCase()) {

            case "depot" -> "DEPOSIT";

            case "retrait" -> "WITHDRAW";

            case "virement" -> "TRANSFER";

            default -> null;
        };
    }
}
