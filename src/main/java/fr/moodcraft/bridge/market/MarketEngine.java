package fr.moodcraft.bridge.market;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.manager.PriceUpdater;

public final class MarketEngine {

    public static double getPrice(String item) {
        return Math.max(1, MarketState.getPrice(item));
    }

    //
    // 📉 Vente joueur → offre ↑ → prix ↓
    //

    public static void recordSell(String item, int amount) {
        applySell(item, amount);
    }

    //
    // 📈 Achat joueur → offre ↓ → prix ↑
    //

    public static void recordBuy(String item, int amount) {
        applyBuy(item, amount);
    }

    public static void applyBuy(String item, int amount) {

        MarketState.buy.merge(
                item,
                (double) amount,
                Double::sum
        );
    }

    public static void applySell(String item, int amount) {

        MarketState.sell.merge(
                item,
                (double) amount,
                Double::sum
        );
    }

    public static void reload() {
        Main.getInstance().reloadConfig();
    }

    //
    // 🔄 RESET
    //

    public static void reset() {

        for (String item : MarketState.base.keySet()) {

            double base =
                    MarketState.base.getOrDefault(
                            item,
                            1.0
                    );

            MarketState.setPrice(item, base);

            //
            // 📦 STOCK NEUTRE
            //

            MarketState.stock.put(item, 100.0);

            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);

            //
            // 🔥 QUICKSHOP SYNC
            //

            PriceUpdater.updateItem(item, base);
        }
    }

    //
    // ⚙ TICK ÉCONOMIQUE
    //

    public static void tick() {

        var cfg =
                Main.getInstance().getConfig();

        //
        // ⚙ CONFIG
        //

        double baseReturn =
                cfg.getDouble(
                        "engine.base_return",
                        0.012
                );

        double maxChangeFactor =
                cfg.getDouble(
                        "engine.max_change",
                        0.035
                );

        double activityCapFactor =
                cfg.getDouble(
                        "engine.activity_cap",
                        0.03
                );

        double stockDecay =
                cfg.getDouble(
                        "engine.stock_decay",
                        0.90
                );

        double minFactor =
                cfg.getDouble(
                        "engine.min_price_factor",
                        0.45
                );

        double maxFactor =
                cfg.getDouble(
                        "engine.max_price_factor",
                        1.5
                );

        double buyMultiplier =
                cfg.getDouble(
                        "engine.buy_multiplier",
                        1.35
                );

        double sellMultiplier =
                cfg.getDouble(
                        "engine.sell_multiplier",
                        1.0
                );

        //
        // 🌟 RARETÉ
        //

        boolean rarityEnabled =
                cfg.getBoolean(
                        "engine.rarity.enabled",
                        true
                );

        double rarityBoost =
                cfg.getDouble(
                        "engine.rarity.boost",
                        0.0010
                );

        double rarityExp =
                cfg.getDouble(
                        "engine.rarity.exponent",
                        1.05
                );

        double rarityMax =
                cfg.getDouble(
                        "engine.rarity.max_boost",
                        0.018
                );

        //
        // 🔄 ITEMS
        //

        for (String item : MarketState.base.keySet()) {

            //
            // 💰 PRIX
            //

            double price =
                    Math.max(
                            1,
                            MarketState.getPrice(item)
                    );

            double base =
                    MarketState.base.getOrDefault(
                            item,
                            price
                    );

            //
            // 📦 STOCK
            //

            double stock =
                    MarketState.stock.getOrDefault(
                            item,
                            100.0
                    );

            //
            // 🛒 ACTIVITÉ
            //

            double buy =
                    MarketState.buy.getOrDefault(
                            item,
                            0.0
                    );

            double sell =
                    MarketState.sell.getOrDefault(
                            item,
                            0.0
                    );

            //
            // 📦 ÉCONOMIE RÉELLE
            //
            // achat = stock ↓
            // vente = stock ↑
            //

            stock -= buy * buyMultiplier;

            stock += sell * sellMultiplier;

            //
            // 🔒 SAFE STOCK
            //

            double safeStock =
                    Math.max(
                            1,
                            stock + 100
                    );

            //
            // 📊 ACTIVITÉ
            //

            double coef =
                    MarketState.activity.getOrDefault(
                            item,
                            0.003
                    );

            double activity =
                    Math.sqrt(safeStock) * coef;

            double maxActivity =
                    price * activityCapFactor;

            if (activity > maxActivity) {
                activity = maxActivity;
            }

            //
            // 📈 DEMANDE RÉELLE
            //

            double pressure =
                    (
                            (buy * buyMultiplier)
                                    - (sell * sellMultiplier)
                    );

            price += pressure * activity;

            //
            // 🌟 BOOST RARETÉ
            //

            if (rarityEnabled) {

                double rare =
                        MarketState.rarity.getOrDefault(
                                item,
                                100.0
                        );

                if (safeStock < rare) {

                    double boost = rarityBoost;
                    double exponent = rarityExp;
                    double maxBoost = rarityMax;

                    String path =
                            "rarity_settings." + item;

                    if (cfg.contains(path)) {

                        boost =
                                cfg.getDouble(
                                        path + ".boost",
                                        boost
                                );

                        exponent =
                                cfg.getDouble(
                                        path + ".exponent",
                                        exponent
                                );

                        maxBoost =
                                cfg.getDouble(
                                        path + ".max_boost",
                                        maxBoost
                                );
                    }

                    double ratio =
                            (rare - safeStock) / rare;

                    if (ratio < 0)
                        ratio = 0;

                    double calc =
                            Math.pow(ratio, exponent)
                                    * boost;

                    if (calc > maxBoost) {
                        calc = maxBoost;
                    }

                    price += base * calc;
                }
            }

            //
            // 💥 IMPACT
            //

            double impact =
                    Math.max(
                            1,
                            MarketState.impact.getOrDefault(
                                    item,
                                    50.0
                            )
                    );

            double delta =
                    pressure / impact;

            double maxChange =
                    price * maxChangeFactor;

            if (delta > maxChange) {
                delta = maxChange;
            }

            if (delta < -maxChange) {
                delta = -maxChange;
            }

            price += delta;

            //
            // 🔄 RETOUR PROGRESSIF
            //

            price +=
                    (base - price)
                            * baseReturn;

            //
            // 🧹 STOCK DECAY
            //

            stock *= stockDecay;

            //
            // 🔒 LIMITES STOCK
            //

            if (stock > 10000) {
                stock = 10000;
            }

            if (stock < -10000) {
                stock = -10000;
            }

            MarketState.stock.put(
                    item,
                    stock
            );

            //
            // 🧱 LIMITES PRIX
            //

            double min =
                    base * minFactor;

            double max =
                    base * maxFactor;

            if (price < min) {
                price = min;
            }

            if (price > max) {
                price = max;
            }

            if (price < 1) {
                price = 1;
            }

            //
            // 💹 FINAL
            //

            price = round(price);

            MarketState.setPrice(
                    item,
                    price
            );

            //
            // 🔥 QUICKSHOP
            //

            PriceUpdater.updateItem(
                    item,
                    price
            );

            //
            // 🔁 RESET TICK
            //

            MarketState.buy.put(item, 0.0);

            MarketState.sell.put(item, 0.0);
        }
    }

    //
    // 🔢 ROUND
    //

    private static double round(double v) {

        return Math.round(v * 100.0)
                / 100.0;
    }
}