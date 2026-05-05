package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;
import fr.moodcraft.bridge.util.ItemNormalizer;

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

        // ❌ item inconnu
        if (item == null || !MarketState.price.containsKey(item)) return;

        ItemStack tool = player.getInventory().getItemInMainHand();

        // 🔒 Silk Touch → ignore
        if (tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        int amount = 1;

        // 💎 Fortune
        if (tool != null && tool.containsEnchantment(Enchantment.FORTUNE)) {

            int level = tool.getEnchantmentLevel(Enchantment.FORTUNE);

            // 🔥 petite amélioration RNG
            amount = 1 + (int) (Math.random() * (level + 1));
        }

        // 📊 IMPACT MARCHÉ (VENTE)
        MarketEngine.recordSell(item, amount);
    }
}