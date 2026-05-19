package fr.moodcraft.bridge.market;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.manager.PriceUpdater;
import org.bukkit.configuration.ConfigurationSection;

public final class MarketEngine {

    public static double getPrice(String item) {
        return Math.max(1, MarketState.getPrice(item));
    }

    // 📉 Vente joueur via /prix -> offre ↑ -> prix ↓
    public static void recordSell(String item, int amount) {
        applySell(item, amount);
    }

    // 📈 Achat joueur en coffre/shop -> demande ↑ -> prix ↑
    public static void recordBuy(String item, int amount) {
        applyBuy(item, amount);
    }

    // ⛏ Minage joueur -> offre créée ↑ -> prix ↓
    public static void recordMine(String item, int amount) {
        applyMine(item, amount);
    }

    public static void applyBuy(String item, int amount) {
        MarketState.buy.merge(item, (double) amount, Double::sum);
    }

    public static void applySell(String item, int amount) {
        MarketState.sell.merge(item, (double) amount, Double::sum);
    }

    public static void applyMine(String item, int amount) {
        MarketState.mined.merge(item, (double) amount, Double::sum);
    }

    public static void reload() {
        Main.getInstance().reloadConfig();
        reloadMarketConfig(false);
    }

    public static void reloadAndResetPrices() {
        Main.getInstance().reloadConfig();
        reloadMarketConfig(true);
    }

    private static void reloadMarketConfig(boolean resetPrices) {
        reloadSection("base", resetPrices);
        reloadSection("activity");
        reloadSection("impact");
        reloadSection("rarity");
        reloadSection("weight");
    }

    private static void reloadSection(String path) {
        ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection(path);
        if (section == null) return;
        var target = switch (path) {
            case "activity" -> MarketState.activity;
            case "impact" -> MarketState.impact;
            case "rarity" -> MarketState.rarity;
            case "weight" -> MarketState.weight;
            default -> null;
        };
        if (target == null) return;
        target.clear();
        for (String key : section.getKeys(false)) target.put(key, section.getDouble(key));
    }

    private static void reloadSection(String path, boolean resetPrices) {
        ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection(path);
        if (section == null) return;
        MarketState.base.clear();
        for (String key : section.getKeys(false)) {
            double value = section.getDouble(key);
            MarketState.base.put(key, value);
            if (resetPrices || !MarketState.price.containsKey(key)) MarketState.setPrice(key, value);
            MarketState.stock.putIfAbsent(key, 0.0);
            MarketState.buy.putIfAbsent(key, 0.0);
            MarketState.sell.putIfAbsent(key, 0.0);
            MarketState.mined.putIfAbsent(key, 0.0);
        }
        MarketState.price.keySet().removeIf(item -> !MarketState.base.containsKey(item));
        MarketState.stock.keySet().removeIf(item -> !MarketState.base.containsKey(item));
        MarketState.buy.keySet().removeIf(item -> !MarketState.base.containsKey(item));
        MarketState.sell.keySet().removeIf(item -> !MarketState.base.containsKey(item));
        MarketState.mined.keySet().removeIf(item -> !MarketState.base.containsKey(item));
    }

    public static void reset() {
        reloadAndResetPrices();
        for (String item : MarketState.base.keySet()) {
            double base = MarketState.base.getOrDefault(item, 1.0);
            MarketState.setPrice(item, base);
            MarketState.stock.put(item, 100.0);
            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);
            MarketState.mined.put(item, 0.0);
            PriceUpdater.updateItem(item, base);
        }
    }

    public static void tick() {
        var cfg = Main.getInstance().getConfig();

        double baseReturn = cfg.getDouble("engine.base_return", 0.006);
        double maxChangeFactor = cfg.getDouble("engine.max_change", 0.035);
        double activityCapFactor = cfg.getDouble("engine.activity_cap", 0.03);
        double stockDecay = cfg.getDouble("engine.stock_decay", 0.90);
        double minFactor = cfg.getDouble("engine.min_price_factor", 0.45);
        double maxFactor = cfg.getDouble("engine.max_price_factor", 1.35);
        double buyMultiplier = cfg.getDouble("engine.buy_multiplier", 1.35);
        double sellMultiplier = cfg.getDouble("engine.sell_multiplier", 1.0);
        double miningMultiplier = cfg.getDouble("engine.mining_multiplier", 0.75);

        boolean rarityEnabled = cfg.getBoolean("engine.rarity.enabled", true);
        double rarityBoost = cfg.getDouble("engine.rarity.boost", 0.0010);
        double rarityExp = cfg.getDouble("engine.rarity.exponent", 1.05);
        double rarityMax = cfg.getDouble("engine.rarity.max_boost", 0.018);

        for (String item : MarketState.base.keySet()) {
            double price = Math.max(1, MarketState.getPrice(item));
            double base = MarketState.base.getOrDefault(item, price);
            double stock = MarketState.stock.getOrDefault(item, 100.0);

            double buy = MarketState.buy.getOrDefault(item, 0.0);
            double sell = MarketState.sell.getOrDefault(item, 0.0);
            double mined = MarketState.mined.getOrDefault(item, 0.0);

            // Stock réel : achat retire l'offre, vente /prix et minage ajoutent l'offre.
            stock -= buy * buyMultiplier;
            stock += sell * sellMultiplier;
            stock += mined * miningMultiplier;

            double safeStock = Math.max(1, stock + 100);
            double coef = MarketState.activity.getOrDefault(item, 0.003);
            double activity = Math.sqrt(safeStock) * coef;
            double maxActivity = price * activityCapFactor;
            if (activity > maxActivity) activity = maxActivity;

            // Pression prix : achat monte, vente /prix baisse, minage baisse.
            double pressure = (buy * buyMultiplier) - (sell * sellMultiplier) - (mined * miningMultiplier);
            price += pressure * activity;

            if (rarityEnabled) {
                double rare = MarketState.rarity.getOrDefault(item, 100.0);
                if (safeStock < rare) {
                    double boost = rarityBoost;
                    double exponent = rarityExp;
                    double maxBoost = rarityMax;
                    String rarityPath = "rarity_settings." + item;
                    if (cfg.contains(rarityPath)) {
                        boost = cfg.getDouble(rarityPath + ".boost", boost);
                        exponent = cfg.getDouble(rarityPath + ".exponent", exponent);
                        maxBoost = cfg.getDouble(rarityPath + ".max_boost", maxBoost);
                    }
                    double ratio = (rare - safeStock) / rare;
                    if (ratio < 0) ratio = 0;
                    double calc = Math.pow(ratio, exponent) * boost;
                    if (calc > maxBoost) calc = maxBoost;
                    price += base * calc;
                }
            }

            double impact = Math.max(1, MarketState.impact.getOrDefault(item, 50.0));
            double delta = pressure / impact;
            double maxChange = price * maxChangeFactor;
            if (delta > maxChange) delta = maxChange;
            if (delta < -maxChange) delta = -maxChange;
            price += delta;

            price += (base - price) * baseReturn;
            stock *= stockDecay;

            if (stock > 10000) stock = 10000;
            if (stock < -10000) stock = -10000;
            MarketState.stock.put(item, stock);

            double min = base * minFactor;
            double max = base * maxFactor;
            if (price < min) price = min;
            if (price > max) price = max;
            if (price < 1) price = 1;

            price = round(price);
            MarketState.setPrice(item, price);
            PriceUpdater.updateItem(item, price);

            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);
            MarketState.mined.put(item, 0.0);
        }
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}