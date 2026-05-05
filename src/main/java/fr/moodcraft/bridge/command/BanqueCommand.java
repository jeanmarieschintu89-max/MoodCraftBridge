package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.*;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BanqueCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 8;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        // =========================
        // 🏦 MENU
        // =========================
        if (args.length == 0) {
            BankGUI.open(p);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // =========================
            // 💰 DEPOT
            // =========================
            case "depot" -> {

                if (args.length < 2) return usage(p, "/banque depot <montant>");

                double amount = parseAmount(p, args[1]);
                if (amount <= 0) return true;

                if (VaultHook.getBalance(p) < amount) {
                    return error(p, "Pas assez d'argent");
                }

                VaultHook.remove(p, amount);
                BankStorage.add(p.getUniqueId().toString(), amount);
                TransactionManager.deposit(p.getUniqueId(), amount);

                success(p, "Dépôt", "+" + SafeGUI.money(amount));
            }

            // =========================
            // 💸 RETRAIT
            // =========================
            case "retrait" -> {

                if (args.length < 2) return usage(p, "/banque retrait <montant>");

                double amount = parseAmount(p, args[1]);
                if (amount <= 0) return true;

                String uuid = p.getUniqueId().toString();

                if (BankStorage.get(uuid) < amount) {
                    return error(p, "Fonds insuffisants");
                }

                BankStorage.remove(uuid, amount);
                VaultHook.add(p, amount);
                TransactionManager.withdraw(p.getUniqueId(), amount);

                success(p, "Retrait", "-" + SafeGUI.money(amount));
            }

            // =========================
            // 💸 VIREMENT
            // =========================
            case "virement" -> {

                if (args.length < 3) return usage(p, "/banque virement <iban> <montant>");

                String iban = args[1].replace(" ", "").toUpperCase();
                double amount = parseAmount(p, args[2]);
                if (amount <= 0) return true;

                UUID targetUUID = IbanManager.getOwner(iban);

                if (targetUUID == null) return error(p, "IBAN introuvable");
                if (targetUUID.equals(p.getUniqueId())) return error(p, "Auto-virement interdit");

                if (VaultHook.getBalance(p) < amount) {
                    return error(p, "Fonds insuffisants");
                }

                Player target = Bukkit.getPlayer(targetUUID);

                VaultHook.remove(p, amount);

                if (target != null && target.isOnline()) {
                    VaultHook.add(target, amount);
                } else {
                    try {
                        VaultHook.add(targetUUID, amount);
                    } catch (Exception ignored) {}
                }

                TransactionManager.transfer(p.getUniqueId(), targetUUID, amount);

                p.sendMessage("§8§m-----------------------------");
                p.sendMessage("§6✦ §fVirement effectué");
                p.sendMessage("§7Vers IBAN: §e" + iban);
                p.sendMessage("§7Montant: §c-" + SafeGUI.money(amount) + "€");
                p.sendMessage("§8§m-----------------------------");

                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.2f);

                if (target != null && target.isOnline()) {
                    target.sendMessage("§8§m-----------------------------");
                    target.sendMessage("§a✦ §fVirement reçu");
                    target.sendMessage("§7De: §e" + p.getName());
                    target.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
                    target.sendMessage("§8§m-----------------------------");
                }
            }

            // =========================
            // 📜 HISTORIQUE
            // =========================
            case "historique" -> {

                int page = 1;
                String filter = null;

                if (args.length >= 2) filter = translate(args[1]);
                if (args.length >= 3 && isNumber(args[2])) page = Integer.parseInt(args[2]);

                List<String> list = TransactionManager.getFiltered(p.getUniqueId(), filter, null);
                List<String> pageData = TransactionManager.getPage(list, page, PAGE_SIZE);

                p.sendMessage("§8§m-----------------------------");
                p.sendMessage("§6✦ §fHistorique bancaire §8(Page " + page + ")");

                if (pageData.isEmpty()) {
                    p.sendMessage("§7Aucune transaction");
                } else {
                    pageData.forEach(line -> p.sendMessage(" " + line));
                }

                p.sendMessage("§8§m-----------------------------");
            }

            // =========================
            // 💳 IBAN
            // =========================
            case "iban" -> {
                String iban = IbanManager.get(p.getUniqueId());
                p.sendMessage("§6Ton IBAN: §e" + (iban == null ? "Non défini" : iban));
            }

            // =========================
            // 🛠 ADMIN
            // =========================
            case "admin" -> {

                if (!p.hasPermission("moodcraft.admin")) return error(p, "Permission refusée");

                if (args.length < 3) {
                    p.sendMessage("§c/banque admin <action> <joueur>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) return error(p, "Joueur introuvable");

                String uuid = target.getUniqueId().toString();
                String action = args[1].toLowerCase();

                switch (action) {

                    case "solde" -> {
                        p.sendMessage("§6Solde: §e" + BankStorage.get(uuid) + "€");
                    }

                    case "ajouter" -> {
                        double amount = parseAmount(p, args[3]);
                        BankStorage.add(uuid, amount);
                        p.sendMessage("§aAjouté");
                    }

                    case "retirer" -> {
                        double amount = parseAmount(p, args[3]);
                        BankStorage.remove(uuid, amount);
                        p.sendMessage("§cRetiré");
                    }

                    case "reset" -> {
                        BankStorage.set(uuid, 0);
                        p.sendMessage("§cReset effectué");
                    }

                    case "iban" -> {
                        p.sendMessage("§6IBAN: §e" + IbanManager.get(target.getUniqueId()));
                    }
                }
            }

            // =========================
            // 📜 LOGS
            // =========================
            case "logs" -> {
                if (!p.hasPermission("moodcraft.admin")) return error(p, "Permission refusée");

                TransactionManager.getGlobal()
                        .stream()
                        .limit(10)
                        .forEach(line -> p.sendMessage(" " + line));
            }

            default -> p.sendMessage("§cSous-commande inconnue");
        }

        return true;
    }

    // =========================
    // 🔧 UTILS
    // =========================

    private double parseAmount(Player p, String s) {
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (Exception e) {
            error(p, "Montant invalide");
            return -1;
        }
    }

    private boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean error(Player p, String msg) {
        p.sendMessage("§c❌ " + msg);
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        return true;
    }

    private void success(Player p, String type, String amount) {
        p.sendMessage("§a✔ " + type + ": §e" + amount + "€");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    private boolean usage(Player p, String msg) {
        p.sendMessage("§cUsage: " + msg);
        return true;
    }

    private String translate(String s) {
        return switch (s.toLowerCase()) {
            case "depot" -> "DEPOSIT";
            case "retrait" -> "WITHDRAW";
            case "virement" -> "TRANSFER";
            default -> null;
        };
    }
}