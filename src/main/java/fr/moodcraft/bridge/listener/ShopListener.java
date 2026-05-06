package fr.moodcraft.bridge.listener;

import com.ghostchu.quickshop.api.event.economy.ShopPurchaseEvent;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.util.TransactionLogger;

import fr.moodcraft.bridge.manager.PriceUpdater;

import org.bukkit.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShopListener implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onBuy(ShopPurchaseEvent event) {

        if (event == null
                || event.getShop() == null
                || event.getShop().getItem() == null) {
            return;
        }

        //
        // 📦 ITEM
        //

        String item =
                event.getShop()
                        .getItem()
                        .getType()
                        .name()
                        .toLowerCase();

        if (!PriceUpdater.ALLOWED.contains(item))
            return;

        //
        // 📊 AMOUNT
        //

        int amount =
                Math.max(1, event.getAmount());

        //
        // 👤 PLAYER
        //

        String player = "Inconnu";

        if (event.getPurchaser() != null) {

            var offline =
                    Bukkit.getOfflinePlayer(
                            event.getPurchaser()
                                    .getUniqueId()
                    );

            if (offline.getName() != null) {
                player = offline.getName();
            }
        }

        //
        // 💰 PRICE
        //

        double unit =
                MarketEngine.getPrice(item);

        double total =
                unit * amount;

        //
        // 🏪 TYPE
        //

        boolean isSellingToShop =
                event.getShop().isBuying();

        //
        // 📄 LOG
        //

        TransactionLogger.log(

                player,

                isSellingToShop
                        ? "SELL"
                        : "BUY",

                total,

                item + " x" + amount
        );

        //
        // 📈 ECONOMY IMPACT
        //

        int impact =
                Math.max(
                        1,
                        (int) Math.sqrt(amount)
                );

        if (isSellingToShop) {

            //
            // 📉 joueur vend au shop
            //

            MarketEngine.recordSell(
                    item,
                    impact
            );

        } else {

            //
            // 📈 joueur achète au shop
            //

            MarketEngine.recordBuy(
                    item,
                    impact * 2
            );
        }
    }
}