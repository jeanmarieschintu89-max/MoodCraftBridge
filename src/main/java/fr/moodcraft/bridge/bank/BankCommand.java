package fr.moodcraft.bridge.bank;

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
            amount = Double.parseDouble(args[1].replace(",", "."));
        } catch (Exception e) {
            sender.sendMessage("§cMontant invalide");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage("§cMontant invalide");
            return true;
        }

        BankStorage.add(uuid, amount);

        sender.sendMessage("§a✔ Ajout de §e" + amount + "€ §aau compte " + uuid);

        return true;
    }
}