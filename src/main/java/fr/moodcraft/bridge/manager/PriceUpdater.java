package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.shop.Shop;
import fr.moodcraft.bridge.market.MarketEngine;

import java.util.Set;
import java.util.HashSet;

public final class PriceUpdater {

    private PriceUpdater() {}

    // 🔥 LISTE ITEMS AUTORISÉS (fix tes erreurs ALLOWED)
    public static final Set<String> ALLOWED = new HashSet<>();

    static {
        // 👉 adapte selon tes items
        ALLOWED.add("diamond");
        ALLOWED.add("emerald");
        ALLOWED.add("gold");
        ALLOWED.add("iron");
        ALLOWED.add("copper");
        ALLOWED.add("netherite");
        ALLOWED.add("redstone");
        ALLOWED.add("lapis");
        ALLOWED.add("coal");
        ALLOWED.add("quartz");
        ALLOWED.add("amethyst");
        ALLOWED.add("glowstone");
    }

    // =========================
    // 🔥 NOUVELLE VERSION (MarketEngine)
    // =========================
    public static void updateItem(String item, double price) {

        Set<Shop> shops = ShopIndex.get(item);
        if (shops == null || shops.isEmpty()) return;

        for (Shop shop : shops) {
            try {
                if (Math.abs(shop.getPrice() - price) < 0.01) continue;
                shop.setPrice(price);
            } catch (Exception e) {
                System.out.println("[PriceUpdater] ❌ erreur shop: " + item);
            }
        }
    }

    // =========================
    // 🧠 COMPAT ANCIEN CODE
    // =========================
    public static void updateItem(String item) {
        double price = MarketEngine.getPrice(item);
        updateItem(item, price);
    }

    // =========================
    // 🔁 UPDATE 1 SEUL SHOP
    // =========================
    public static void updateSingle(Shop shop, String item) {

        try {
            double price = MarketEngine.getPrice(item);

            if (Math.abs(shop.getPrice() - price) < 0.01) return;

            shop.setPrice(price);

        } catch (Exception e) {
            System.out.println("[PriceUpdater] ❌ erreur single shop: " + item);
        }
    }
}