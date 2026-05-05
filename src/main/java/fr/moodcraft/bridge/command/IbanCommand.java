package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.IbanManager;
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

            String iban = IbanManager.get(p.getUniqueId());

            if (iban == null) {
                p.sendMessage("§cAucun IBAN défini.");
                p.sendMessage("§7Utilise le menu banque pour en créer un.");
                return true;
            }

            send(p, p.getName(), iban);
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

            String iban = IbanManager.get(target.getUniqueId());

            if (iban == null) {
                p.sendMessage("§cCe joueur n'a pas d'IBAN.");
                return true;
            }

            send(p, target.getName(), iban);
            return true;
        }

        return true;
    }

    private void send(Player p, String name, String iban) {
        p.sendMessage("§8━━━━━━━━━━━━━━━━━━");
        p.sendMessage("§e🏦 Banque MoodCraft");
        p.sendMessage("§7Titulaire: §e" + name);
        p.sendMessage("§7IBAN: §b" + iban);
        p.sendMessage("§8━━━━━━━━━━━━━━━━━━");
    }
}