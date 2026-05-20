package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.FortuneService;
import fr.moodcraft.bridge.manager.FortuneService.FortuneResult;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FortuneCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("econ.admin") && !sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 1) {
            header(sender, "Fortune");
            sender.sendMessage("§e➜ §7Utilisation : §e/fortune <joueur>");
            footer(sender);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null || target.getUniqueId() == null) {
            error(sender, "Joueur introuvable.");
            return true;
        }

        FortuneResult result = FortuneService.calculate(target);

        header(sender, "Fortune de " + result.name());
        sender.sendMessage(line("Argent de poche", FortuneService.money(result.pocket())));
        sender.sendMessage(line("Banque personnelle", FortuneService.money(result.personalBank())));

        if (result.mayor()) {
            sender.sendMessage(line("Banque ville", FortuneService.money(result.townBank()) + " §8(§b" + result.townName() + "§8)"));
        } else if (result.townName() != null) {
            sender.sendMessage(line("Banque ville", "§8non comptée §7(§f" + result.townName() + "§7, pas maire§8)"));
        } else {
            sender.sendMessage(line("Banque ville", "§80€ §7(aucune ville)"));
        }

        if (result.hasBusiness()) {
            sender.sendMessage(line("Banque entreprise", FortuneService.money(result.businessBank()) + " §8(§d" + result.businessName() + "§8)"));
        } else {
            sender.sendMessage(line("Banque entreprise", "§80€ §7(aucune entreprise dirigée)"));
        }

        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("§6✦ §fTotal estimé : §a" + FortuneService.money(result.total()));
        footer(sender);

        return true;
    }

    private String line(String label, String value) {
        return "§8• §7" + label + " : §f" + value;
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Fortune");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §f" + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }
}