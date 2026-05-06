package fr.moodcraft.bridge.manager;

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import fr.moodcraft.bridge.util.ItemNormalizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ShopIndex {

    private static final Map<String, Set<Shop>> INDEX = new ConcurrentHashMap<>();

    private ShopIndex() {}

    // 🔁 rebuild au démarrage
    public static void rebuild() {

        INDEX.clear();

        QuickShopAPI api = QuickShopAPI.getInstance();

        if (api == null || api.getShopManager() == null) {
            System.out.println("[ShopIndex] ❌ QuickShop non prêt");
            return;
        }

        Collection<Shop> shops = api.getShopManager().getAllShops();

        for (Shop shop : shops) {
            add(shop);
        }

        Main.getInstance().getLogger().info("[ShopIndex] ✔ 12 items indexés");
    }

    // ➕ ajout dynamique
    public static void add(Shop shop) {

        if (shop == null || shop.getItem() == null) return;

        String key = ItemNormalizer.normalize(shop.getItem().getType());

        if (key == null) return;
        if (!PriceUpdater.ALLOWED.contains(key)) return;

        INDEX.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(shop);
    }

    // ➖ suppression dynamique
    public static void remove(Shop shop) {

        if (shop == null || shop.getItem() == null) return;

        String key = ItemNormalizer.normalize(shop.getItem().getType());

        if (key == null) return;

        Set<Shop> set = INDEX.get(key);
        if (set != null) {
            set.remove(shop);
        }
    }

    // 📦 récupérer les shops d’un item
    public static Set<Shop> get(String item) {
        return INDEX.getOrDefault(item, Collections.emptySet());
    }
}