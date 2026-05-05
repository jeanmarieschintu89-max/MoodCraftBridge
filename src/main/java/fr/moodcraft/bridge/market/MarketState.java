package fr.moodcraft.bridge.market;

import java.util.HashMap;
import java.util.Map;

public final class MarketState {

    public static final Map<String, Double> price = new HashMap<>();
    public static final Map<String, Double> base = new HashMap<>();
    public static final Map<String, Double> stock = new HashMap<>();
    public static final Map<String, Double> buy = new HashMap<>();
    public static final Map<String, Double> sell = new HashMap<>();
    public static final Map<String, String> trend = new HashMap<>();

    // 🔥 mémoire des anciens prix
    public static final Map<String, Double> lastPrice = new HashMap<>();

    // 🔥 CONFIG
    public static final Map<String, Double> activity = new HashMap<>();
    public static final Map<String, Double> impact = new HashMap<>();
    public static final Map<String, Double> rarity = new HashMap<>();
    public static final Map<String, Double> weight = new HashMap<>();

    private MarketState() {}

    public static double getPrice(String item) {
        return price.getOrDefault(item, base.getOrDefault(item, 0.0));
    }

    public static void setPrice(String item, double newPrice) {

        double oldPrice = price.getOrDefault(item, base.getOrDefault(item, newPrice));

        double change = 0;
        if (oldPrice > 0) {
            change = (newPrice - oldPrice) / oldPrice;
        }

        String arrow;

        if (change > 0.005) {
            arrow = "§a▲ Hausse";
        } else if (change < -0.005) {
            arrow = "§c▼ Baisse";
        } else {
            arrow = "§7▬ Stable";
        }

        // 🔥 update tendance
        trend.put(item, arrow);

        // 🔥 sauvegarde ancien prix
        lastPrice.put(item, oldPrice);

        // 🔥 nouveau prix
        price.put(item, newPrice);
    }
}