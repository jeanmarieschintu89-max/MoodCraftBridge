package fr.moodcraft.bridge;

import fr.moodcraft.bridge.command.*;
import fr.moodcraft.bridge.handler.*;
import fr.moodcraft.bridge.listener.*;
import fr.moodcraft.bridge.manager.*;
import fr.moodcraft.bridge.util.TransactionLogger;
import fr.moodcraft.bridge.hook.JobsHook;

// 🔥 si ton market est dans bridge.market
import fr.moodcraft.bridge.market.*;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
        // 🔥 CORE INIT
        // =========================
        GUIManager.init(this);

        // =========================
        // 🔌 HOOKS
        // =========================
        JobsHook.init(); // 💼 Jobs plugin

        // =========================
        // 💾 DATA INIT
        // =========================
        MarketStorage.init();
        TransactionLogger.init();

        // =========================
        // 🏪 SHOP INDEX
        // =========================
        ShopIndex.rebuild();

        // =========================
        // 🔁 MARKET TASK
        // =========================
        Bukkit.getScheduler().runTaskTimer(this,
                MarketEngine::tick,
                20L,
                20L * 45
        );

        // =========================
        // 🎧 LISTENERS
        // =========================
        registerEvents(
                new GlobalGUIListener(),
                new InventoryCloseListener(),
                new InventoryGuardListener(),
                new MineListener(),
                new ChatInputListener(),
                new PayListener() // 💰 important
        );

        // =========================
        // 🧠 GUI HANDLERS
        // =========================
        GUIManager.register("minerais", new PriceHandler());
        GUIManager.register("bank_deposit", new DepositHandler());
        GUIManager.register("bank_withdraw", new WithdrawHandler()); // 🔥 AJOUT
        GUIManager.register("iban_gui", new IbanHandler());

        // =========================
        // 📜 COMMANDES
        // =========================
        registerCommand("ecoreload", new EcoReloadCommand());
        registerCommand("ecoreset", new EcoResetCommand());
        registerCommand("ibanpay", new IbanPayCommand());
        registerCommand("iban", new IbanCommand());
        registerCommand("ecotest", new EcoTestCommand());
        registerCommand("prix", new PrixCommand());
        registerCommand("menu", new MenuCommand()); // 🔥 AJOUT
        registerCommand("trend", new GetTrendCommand()); // 🔥 AJOUT
        registerCommand("sync", new SyncCommand());

        // =========================
        // 🚀 LOG
        // =========================
        getLogger().info("=================================");
        getLogger().info("✅ MoodCraftBridge chargé");
        getLogger().info("📊 Market: ACTIF");
        getLogger().info("🏪 Shops indexés: OK");
        getLogger().info("💾 Transactions: OK");
        getLogger().info("🔌 Hooks: Jobs OK");
        getLogger().info("🎮 GUI + Input + Commands OK");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {

        // 💾 SAVE PROPRE
        MarketStorage.save();
        TransactionLogger.save();

        getLogger().info("❌ MoodCraftBridge désactivé");
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
}