package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cCommande joueur uniquement.");
            return true;
        }

        // =========================
        // 👤 /iban
        // =========================
        if (args.length == 0) {

            String iban = generateIban(p.getUniqueId());

            p.sendMessage("§8━━━━━━━━━━━━━━━━━━");
            p.sendMessage("§e🏦 Banque MoodCraft");
            p.sendMessage("§7Titulaire: §e" + p.getName());
            p.sendMessage("§7IBAN: §b" + iban);
            p.sendMessage("§8━━━━━━━━━━━━━━━━━━");

            return true;
        }

        // =========================
        // 👑 /iban <joueur>
        // =========================
        if (args.length == 1) {

            if (!p.hasPermission("econ.admin")) {
                p.sendMessage("§c❌ Permission refusée.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                p.sendMessage("§cJoueur introuvable.");
                return true;
            }

            String iban = generateIban(target.getUniqueId());

            p.sendMessage("§8━━━━━━━━━━━━━━━━━━");
            p.sendMessage("§e🏦 Banque MoodCraft");
            p.sendMessage("§7Titulaire: §e" + target.getName());
            p.sendMessage("§7IBAN: §b" + iban);
            p.sendMessage("§8━━━━━━━━━━━━━━━━━━");

            return true;
        }

        return true;
    }

    // =========================
    // 🏦 GÉNÉRATION IBAN
    // =========================
    private String generateIban(UUID uuid) {

        String base = uuid.toString().replace("-", "").substring(0, 16);

        return "MC" + base.toUpperCase();
    }
}