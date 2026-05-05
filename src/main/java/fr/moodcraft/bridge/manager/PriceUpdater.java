package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.shop.Shop;

import java.util.Set;

public final class PriceUpdater {

    private PriceUpdater() {}

    public static void updateItem(String item, double price) {

        Set<Shop> shops = ShopIndex.get(item);

        if (shops == null || shops.isEmpty()) return;

        for (Shop shop : shops) {

            try {

                // ⚠️ évite boucle infinie si même prix
                if (Math.abs(shop.getPrice() - price) < 0.01) continue;

                shop.setPrice(price);

            } catch (Exception e) {
                System.out.println("[PriceUpdater] ❌ erreur shop: " + item);
                e.printStackTrace();
            }
        }
    }
}