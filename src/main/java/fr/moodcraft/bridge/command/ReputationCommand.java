package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.ReputationManager;
import fr.moodcraft.bridge.manager.ReputationHistoryManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReputationCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 8;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // 🔥 Alias /toprep
        if (label.equalsIgnoreCase("toprep")) {
            args = new String[]{"classement"};
        }

        // =========================
        // 👤 /reputation
        // =========================
        if (args.length == 0) {

            if (!(sender instanceof Player p)) return true;

            int rep = ReputationManager.get(p.getUniqueId().toString());

            p.sendMessage("§8§m-----------------------------");
            p.sendMessage("§6✦ §fTa réputation");
            p.sendMessage("§7Points: §e" + rep);
            p.sendMessage("§7Rang: " + ReputationManager.getRank(rep));
            p.sendMessage("§8§m-----------------------------");

            return true;
        }

        // =========================
        // 📜 HISTORIQUE
        // =========================
        if (args[0].equalsIgnoreCase("historique")) {

            if (!(sender instanceof Player p)) return true;

            int page = 1;
            if (args.length >= 2 && isNumber(args[1])) {
                page = Integer.parseInt(args[1]);
            }

            List<String> list = ReputationHistoryManager.getPage(p.getUniqueId(), page, PAGE_SIZE);

            p.sendMessage("§8§m-----------------------------");
            p.sendMessage("§6✦ Historique Réputation §8(Page " + page + ")");

            if (list.isEmpty()) {
                p.sendMessage("§7Aucune donnée");
            } else {
                list.forEach(l -> p.sendMessage(" " + l));
            }

            p.sendMessage("§8§m-----------------------------");
            return true;
        }

        // =========================
        // 👤 /reputation <joueur>
        // =========================
        if (args.length == 1 &&
                !args[0].equalsIgnoreCase("classement") &&
                !args[0].equalsIgnoreCase("admin")) {

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            int rep = ReputationManager.get(target.getUniqueId().toString());

            sender.sendMessage("§8§m-----------------------------");
            sender.sendMessage("§6✦ Réputation de §e" + safeName(target));
            sender.sendMessage("§7Points: §e" + rep);
            sender.sendMessage("§7Rang: " + ReputationManager.getRank(rep));
            sender.sendMessage("§8§m-----------------------------");

            return true;
        }

        // =========================
        // 🏆 /reputation classement
        // =========================
        if (args[0].equalsIgnoreCase("classement")) {

            sender.sendMessage("§8§m-----------------------------");
            sender.sendMessage("§6🏆 Classement Réputation");

            int i = 1;

            for (Map.Entry<String, Integer> entry : ReputationManager.getTop(10).entrySet()) {

                UUID uuid = UUID.fromString(entry.getKey());
                String name = safeName(Bukkit.getOfflinePlayer(uuid));

                sender.sendMessage("§e#" + i + " §7" + name + " §8» §a" + entry.getValue());
                i++;
            }

            sender.sendMessage("§8§m-----------------------------");
            return true;
        }

        // =========================
        // 🔒 ADMIN
        // =========================
        if (!args[0].equalsIgnoreCase("admin")) {
            sender.sendMessage("§cSous-commande inconnue");
            return true;
        }

        if (!sender.hasPermission("moodcraft.admin")) {
            sender.sendMessage("§c❌ Permission refusée");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c/reputation admin <ajouter|retirer|definir|reset|historique> <joueur> [valeur/page]");
            return true;
        }

        String action = args[1].toLowerCase();

        // =========================
        // 📜 HISTO ADMIN
        // =========================
        if (action.equals("historique")) {

            if (args.length < 3) {
                sender.sendMessage("§cUsage: /reputation admin historique <joueur> [page]");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);

            int page = 1;
            if (args.length >= 4 && isNumber(args[3])) {
                page = Integer.parseInt(args[3]);
            }

            List<String> list = ReputationHistoryManager.getPage(target.getUniqueId(), page, PAGE_SIZE);

            sender.sendMessage("§8§m-----------------------------");
            sender.sendMessage("§6✦ Historique de §e" + safeName(target) + " §8(Page " + page + ")");

            if (list.isEmpty()) {
                sender.sendMessage("§7Aucune donnée");
            } else {
                list.forEach(l -> sender.sendMessage(" " + l));
            }

            sender.sendMessage("§8§m-----------------------------");
            return true;
        }

        // =========================
        // ACTIONS ADMIN
        // =========================
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /reputation admin <action> <joueur> [valeur]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        String id = target.getUniqueId().toString();

        switch (action) {

            case "ajouter" -> {
                int value = parse(sender, args, 3);
                if (value <= 0) return true;

                ReputationManager.add(id, value);
                ReputationHistoryManager.add(target.getUniqueId(), value, "Admin");

                sender.sendMessage("§a+" + value + " réputation → §e" + safeName(target));
            }

            case "retirer" -> {
                int value = parse(sender, args, 3);
                if (value <= 0) return true;

                ReputationManager.add(id, -value);
                ReputationHistoryManager.add(target.getUniqueId(), -value, "Admin");

                sender.sendMessage("§c-" + value + " réputation → §e" + safeName(target));
            }

            case "definir" -> {
                int value = parse(sender, args, 3);
                if (value < 0) value = 0;

                ReputationManager.set(id, value);
                ReputationHistoryManager.add(target.getUniqueId(), value, "Set admin");

                sender.sendMessage("§eRéputation définie → " + value);
            }

            case "reset" -> {
                ReputationManager.reset(id);
                ReputationHistoryManager.add(target.getUniqueId(), 0, "Reset");

                sender.sendMessage("§cRéputation réinitialisée → §e" + safeName(target));
            }

            default -> sender.sendMessage("§cAction inconnue");
        }

        return true;
    }

    // =========================
    // UTILS
    // =========================

    private int parse(CommandSender sender, String[] args, int index) {
        try {
            return Integer.parseInt(args[index]);
        } catch (Exception e) {
            sender.sendMessage("§cValeur invalide");
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

    private String safeName(OfflinePlayer p) {
        return p.getName() != null ? p.getName() : "Inconnu";
    }
}