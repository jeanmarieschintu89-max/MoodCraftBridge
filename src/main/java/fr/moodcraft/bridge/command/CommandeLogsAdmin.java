package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.TransactionManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandeLogsAdmin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (!p.hasPermission("moodcraft.admin")) {
            p.sendMessage("§cPermission refusée");
            return true;
        }

        List<String> list = TransactionManager.getGlobal();

        p.sendMessage("§8§m-----------------------------");
        p.sendMessage("§c✦ §fLogs bancaires");

        if (list.isEmpty()) {
            p.sendMessage("§7Aucun log");
        } else {
            for (int i = 0; i < Math.min(10, list.size()); i++) {
                p.sendMessage(" " + list.get(i));
            }
        }

        p.sendMessage("§8§m-----------------------------");

        return true;
    }
}