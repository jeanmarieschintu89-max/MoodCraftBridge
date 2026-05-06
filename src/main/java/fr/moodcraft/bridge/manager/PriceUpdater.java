package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.shop.Shop;
import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.market.MarketEngine;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class PriceUpdater {

    private static final Logger log = Main.getInstance().getLogger();

    private PriceUpdater() {}

    // =========================
    // 🔥 ITEMS AUTORISÉS
    // =========================
    public static final Set<String> ALLOWED = new HashSet<>();

    static {

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
    // 📊 UPDATE ITEM
    // =========================
    public static void updateItem(String item, double price) {

        Set<Shop> shops = ShopIndex.get(item);

        if (shops == null || shops.isEmpty()) {
            return;
        }

        int updated = 0;

        for (Shop shop : shops) {

            try {

                if (Math.abs(shop.getPrice() - price) < 0.01) {
                    continue;
                }

                shop.setPrice(price);
                updated++;

            } catch (Exception e) {

                log.warning("[PriceUpdater] Erreur shop: "
                        + item + " | " + e.getMessage());
            }
        }

        if (updated > 0) {
            log.info("[PriceUpdater] "
                    + item + " → "
                    + updated + " shops mis à jour");
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
    // 🔁 UPDATE SHOP UNIQUE
    // =========================
    public static void updateSingle(Shop shop, String item) {

        try {

            double price = MarketEngine.getPrice(item);

            if (Math.abs(shop.getPrice() - price) < 0.01) {
                return;
            }

            shop.setPrice(price);

        } catch (Exception e) {

            log.warning("[PriceUpdater] Erreur single shop: "
                    + item + " | " + e.getMessage());
        }
    }
}