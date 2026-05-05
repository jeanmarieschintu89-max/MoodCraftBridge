package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class TransactionsCommand implements CommandExecutor {

    private static final int MAX_LINES = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cCommande joueur uniquement.");
            return true;
        }

        File file = new File(Main.getInstance().getDataFolder(), "transactions.log");

        if (!file.exists()) {
            p.sendMessage("§cAucune transaction enregistrée.");
            return true;
        }

        try {

            List<String> lines = Files.readAllLines(file.toPath());

            if (lines.isEmpty()) {
                p.sendMessage("§7Aucune transaction.");
                return true;
            }

            Collections.reverse(lines);

            String target = p.getName();

            // 🔥 ADMIN peut voir autre joueur
            if (args.length > 0 && p.hasPermission("econ.admin")) {
                target = args[0];
            }

            p.sendMessage("§6📜 Transactions de §e" + target);

            int count = 0;

            for (String line : lines) {

                if (!line.contains(target)) continue;

                p.sendMessage("§7" + line);
                count++;

                if (count >= MAX_LINES) break;
            }

            if (count == 0) {
                p.sendMessage("§7Aucune transaction trouvée.");
            }

        } catch (IOException e) {
            p.sendMessage("§cErreur lecture fichier.");
            e.printStackTrace();
        }

        return true;
    }
}