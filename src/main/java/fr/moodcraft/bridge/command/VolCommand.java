package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.VolManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VolCommand implements CommandExecutor {

    private static final long MAX_ITEM_SECONDS = 30L * 60L;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!VolManager.isEnabled()) {
            sender.sendMessage("§c✖ §fLe système de vol MoodCraft n'est pas activé.");
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§c✖ §fCommande joueur uniquement.");
                return true;
            }
            VolManager.toggle(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "temps", "time", "restant" -> time(sender);
            case "item" -> item(sender, args);
            case "give", "add" -> give(sender, args);
            case "remove", "retire" -> remove(sender, args);
            case "reset" -> reset(sender, args);
            default -> help(sender);
        }
        return true;
    }

    private void time(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return;
        }
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fVol ✦ §8-----");
        sender.sendMessage("§8• §7Temps restant: §e" + VolManager.format(VolManager.remaining(player.getUniqueId())));
        sender.sendMessage("§8-----------------------------");
    }

    private void item(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3) {
            sender.sendMessage("§c✖ §fUtilisation: §e/vol item <joueur> <temps>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§c✖ §fJoueur introuvable ou hors ligne: §e" + args[1]);
            return;
        }
        long seconds = VolManager.parse(args[2]);
        if (seconds <= 0) {
            sender.sendMessage("§c✖ §fTemps invalide. Exemple: §e5m§f, §e10m§f, §e30m");
            return;
        }
        if (seconds > MAX_ITEM_SECONDS) {
            sender.sendMessage("§c✖ §fLes bons de vol sont limités à §e30 min§f maximum.");
            return;
        }
        ItemStack reward = VolManager.item(seconds);
        target.getInventory().addItem(reward).values().forEach(left -> target.getWorld().dropItemNaturally(target.getLocation(), left));
        sender.sendMessage("§a✔ §fBon de vol donné à §e" + target.getName() + " §8(§e" + VolManager.format(seconds) + "§8).");
        target.sendMessage("§a✔ §fTu as reçu un bon de vol: §e" + VolManager.format(seconds));
    }

    private void give(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3) {
            sender.sendMessage("§c✖ §fUtilisation: §e/vol give <joueur> <temps>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§c✖ §fJoueur introuvable ou hors ligne: §e" + args[1]);
            return;
        }
        long seconds = VolManager.parse(args[2]);
        if (seconds <= 0) {
            sender.sendMessage("§c✖ §fTemps invalide. Exemple: §e5m§f, §e10m§f, §e30m");
            return;
        }
        VolManager.add(target.getUniqueId(), seconds);
        sender.sendMessage("§a✔ §fTemps ajouté à §e" + target.getName() + " §8(§e" + VolManager.format(seconds) + "§8).");
    }

    private void remove(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3) {
            sender.sendMessage("§c✖ §fUtilisation: §e/vol remove <joueur> <temps>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§c✖ §fJoueur introuvable ou hors ligne: §e" + args[1]);
            return;
        }
        long seconds = VolManager.parse(args[2]);
        if (seconds <= 0) {
            sender.sendMessage("§c✖ §fTemps invalide.");
            return;
        }
        VolManager.remove(target.getUniqueId(), seconds);
        sender.sendMessage("§a✔ §fTemps retiré à §e" + target.getName() + "§f.");
    }

    private void reset(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 2) {
            sender.sendMessage("§c✖ §fUtilisation: §e/vol reset <joueur>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§c✖ §fJoueur introuvable ou hors ligne: §e" + args[1]);
            return;
        }
        VolManager.reset(target.getUniqueId());
        sender.sendMessage("§a✔ §fTemps de vol remis à zéro pour §e" + target.getName() + "§f.");
    }

    private boolean admin(CommandSender sender) {
        if (sender.hasPermission("moodcraft.admin") || sender.hasPermission("moodcraftbridge.vol.admin")) return true;
        sender.sendMessage("§c✖ §fAccès réservé à l'administration MoodCraft.");
        return false;
    }

    private void help(CommandSender sender) {
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §fVol ✦ §8-----");
        sender.sendMessage("§e➜ §7/vol §8- §fActiver/désactiver le vol");
        sender.sendMessage("§e➜ §7/vol temps §8- §fVoir le temps restant");
        if (sender.hasPermission("moodcraft.admin") || sender.hasPermission("moodcraftbridge.vol.admin")) {
            sender.sendMessage("§e➜ §7/vol item <joueur> <temps> §8- §fDonner un bon de vol");
            sender.sendMessage("§e➜ §7/vol give <joueur> <temps> §8- §fAjouter du temps");
            sender.sendMessage("§e➜ §7/vol remove <joueur> <temps> §8- §fRetirer du temps");
            sender.sendMessage("§e➜ §7/vol reset <joueur> §8- §fRemettre à zéro");
        }
        sender.sendMessage("§8-----------------------------");
    }
}
