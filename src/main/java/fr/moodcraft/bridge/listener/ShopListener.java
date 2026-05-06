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

        //
        // 🔒 SAFE
        //

        if (event == null
                || event.getShop() == null
                || event.getShop().getItem() == null) {
            return;
        }

        //
        // 🏦 ADMINSHOP UNIQUEMENT
        //

        if (!event.getShop().isUnlimited()) {
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
                Math.max(
                        1,
                        event.getAmount()
                );

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
        // 📈 IMPACT ÉCONOMIQUE
        //

        int impact =

                Math.max(
                        1,
                        (int) Math.sqrt(amount)
                );

        //
        // 📉 JOUEUR VEND AU SHOP
        //

        if (isSellingToShop) {

            MarketEngine.recordSell(
                    item,
                    impact
            );

            Bukkit.getLogger().info(

                    "[MoodCraft Market] SELL "
                            + item
                            + " x"
                            + amount
                            + " impact="
                            + impact
            );
        }

        //
        // 📈 JOUEUR ACHÈTE AU SHOP
        //

        else {

            MarketEngine.recordBuy(
                    item,
                    impact * 2
            );

            Bukkit.getLogger().info(

                    "[MoodCraft Market] BUY "
                            + item
                            + " x"
                            + amount
                            + " impact="
                            + (impact * 2)
            );
        }
    }
}