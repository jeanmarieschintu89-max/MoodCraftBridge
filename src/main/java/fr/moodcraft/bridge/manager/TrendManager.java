package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.market.MarketState;

import java.util.HashMap;
import java.util.Map;

public class TrendManager {

    // stockage du % réel (pour GUI, stats, etc.)
    private static final Map<String, Double> percentMap = new HashMap<>();

    public static void updateTrend(String item, double newPrice) {

        double old = MarketState.getPrice(item);

        // sécurité
        if (old <= 0) old = newPrice;

        double diff = newPrice - old;
        double percent = (diff / old) * 100;

        percent = Math.round(percent * 100.0) / 100.0;

        // on stocke le % brut
        percentMap.put(item, percent);

        String result;

        if (percent > 0) {
            result = percent > 5
                    ? "§2⬆ +" + percent + "%"
                    : "§a⬆ +" + percent + "%";
        } else if (percent < 0) {
            result = percent < -5
                    ? "§4⬇ " + percent + "%"
                    : "§c⬇ " + percent + "%";
        } else {
            result = "§7➡ stable";
        }

        MarketState.trend.put(item, result);
    }

    // version texte
    public static String getTrend(String item) {
        return MarketState.trend.getOrDefault(item, "§7➡ stable");
    }

    // version numérique
    public static double getTrendPercent(String item) {
        return percentMap.getOrDefault(item, 0.0);
    }
}