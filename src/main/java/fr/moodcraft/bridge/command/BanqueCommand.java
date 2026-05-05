package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.IbanManager;
import fr.moodcraft.bridge.bank.TransactionManager;
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
                if (args.length < 2) {
                    p.sendMessage("§cUsage: /banque depot <montant>");
                    return true;
                }

                double amount = parseAmount(p, args[1]);
                if (amount <= 0) return true;

                double cash = VaultHook.getBalance(p);
                if (cash < amount) {
                    error(p, "Pas assez d'argent");
                    return true;
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
                if (args.length < 2) {
                    p.sendMessage("§cUsage: /banque retrait <montant>");
                    return true;
                }

                double amount = parseAmount(p, args[1]);
                if (amount <= 0) return true;

                String uuid = p.getUniqueId().toString();
                double bank = BankStorage.get(uuid);

                if (bank < amount) {
                    error(p, "Fonds insuffisants");
                    return true;
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
                if (args.length < 3) {
                    p.sendMessage("§cUsage: /banque virement <iban> <montant>");
                    return true;
                }

                String iban = args[1].replace(" ", "").toUpperCase();
                double amount = parseAmount(p, args[2]);
                if (amount <= 0) return true;

                UUID targetUUID = IbanManager.getOwner(iban);

                if (targetUUID == null) {
                    error(p, "IBAN introuvable");
                    return true;
                }

                if (targetUUID.equals(p.getUniqueId())) {
                    error(p, "Tu ne peux pas te payer toi-même");
                    return true;
                }

                double balance = VaultHook.getBalance(p);
                if (balance < amount) {
                    error(p, "Fonds insuffisants");
                    return true;
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

                // messages stylés
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

                String filter = null;
                String search = null;
                int page = 1;

                if (args.length >= 2) filter = translate(args[1]);
                if (args.length >= 3) search = args[2];
                if (args.length >= 4) page = Integer.parseInt(args[3]);

                List<String> list = TransactionManager.getFiltered(
                        p.getUniqueId(), filter, search
                );

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
            // 📜 LOGS ADMIN
            // =========================
            case "logs" -> {
                if (!p.hasPermission("moodcraft.admin")) {
                    error(p, "Permission refusée");
                    return true;
                }

                TransactionManager.getGlobal()
                        .stream()
                        .limit(10)
                        .forEach(line -> p.sendMessage(" " + line));
            }

            default -> p.sendMessage("§cSous-commande inconnue");
        }

        return true;
    }

    private double parseAmount(Player p, String s) {
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (Exception e) {
            error(p, "Montant invalide");
            return -1;
        }
    }

    private void error(Player p, String msg) {
        p.sendMessage("§c❌ " + msg);
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    private void success(Player p, String type, String amount) {
        p.sendMessage("§a✔ " + type + ": §e" + amount + "€");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
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