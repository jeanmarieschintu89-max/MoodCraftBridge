package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BankAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("moodcraft.admin") && !sender.hasPermission("econ.admin")) {
            error(sender, "Accès réservé à l'administration bancaire.");
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "help", "aide" -> help(sender);
            case "status", "etat", "état" -> status(sender);
            case "info" -> info(sender, args);
            case "set" -> set(sender, args);
            case "add", "ajouter" -> add(sender, args);
            case "remove", "retirer" -> remove(sender, args);
            case "iban" -> iban(sender, args);
            case "history", "historique" -> history(sender, args);
            case "reload" -> reload(sender);
            case "save" -> save(sender);
            default -> help(sender);
        }

        return true;
    }

    private void status(CommandSender sender) {
        int logs = BankStorage.getLogs().size();

        header(sender, "Admin Banque");
        sender.sendMessage("§e➜ §7Logs transactions : §e" + logs);
        sender.sendMessage("§e➜ §7Stockage : §abank.yml chargé");
        footer(sender);
    }

    private void info(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/bankadmin info <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String uuid = target.getUniqueId().toString();

        header(sender, "Admin Banque");
        sender.sendMessage("§e➜ §7Joueur : §e" + safeName(target));
        sender.sendMessage("§e➜ §7UUID : §f" + uuid);
        sender.sendMessage("§e➜ §7Solde : §e" + money(BankStorage.get(uuid)));
        sender.sendMessage("§e➜ §7IBAN : §e" + BankStorage.getIban(uuid));
        footer(sender);
    }

    private void set(CommandSender sender, String[] args) {
        if (args.length < 3) {
            usage(sender, "/bankadmin set <joueur> <montant>");
            return;
        }

        Double amount = parseAmount(args[2]);
        if (amount == null || amount < 0) {
            error(sender, "Montant invalide.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        BankStorage.set(target.getUniqueId().toString(), amount);
        success(sender, "Solde défini pour §e" + safeName(target) + " §8: §e" + money(amount));
    }

    private void add(CommandSender sender, String[] args) {
        if (args.length < 3) {
            usage(sender, "/bankadmin add <joueur> <montant>");
            return;
        }

        Double amount = parseAmount(args[2]);
        if (amount == null || amount <= 0) {
            error(sender, "Montant invalide.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        BankStorage.add(target.getUniqueId().toString(), amount);
        success(sender, "Ajout effectué pour §e" + safeName(target) + " §8: §e" + money(amount));
    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            usage(sender, "/bankadmin remove <joueur> <montant>");
            return;
        }

        Double amount = parseAmount(args[2]);
        if (amount == null || amount <= 0) {
            error(sender, "Montant invalide.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        boolean ok = BankStorage.remove(target.getUniqueId().toString(), amount);

        if (!ok) {
            error(sender, "Solde insuffisant ou retrait impossible.");
            return;
        }

        success(sender, "Retrait effectué pour §e" + safeName(target) + " §8: §e" + money(amount));
    }

    private void iban(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/bankadmin iban <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        success(sender, "IBAN de §e" + safeName(target) + " §8: §e" + BankStorage.getIban(target.getUniqueId().toString()));
    }

    private void history(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/bankadmin history <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String uuid = target.getUniqueId().toString();

        List<String> keys = new ArrayList<>(BankStorage.getLogs());
        keys.removeIf(key -> !uuid.equals(BankStorage.getLog(key, "uuid")));
        keys.sort(Comparator.comparingLong(this::logTime).reversed());

        header(sender, "Historique Banque");
        sender.sendMessage("§e➜ §7Joueur : §e" + safeName(target));

        int shown = 0;
        for (String key : keys) {
            sender.sendMessage("§e➜ §7" + BankStorage.getLog(key, "type")
                    + " §8• §e" + BankStorage.getLog(key, "amount")
                    + " §8• §7" + BankStorage.getLog(key, "reason"));
            shown++;
            if (shown >= 8) {
                break;
            }
        }

        if (shown == 0) {
            sender.sendMessage("§e➜ §7Aucune transaction trouvée.");
        }

        footer(sender);
    }

    private long logTime(String key) {
        try {
            return Long.parseLong(BankStorage.getLog(key, "time"));
        } catch (Exception e) {
            return 0L;
        }
    }

    private void reload(CommandSender sender) {
        BankStorage.init();
        success(sender, "Stockage bancaire rechargé.");
    }

    private void save(CommandSender sender) {
        BankStorage.save();
        success(sender, "Stockage bancaire sauvegardé.");
    }

    private Double parseAmount(String input) {
        try {
            return Double.parseDouble(input.replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    private String money(double value) {
        return NumberFormat.getInstance(Locale.FRANCE).format(value) + "€";
    }

    private String safeName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : "Inconnu";
    }

    private void help(CommandSender sender) {
        header(sender, "Admin Banque");
        sender.sendMessage("§e➜ §7/bankadmin status");
        sender.sendMessage("§e➜ §7/bankadmin info <joueur>");
        sender.sendMessage("§e➜ §7/bankadmin set <joueur> <montant>");
        sender.sendMessage("§e➜ §7/bankadmin add <joueur> <montant>");
        sender.sendMessage("§e➜ §7/bankadmin remove <joueur> <montant>");
        sender.sendMessage("§e➜ §7/bankadmin iban <joueur>");
        sender.sendMessage("§e➜ §7/bankadmin history <joueur>");
        sender.sendMessage("§e➜ §7/bankadmin reload");
        sender.sendMessage("§e➜ §7/bankadmin save");
        footer(sender);
    }

    private void usage(CommandSender sender, String usage) {
        header(sender, "Admin Banque");
        sender.sendMessage("§c✖ §fCommande incomplète.");
        sender.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(sender);
    }

    private void success(CommandSender sender, String message) {
        header(sender, "Admin Banque");
        sender.sendMessage("§a✔ §f" + message);
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Admin Banque");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
    }
}
