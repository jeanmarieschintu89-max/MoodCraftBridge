package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.util.ItemNormalizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class ShopIndex {

    private static final Map<String, Set<Shop>> INDEX = new ConcurrentHashMap<>();

    private static final Logger log = Main.getInstance().getLogger();

    private ShopIndex() {}

    // =========================
    // 🔁 REBUILD INDEX
    // =========================
    public static void rebuild() {

        INDEX.clear();

        QuickShopAPI api = QuickShopAPI.getInstance();

        if (api == null || api.getShopManager() == null) {

            log.warning("[ShopIndex] QuickShop non prêt");

            return;
        }

        Collection<Shop> shops = api.getShopManager().getAllShops();

        int indexed = 0;

        for (Shop shop : shops) {

            if (add(shop)) {
                indexed++;
            }
        }

        log.info("[ShopIndex] ✔ "
                + indexed
                + " shops indexés | "
                + INDEX.size()
                + " items");
    }

    // =========================
    // ➕ AJOUT DYNAMIQUE
    // =========================
    public static boolean add(Shop shop) {

        if (shop == null || shop.getItem() == null) {
            return false;
        }

        String key = ItemNormalizer.normalize(shop.getItem().getType());

        if (key == null) {
            return false;
        }

        if (!PriceUpdater.ALLOWED.contains(key)) {
            return false;
        }

        INDEX.computeIfAbsent(
                key,
                k -> ConcurrentHashMap.newKeySet()
        ).add(shop);

        return true;
    }

    // =========================
    // ➖ REMOVE DYNAMIQUE
    // =========================
    public static void remove(Shop shop) {

        if (shop == null || shop.getItem() == null) {
            return;
        }

        String key = ItemNormalizer.normalize(shop.getItem().getType());

        if (key == null) {
            return;
        }

        Set<Shop> set = INDEX.get(key);

        if (set != null) {

            set.remove(shop);

            // 🔥 cleanup mémoire
            if (set.isEmpty()) {
                INDEX.remove(key);
            }
        }
    }

    // =========================
    // 📦 GET SHOPS
    // =========================
    public static Set<Shop> get(String item) {

        if (item == null) {
            return Collections.emptySet();
        }

        return INDEX.getOrDefault(
                item.toLowerCase(),
                Collections.emptySet()
        );
    }

    // =========================
    // 📊 DEBUG
    // =========================
    public static int size() {
        return INDEX.size();
    }

    public static void clear() {
        INDEX.clear();
    }
}