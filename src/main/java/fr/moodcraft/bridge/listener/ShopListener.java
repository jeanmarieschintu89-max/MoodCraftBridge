package fr.moodcraft.bridge.listener;

import com.ghostchu.quickshop.api.event.economy.ShopPurchaseEvent;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.util.TransactionLogger;
import fr.moodcraft.bridge.util.ItemNormalizer;

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
                || event.getShop().getItem() == null
                || event.getShop().getLocation() == null
                || event.getShop().getLocation().getWorld() == null) {

            return;
        }

        //
        // 🌍 MONDE ADMINSHOP
        //

        String world =

                event.getShop()
                        .getLocation()
                        .getWorld()
                        .getName();

        //
        // ❌ ignore hors spawn
        //

        if (!world.equalsIgnoreCase("world")) {
            return;
        }

        //
        // 📦 ITEM NORMALISÉ
        //

        String item =

                ItemNormalizer.normalize(

                        event.getShop()
                                .getItem()
                                .getType()
                );

        //
        // ❌ item invalide
        //

        if (item == null)
            return;

        //
        // ❌ item non autorisé
        //

        if (!PriceUpdater.ALLOWED.contains(item))
            return;

        //
        // 📊 QUANTITÉ
        //

        int amount =

                Math.max(
                        1,
                        event.getAmount()
                );

        //
        // 👤 JOUEUR
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
        // 💰 PRIX
        //

        double unit =
                MarketEngine.getPrice(item);

        double total =
                unit * amount;

        //
        // 🏪 QUICKSHOP TYPE
        //

        boolean isSellingToShop =
                event.getShop().isBuying();

        //
        // 🧪 DEBUG
        //

        Bukkit.getLogger().info(

                "[MoodCraft DEBUG] "

                        + item

                        + " | amount="

                        + amount

                        + " | buying="

                        + isSellingToShop

                        + " | player="

                        + player
        );

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
        // 📈 IMPACT MARCHÉ
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