package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.IbanManager;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;

import fr.moodcraft.bridge.manager.ReputationManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BanqueCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 8;

    @Override
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String label,
                             String[] args) {

        if (!(sender instanceof Player p))
            return true;

        //
        // 🏦 /rib
        //

        if (label.equalsIgnoreCase("rib")) {

            String iban =
                    IbanManager.get(
                            p.getUniqueId()
                    );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§6🏦 §fCompte bancaire MoodCraft"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7IBAN associé:"
            );

            p.sendMessage(
                    "§e" + iban
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Utilisable pour les virements."
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

            String iban =
                    args[0]
                            .replace(" ", "")
                            .toUpperCase();

            double amount =
                    parseAmount(
                            p,
                            args[1]
                    );

            if (amount <= 0)
                return true;

            UUID targetUUID =
                    IbanManager.getOwner(
                            iban
                    );

            if (targetUUID == null)
                return error(
                        p,
                        "IBAN introuvable."
                );

            if (targetUUID.equals(
                    p.getUniqueId()
            )) {

                return error(
                        p,
                        "Auto-virement interdit."
                );
            }

            String senderUUID =
                    p.getUniqueId().toString();

            if (BankStorage.get(senderUUID)
                    < amount) {

                return error(
                        p,
                        "Fonds insuffisants."
                );
            }

            Player target =
                    Bukkit.getPlayer(targetUUID);

            String targetName =
                    Bukkit.getOfflinePlayer(
                            targetUUID
                    ).getName();

            if (targetName == null)
                targetName = "Inconnu";

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
                    "§7Destinataire: §e"
                            + targetName
                            + " §8("
                            + targetRank
                            + "§8)"
            );

            p.sendMessage(
                    "§7Montant envoyé: §c-"
                            + SafeGUI.money(amount)
                            + "€"
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
                        "§7Expéditeur: §e"
                                + p.getName()
                                + " §8("
                                + senderRank
                                + "§8)"
                );

                target.sendMessage(
                        "§7Montant reçu: §a+"
                                + SafeGUI.money(amount)
                                + "€"
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
                        1.3f
                );
            }

            return true;
        }

        //
        // 🏦 GUI BANQUE
        //

        if (args.length == 0) {

            BankGUI.open(p);

            return true;
        }

        String sub =
                args[0].toLowerCase();

        switch (sub) {//
            // 📥 DEPOT
            //

            case "depot" -> {

                if (args.length < 2)
                    return usage(
                            p,
                            "/banque depot <montant>"
                    );

                double amount =
                        parseAmount(
                                p,
                                args[1]
                        );

                if (amount <= 0)
                    return true;

                if (VaultHook.getBalance(p)
                        < amount) {

                    return error(
                            p,
                            "Pas assez d'argent."
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

                success(
                        p,
                        "Dépôt",
                        "+" + SafeGUI.money(amount)
                );
            }

            //
            // 📤 RETRAIT
            //

            case "retrait" -> {

                if (args.length < 2)
                    return usage(
                            p,
                            "/banque retrait <montant>"
                    );

                double amount =
                        parseAmount(
                                p,
                                args[1]
                        );

                if (amount <= 0)
                    return true;

                String uuid =
                        p.getUniqueId().toString();

                if (BankStorage.get(uuid)
                        < amount) {

                    return error(
                            p,
                            "Fonds insuffisants."
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

                success(
                        p,
                        "Retrait",
                        "-" + SafeGUI.money(amount)
                );
            }

            //
            // 📜 HISTORIQUE
            //

            case "historique" -> {

                int page = 1;

                String filter = null;

                if (args.length >= 2)
                    filter = translate(args[1]);

                if (args.length >= 3
                        && isNumber(args[2])) {

                    page = Integer.parseInt(args[2]);
                }

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

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fHistorique bancaire §8(Page "
                                + page
                                + ")"
                );

                p.sendMessage("");

                if (pageData.isEmpty()) {

                    p.sendMessage(
                            "§7Aucune transaction."
                    );

                } else {

                    pageData.forEach(
                            line -> p.sendMessage(" " + line)
                    );
                }

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");
            }

            //
            // 🏦 IBAN
            //

            case "iban" -> {

                String iban =
                        IbanManager.get(
                                p.getUniqueId()
                        );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6🏦 §fCompte bancaire MoodCraft"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7IBAN associé:"
                );

                p.sendMessage(
                        "§e" + iban
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Utilisable pour les virements."
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
            }//
            // 🔒 ADMIN
            //

            case "admin" -> {

                if (!p.hasPermission(
                        "moodcraft.admin"
                )) {

                    return error(
                            p,
                            "Permission refusée."
                    );
                }

                p.sendMessage(
                        "§cAdministration bancaire active."
                );
            }

            //
            // 📜 LOGS
            //

            case "logs" -> {

                if (!p.hasPermission(
                        "moodcraft.admin"
                )) {

                    return error(
                            p,
                            "Permission refusée."
                    );
                }

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fLogs bancaires"
                );

                p.sendMessage("");

                TransactionManager.getGlobal()
                        .stream()
                        .limit(10)
                        .forEach(
                                line -> p.sendMessage(" " + line)
                        );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");
            }

            default ->
                    p.sendMessage(
                            "§cSous-commande inconnue."
                    );
        }

        return true;
    }

    //
    // 🔢 PARSE
    //

    private double parseAmount(Player p,
                               String s) {

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

    private boolean isNumber(String s) {

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

    private boolean error(Player p,
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

        return true;
    }

    //
    // ✅ SUCCESS
    //

    private boolean success(Player p,
                            String type,
                            String amount) {

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fTransaction bancaire"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Type: §e" + type
        );

        p.sendMessage(
                "§7Montant: §a" + amount
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

        return true;
    }

    //
    // 📘 USAGE
    //

    private boolean usage(Player p,
                          String msg) {

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fCommande bancaire"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Usage: §e" + msg
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                1f,
                0.8f
        );

        return true;
    }

    //
    // 🌍 FILTER
    //

    private String translate(String s) {

        return switch (s.toLowerCase()) {

            case "depot" -> "DEPOSIT";

            case "retrait" -> "WITHDRAW";

            case "virement" -> "TRANSFER";

            default -> null;
        };
    }
}