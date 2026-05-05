package fr.moodcraft.bridge.bank;

import fr.moodcraft.bank.BankAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("moodcraft.admin")) {
            sender.sendMessage("§c❌ Permission refusée");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bank <uuid> <montant>");
            return true;
        }

        String uuid = args[0];

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (Exception e) {
            sender.sendMessage("§cMontant invalide");
            return true;
        }

        BankAPI.add(uuid, amount);

        sender.sendMessage("§a✔ Banque modifiée");
        sender.sendMessage("§7UUID: §f" + uuid);
        sender.sendMessage("§7Ajout: §e" + amount + "€");

        return true;
    }
}