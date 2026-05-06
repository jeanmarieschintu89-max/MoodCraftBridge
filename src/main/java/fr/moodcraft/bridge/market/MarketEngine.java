package fr.moodcraft.bridge.market;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.manager.PriceUpdater;

public final class MarketEngine {

    public static double getPrice(String item) {
        return Math.max(1, MarketState.getPrice(item));
    }

    public static void recordSell(String item, int amount) {
        applySell(item, amount);
    }

    public static void recordBuy(String item, int amount) {
        applyBuy(item, amount);
    }

    public static void applyBuy(String item, int amount) {
        MarketState.buy.merge(item, (double) amount, Double::sum);
    }

    public static void applySell(String item, int amount) {
        MarketState.sell.merge(item, (double) amount, Double::sum);
    }

    public static void reload() {
        Main.getInstance().reloadConfig();
    }

    public static void reset() {

        for (String item : MarketState.base.keySet()) {

            double base = MarketState.base.getOrDefault(item, 1.0);

            MarketState.setPrice(item, base);

            MarketState.stock.put(item, 0.0);
            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);

            // 🔥 synchro reset
            PriceUpdater.updateItem(item, base);
        }
    }

    public static void tick() {

        var cfg = Main.getInstance().getConfig();

        //
        // ⚙ ENGINE CONFIG
        //

        double baseReturn = cfg.getDouble("engine.base_return", 0.002);
        double maxChangeFactor = cfg.getDouble("engine.max_change", 0.35);
        double activityCapFactor = cfg.getDouble("engine.activity_cap", 0.08);
        double stockDecay = cfg.getDouble("engine.stock_decay", 0.92);

        double minFactor = cfg.getDouble("engine.min_price_factor", 0.45);
        double maxFactor = cfg.getDouble("engine.max_price_factor", 3.5);

        double buyMultiplier = cfg.getDouble("engine.buy_multiplier", 1.45);
        double sellMultiplier = cfg.getDouble("engine.sell_multiplier", 1.0);

        //
        // 🌟 RARITY
        //

        boolean rarityEnabled = cfg.getBoolean("engine.rarity.enabled", true);

        double rarityBoost =
                cfg.getDouble("engine.rarity.boost", 0.004);

        double rarityExp =
                cfg.getDouble("engine.rarity.exponent", 1.15);

        double rarityMax =
                cfg.getDouble("engine.rarity.max_boost", 0.08);

        //
        // 🔄 ITEMS
        //

        for (String item : MarketState.base.keySet()) {

            double price =
                    Math.max(1, MarketState.getPrice(item));

            double base =
                    MarketState.base.getOrDefault(item, price);

            double stock =
                    MarketState.stock.getOrDefault(item, 0.0);

            double buy =
                    MarketState.buy.getOrDefault(item, 0.0);

            double sell =
                    MarketState.sell.getOrDefault(item, 0.0);

            //
            // 📦 STOCK
            //

            stock += buy * buyMultiplier;
            stock -= sell * sellMultiplier;

            //
            // 📊 ACTIVITY
            //

            double safeStock = Math.max(0, stock);

            double coef =
                    MarketState.activity.getOrDefault(item, 0.05);

            double activity =
                    Math.sqrt(safeStock + 1) * coef;

            double maxActivity =
                    price * activityCapFactor;

            if (activity > maxActivity) {
                activity = maxActivity;
            }

            price += (
                    (buy * buyMultiplier)
                            - (sell * sellMultiplier)
            ) * activity;

            //
            // 🌟 RARITY BOOST
            //

            if (rarityEnabled) {

                double rare =
                        MarketState.rarity.getOrDefault(item, 10.0);

                if (stock < rare) {

                    double boost = rarityBoost;
                    double exponent = rarityExp;
                    double maxBoost = rarityMax;

                    String path = "rarity_settings." + item;

                    if (cfg.contains(path)) {

                        boost =
                                cfg.getDouble(path + ".boost", boost);

                        exponent =
                                cfg.getDouble(path + ".exponent", exponent);

                        maxBoost =
                                cfg.getDouble(path + ".max_boost", maxBoost);
                    }

                    double ratio =
                            (rare - stock) / rare;

                    double calc =
                            Math.pow(ratio, exponent) * boost;

                    if (calc > maxBoost) {
                        calc = maxBoost;
                    }

                    price += base * calc;
                }
            }

            //
            // 💥 IMPACT
            //

            double div =
                    Math.max(1,
                            MarketState.impact.getOrDefault(item, 6.0));

            double delta =
                    (
                            (buy * buyMultiplier)
                                    - (sell * sellMultiplier)
                    ) / div;

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
            // 🔄 RETOUR PRIX BASE
            //

            price += (base - price) * baseReturn;

            //
            // 🧹 STOCK DECAY
            //

            stock *= stockDecay;

            if (stock > 10000) {
                stock = 10000;
            }

            if (stock < -10000) {
                stock = -10000;
            }

            MarketState.stock.put(item, stock);

            //
            // 🧱 LIMITES
            //

            double min = base * minFactor;
            double max = base * maxFactor;

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

            MarketState.setPrice(item, price);

            // 🔥 QUICKSHOP
            PriceUpdater.updateItem(item, price);

            //
            // 🔁 RESET TICK
            //

            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);
        }
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}