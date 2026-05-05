package fr.moodcraft.bridge.market;

import fr.moodcraft.bridge.Main; // ✅ FIX

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MarketStorage {

    private static File file;
    private static FileConfiguration config;

    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "market.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // 🔥 sécurité
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        load();
    }

    public static void load() {

        for (String key : config.getKeys(false)) {

            MarketState.price.put(key, config.getDouble(key + ".price"));
            MarketState.stock.put(key, config.getDouble(key + ".stock"));
            MarketState.buy.put(key, config.getDouble(key + ".buy"));
            MarketState.sell.put(key, config.getDouble(key + ".sell"));
        }
    }

    public static void save() {

        for (String key : MarketState.price.keySet()) {

            config.set(key + ".price", MarketState.price.get(key));
            config.set(key + ".stock", MarketState.stock.get(key));
            config.set(key + ".buy", MarketState.buy.get(key));
            config.set(key + ".sell", MarketState.sell.get(key));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}