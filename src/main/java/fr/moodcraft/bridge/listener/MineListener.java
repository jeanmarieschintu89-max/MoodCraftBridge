package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.util.ItemNormalizer;
import fr.moodcraft.bridge.market.MarketAPI;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MineListener implements Listener {

    @EventHandler
    public void onMine(BlockBreakEvent e) {

        if (e.isCancelled()) return;

        var block = e.getBlock();
        var player = e.getPlayer();

        String item = ItemNormalizer.normalizeBlock(block);

        if (item == null || !MarketAPI.hasItem(item)) return;

        ItemStack tool = player.getInventory().getItemInMainHand();

        // 🔒 Silk Touch → ignore
        if (tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        int amount = 1;

        // 💎 Fortune
        if (tool != null && tool.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {

            int level = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            amount = 1 + (int)(Math.random() * level);
        }

        // 📊 Envoie au Market
        MarketAPI.testSell(item, amount);
    }
}