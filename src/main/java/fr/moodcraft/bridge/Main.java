package fr.moodcraft.bridge;

import fr.moodcraft.bridge.command.*;
import fr.moodcraft.bridge.gui.*;
import fr.moodcraft.bridge.handler.*;
import fr.moodcraft.bridge.listener.*;
import fr.moodcraft.bridge.manager.*;
import fr.moodcraft.bridge.market.*;
import fr.moodcraft.bridge.bank.*;
import fr.moodcraft.bridge.util.*;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        // =========================
        // 📦 INIT DATA
        // =========================
        BankStorage.init();
        MarketStorage.init();
        TransactionLogger.init();

        // =========================
        // 📊 CONFIG MARCHÉ
        // =========================
        loadBase();
        loadSection("activity", MarketState.activity);
        loadSection("impact", MarketState.impact);
        loadSection("rarity", MarketState.rarity);
        loadSection("weight", MarketState.weight);

        // =========================
        // 🎧 LISTENERS
        // =========================
        registerEvents(
                new ChatInputListener(),
                new MineListener(),
                new PayListener(),
                new InventoryGuardListener(),
                new InventoryCloseListener(),
                new GlobalGUIListener()
        );

        // =========================
        // 🧠 GUI HANDLERS
        // =========================

        // 📊 MARKET
        GUIManager.register("minerais", new PriceHandler());

        // 💰 BANK
        GUIManager.register("bank_deposit", new DepositHandler());
        GUIManager.register("bank_withdraw", new WithdrawHandler());
        GUIManager.register("iban_gui", new IbanHandler());

        // 🧭 AUTRES
        GUIManager.register("teleport", new TeleportHandler());
        GUIManager.register("transfer_confirm", new TransferConfirmHandler());

        // =========================
        // 📜 COMMANDES
        // =========================
        registerCommand("prix", new PrixCommand());
        registerCommand("sync", new SyncCommand());
        registerCommand("trend", new GetTrendCommand());

        registerCommand("ecoreload", new EcoReloadCommand());
        registerCommand("ecoreset", new EcoResetCommand());
        registerCommand("ecotest", new EcoTestCommand());

        registerCommand("menu", new MenuCommand());

        registerCommand("iban", new IbanCommand());
        registerCommand("ibanpay", new IbanPayCommand());

        // =========================
        // 🔁 TASKS
        // =========================
        ShopIndex.rebuild();

        Bukkit.getScheduler().runTaskTimer(this,
                MarketEngine::tick,
                20L,
                20L * 45
        );

        // =========================
        // 🚀 LOG
        // =========================
        getLogger().info("=================================");
        getLogger().info("✅ MoodCraftBridge chargé");
        getLogger().info("🏦 Banque: OK");
        getLogger().info("📊 Marché: OK");
        getLogger().info("💾 Données: OK");
        getLogger().info("🎮 GUI: OK");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        BankStorage.save();
        MarketStorage.save();
        TransactionLogger.save();

        getLogger().info("❌ Plugin désactivé");
    }

    // =========================
    // 🔧 UTILS
    // =========================
    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        if (getCommand(name) != null) {
            getCommand(name).setExecutor(executor);
        } else {
            getLogger().warning("❌ Commande non trouvée: " + name);
        }
    }

    // =========================
    // 📊 CONFIG LOAD
    // =========================
    private void loadBase() {
        if (getConfig().getConfigurationSection("base") == null) return;

        for (String key : getConfig().getConfigurationSection("base").getKeys(false)) {

            double value = getConfig().getDouble("base." + key);

            MarketState.base.put(key, value);

            if (!MarketState.price.containsKey(key)) {
                MarketState.price.put(key, value);
            }

            MarketState.stock.putIfAbsent(key, 0.0);
            MarketState.buy.putIfAbsent(key, 0.0);
            MarketState.sell.putIfAbsent(key, 0.0);
        }
    }

    private void loadSection(String path, Map<String, Double> map) {
        if (getConfig().getConfigurationSection(path) == null) return;

        for (String key : getConfig().getConfigurationSection(path).getKeys(false)) {
            map.put(key, getConfig().getDouble(path + "." + key));
        }
    }
}