package fr.moodcraft.bridge.listener;

import com.ghostchu.quickshop.api.event.economy.ShopPurchaseEvent;

import fr.moodcraft.bridge.manager.PriceUpdater;
import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;
import fr.moodcraft.bridge.util.TransactionLogger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShopListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBuy(ShopPurchaseEvent event) {

        if (event == null || event.getShop() == null || event.getShop().getItem() == null) return;

        int amount = Math.max(1, event.getAmount());

        String item = event.getShop().getItem().getType().name().toLowerCase();
        if (!PriceUpdater.ALLOWED.contains(item)) return;

        // =========================
        // 👤 JOUEUR SAFE
        // =========================
        String player = "Inconnu";

        if (event.getPurchaser() != null) {
            var offline = Bukkit.getOfflinePlayer(event.getPurchaser().getUniqueId());
            if (offline.getName() != null) {
                player = offline.getName();
            }
        }

        // =========================
        // 💰 PRIX FIABLE
        // =========================
        double unitPrice = MarketEngine.getPrice(item);
        double total = unitPrice * amount;

        boolean isSellingToShop = event.getShop().isBuying();

        // =========================
        // 📄 LOG CENTRALISÉ
        // =========================
        if (isSellingToShop) {
            TransactionLogger.log(player, "SELL", total, item + " x" + amount);
        } else {
            TransactionLogger.log(player, "BUY", total, item + " x" + amount);
        }

        Bukkit.getLogger().info("[Market] " + player + " " +
                (isSellingToShop ? "vend" : "achète") +
                " " + item + " x" + amount + " (" + total + ")");

        // =========================
        // 📈 IMPACT INTELLIGENT
        // =========================
        int boosted = Math.max(1, (int) Math.sqrt(amount) * 3);

        if (isSellingToShop) {

            MarketEngine.applySell(item, boosted);
            MarketState.stock.merge(item, (double) boosted, Double::sum);

        } else {

            MarketEngine.applyBuy(item, boosted);
            MarketState.stock.merge(item, -(double) boosted, Double::sum);
        }

        // =========================
        // 🔒 CLAMP STOCK
        // =========================
        double stock = MarketState.stock.getOrDefault(item, 0.0);
        stock = Math.max(-10000, Math.min(10000, stock));
        MarketState.stock.put(item, stock);

        // =========================
        // 🔄 UPDATE SHOP
        // =========================
        PriceUpdater.updateSingle(event.getShop(), item);
    }
}