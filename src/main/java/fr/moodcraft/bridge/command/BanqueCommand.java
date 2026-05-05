package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.gui.BankGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanqueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande uniquement pour les joueurs.");
            return true;
        }

        Player p = (Player) sender;

        // 🔓 Ouvre le menu banque
        BankGUI.open(p);

        return true;
    }
}