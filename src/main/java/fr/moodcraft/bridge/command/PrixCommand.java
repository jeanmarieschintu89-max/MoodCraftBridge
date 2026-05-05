package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.gui.PriceGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PrixCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cCommande joueur uniquement.");
            return true;
        }

        PriceGUI.open(p);

        return true;
    }
}