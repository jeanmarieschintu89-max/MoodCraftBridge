package fr.moodcraft.bridge.market;

import java.util.HashMap;
import java.util.Map;

public class TrendManager {

    // 🆕 stockage du % réel
    private static final Map<String, Double> percentMap = new HashMap<>();

    // 🆕 mémoire du dernier prix connu
    private static final Map<String, Double> lastPrice = new HashMap<>();

    public static void updateTrend(String item, double newPrice) {

        double old = lastPrice.getOrDefault(item, newPrice);

        // 🛡 sécurité
        if (old <= 0) old = newPrice;

        double diff = newPrice - old;
        double percent = (diff / old) * 100;

        // arrondi propre
        percent = Math.round(percent * 100.0) / 100.0;

        // 🧠 on stocke
        percentMap.put(item, percent);
        lastPrice.put(item, newPrice);

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

    // ✔ version texte
    public static String getTrend(String item) {
        return MarketState.trend.getOrDefault(item, "§7➡ stable");
    }

    // ✔ version numérique
    public static double getTrendPercent(String item) {
        return percentMap.getOrDefault(item, 0.0);
    }

    // 🔥 utile pour debug/reset
    public static void reset() {
        percentMap.clear();
        lastPrice.clear();
    }
}