package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.TransactionManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class BankHistoryCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 8;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        String filter = null;
        String search = null;
        int page = 1;

        // =========================
        // 🎛 FILTRE FR → EN
        // =========================
        if (args.length >= 1) {
            filter = translateFilter(args[0]);
        }

        // =========================
        // 🔎 RECHERCHE / PAGE
        // =========================
        if (args.length >= 2) {
            if (isNumber(args[1])) {
                page = Integer.parseInt(args[1]);
            } else {
                search = args[1];
            }
        }

        if (args.length >= 3) {
            page = Integer.parseInt(args[2]);
        }

        List<String> filtered = TransactionManager.getFiltered(
                p.getUniqueId(),
                filter,
                search
        );

        List<String> pageData = TransactionManager.getPage(filtered, page, PAGE_SIZE);

        p.sendMessage("§8§m-----------------------------");
        p.sendMessage("§6✦ §fHistorique bancaire §8(Page " + page + ")");

        if (pageData.isEmpty()) {
            p.sendMessage("§7Aucune transaction");
        } else {
            for (String line : pageData) {
                p.sendMessage(" " + line);
            }
        }

        p.sendMessage("§8§m-----------------------------");

        return true;
    }

    // =========================
    // 🔄 TRADUCTION FILTRES
    // =========================
    private String translateFilter(String input) {

        return switch (input.toLowerCase()) {
            case "depot" -> "DEPOSIT";
            case "retrait" -> "WITHDRAW";
            case "virement" -> "TRANSFER";
            default -> input.toUpperCase();
        };
    }

    private boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}