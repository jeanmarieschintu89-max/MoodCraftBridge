package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.shop.Shop;
import fr.moodcraft.bridge.market.MarketEngine;

import java.util.HashSet;
import java.util.Set;

public final class PriceUpdater {

    private PriceUpdater() {}

    public static final Set<String> ALLOWED = new HashSet<>();

    static {
        ALLOWED.add("diamond");
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

    public static void updateItem(String item, double price) {
        if (item == null) return;

        Set<Shop> shops = ShopIndex.get(item);
        if (shops == null || shops.isEmpty()) return;

        for (Shop shop : shops) {
            try {
                if (shop == null) continue;
                double current = shop.getPrice();
                if (Math.abs(current - price) < 0.01) continue;
                shop.setPrice(price);
            } catch (Exception ignored) {}
        }
    }

    public static void updateItem(String item) {
        if (item == null) return;
        updateItem(item, MarketEngine.getPrice(item));
    }

    public static void updateSingle(Shop shop, String item) {
        // volontairement vide
    }
}
