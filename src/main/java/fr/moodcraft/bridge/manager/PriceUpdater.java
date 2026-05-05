package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.shop.Shop;
import fr.moodcraft.bridge.market.MarketAPI;

import java.util.Set;

public final class PriceUpdater {

    public static final Set<String> ALLOWED = Set.of(
            "diamond","iron","gold","emerald","copper",
            "redstone","lapis","coal","quartz",
            "netherite","amethyst","glowstone"
    );

    // ⚡ UPDATE GLOBAL
    public static void updateItem(String item) {

        if (!ALLOWED.contains(item)) return;

        double price = MarketAPI.getPrice(item);

        Set<Shop> shops = ShopIndex.get(item);
        if (shops == null || shops.isEmpty()) return;

        for (Shop shop : shops) {

            if (shop == null) continue;

            if (Math.abs(shop.getPrice() - price) > 0.01) {
                shop.setPrice(price);
            }
        }
    }

    // ⚡ UPDATE SINGLE
    public static void updateSingle(Shop shop, String item) {

        if (!ALLOWED.contains(item)) return;
        if (shop == null) return;

        double price = MarketAPI.getPrice(item);

        if (Math.abs(shop.getPrice() - price) < 0.01) return;

        shop.setPrice(price);
    }
}