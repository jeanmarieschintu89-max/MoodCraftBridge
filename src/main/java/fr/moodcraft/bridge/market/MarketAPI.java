package fr.moodcraft.bridge.market;

import org.bukkit.Bukkit;

import java.util.Set;

public class MarketAPI {

    // =========================
    // 🔄 RELOAD
    // =========================
    public static void reload() {

        Main plugin = Main.getInstance();

        plugin.reloadConfig();

        MarketState.base.clear();
        MarketState.price.clear();
        MarketState.stock.clear();
        MarketState.buy.clear();
        MarketState.sell.clear();

        MarketState.activity.clear();
        MarketState.impact.clear();
        MarketState.rarity.clear();
        MarketState.weight.clear();

        if (plugin.getConfig().getConfigurationSection("base") != null) {
            for (String key : plugin.getConfig().getConfigurationSection("base").getKeys(false)) {

                double value = plugin.getConfig().getDouble("base." + key);

                MarketState.base.put(key, value);
                MarketState.price.put(key, value);
                MarketState.stock.put(key, 0.0);
                MarketState.buy.put(key, 0.0);
                MarketState.sell.put(key, 0.0);
            }
        }

        load(plugin, "activity", MarketState.activity);
        load(plugin, "impact", MarketState.impact);
        load(plugin, "rarity", MarketState.rarity);
        load(plugin, "weight", MarketState.weight);

        MarketEngine.tick();
    }

    // =========================
    // ♻ RESET
    // =========================
    public static void reset() {

        Bukkit.broadcastMessage("§c⚠ Reset économique en cours...");

        MarketState.price.clear();
        MarketState.stock.clear();
        MarketState.buy.clear();
        MarketState.sell.clear();
        MarketState.trend.clear();

        for (String item : MarketState.base.keySet()) {

            double base = MarketState.base.get(item);

            MarketState.price.put(item, base);

            double rarity = MarketState.rarity.getOrDefault(item, 5.0);
            double random = (Math.random() - 0.5) * rarity;

            double stock = Math.max(20, rarity * 2 + random);
            MarketState.stock.put(item, stock);

            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);
        }

        Bukkit.broadcastMessage("§6🏦 Économie réinitialisée");
        Bukkit.broadcastMessage("§a✔ Marché stabilisé");
    }

    // =========================
    // 📦 GET ITEMS
    // =========================
    public static Set<String> getItems() {
        return MarketState.base.keySet();
    }

    // =========================
    // 🔍 CHECK ITEM
    // =========================
    public static boolean hasItem(String item) {
        return MarketState.base.containsKey(item);
    }

    // =========================
    // 💰 GET PRICE
    // =========================
    public static double getPrice(String item) {
        return MarketState.getPrice(item);
    }

    // =========================
    // 🧪 TEST BUY
    // =========================
    public static void testBuy(String item, int amount) {
        MarketEngine.applyBuy(item, amount);
    }

    // =========================
    // 🧪 TEST SELL
    // =========================
    public static void testSell(String item, int amount) {
        MarketEngine.applySell(item, amount);
    }

    // =========================
    // 🔧 LOAD CONFIG SECTION
    // =========================
    private static void load(Main plugin, String path, java.util.Map<String, Double> map) {

        if (plugin.getConfig().getConfigurationSection(path) == null) return;

        for (String key : plugin.getConfig().getConfigurationSection(path).getKeys(false)) {
            map.put(key, plugin.getConfig().getDouble(path + "." + key));
        }
    }
}