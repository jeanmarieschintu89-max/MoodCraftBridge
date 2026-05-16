package fr.moodcraft.bridge.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public class FurnaceCommand implements CommandExecutor {

    private static final String PERMISSION = "moodcraftbridge.furnace";
    private static final String HEADER = "§8----- §6✦ MoodCraft ✦ §8-----";
    private static final String FOOTER = "§8-----------------------------";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(HEADER);
            player.sendMessage("§c■ §fCommande réservée au grade §eVIP§f.");
            player.sendMessage("§8• §7Permission : §e" + PERMISSION);
            player.sendMessage(FOOTER);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 0.8f);
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR || hand.getAmount() <= 0) {
            player.sendMessage(HEADER);
            player.sendMessage("§c■ §fTiens un item à cuire ou fondre dans ta main.");
            player.sendMessage(FOOTER);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 0.8f);
            return true;
        }

        ItemStack result = findFurnaceResult(hand);
        if (result == null || result.getType() == Material.AIR) {
            player.sendMessage(HEADER);
            player.sendMessage("§c■ §fCet item ne peut pas être cuit ou fondu.");
            player.sendMessage("§8• §7Item : §e" + readable(hand.getType()));
            player.sendMessage(FOOTER);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 0.8f);
            return true;
        }

        int amount = hand.getAmount();
        ItemStack cooked = result.clone();
        cooked.setAmount(Math.max(1, result.getAmount()) * amount);
        player.getInventory().setItemInMainHand(cooked);

        player.sendMessage(HEADER);
        player.sendMessage("§a▶ §fCuisson terminée.");
        player.sendMessage("§8• §7Avant : §e" + amount + "x " + readable(hand.getType()));
        player.sendMessage("§8• §7Après : §a" + cooked.getAmount() + "x " + readable(cooked.getType()));
        player.sendMessage(FOOTER);
        player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.8f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.35f, 1.5f);
        return true;
    }

    private ItemStack findFurnaceResult(ItemStack input) {
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (!(recipe instanceof FurnaceRecipe furnaceRecipe)) continue;
            if (!furnaceRecipe.getInputChoice().test(input)) continue;
            return furnaceRecipe.getResult();
        }
        return null;
    }

    private String readable(Material material) {
        if (material == null) return "Inconnu";
        String raw = material.name().toLowerCase().replace('_', ' ');
        return raw.substring(0, 1).toUpperCase() + raw.substring(1);
    }
}
