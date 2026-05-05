package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.ReputationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ReputationCommand implements CommandExecutor {

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

        if (args.length < 3) {
            sender.sendMessage("§c/reputation admin <ajouter|retirer|definir|reset> <joueur> [valeur]");
            return true;
        }

        String action = args[1].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        String id = target.getUniqueId().toString();

        switch (action) {

            // =========================
            // ➕ AJOUTER
            // =========================
            case "ajouter" -> {

                int value = parse(sender, args, 3);
                if (value <= 0) return true;

                ReputationManager.add(id, value);

                sender.sendMessage("§a+" + value + " réputation → §e" + safeName(target));
            }

            // =========================
            // ➖ RETIRER
            // =========================
            case "retirer" -> {

                int value = parse(sender, args, 3);
                if (value <= 0) return true;

                ReputationManager.add(id, -value);

                sender.sendMessage("§c-" + value + " réputation → §e" + safeName(target));
            }

            // =========================
            // ⚙️ DEFINIR
            // =========================
            case "definir" -> {

                int value = parse(sender, args, 3);
                if (value < 0) value = 0;

                ReputationManager.set(id, value);

                sender.sendMessage("§eRéputation définie → " + value);
            }

            // =========================
            // 🔄 RESET
            // =========================
            case "reset" -> {

                ReputationManager.reset(id);

                sender.sendMessage("§cRéputation réinitialisée → §e" + safeName(target));
            }

            default -> sender.sendMessage("§cAction inconnue");
        }

        return true;
    }

    // =========================
    // 🔧 UTILS
    // =========================

    private int parse(CommandSender sender, String[] args, int index) {
        try {
            return Integer.parseInt(args[index]);
        } catch (Exception e) {
            sender.sendMessage("§cValeur invalide");
            return -1;
        }
    }

    private String safeName(OfflinePlayer p) {
        return p.getName() != null ? p.getName() : "Inconnu";
    }
}