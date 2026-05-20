package fr.moodcraft.bridge;

import fr.moodcraft.bridge.command.FreezeCommand;
import fr.moodcraft.bridge.command.SubventionCommand;

import fr.moodcraft.bridge.listener.FreezeListener;
import fr.moodcraft.bridge.listener.LootGenerateProtectionListener;

import fr.moodcraft.bridge.bank.*;

import fr.moodcraft.bridge.command.*;

import fr.moodcraft.bridge.gui.*;

import fr.moodcraft.bridge.handler.*;

import fr.moodcraft.bridge.listener.*;

import fr.moodcraft.bridge.manager.*;

import fr.moodcraft.bridge.market.*;

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

        BankStorage.init();
        MarketStorage.init();
        ReputationManager.init();
        IbanManager.init();
        TransactionManager.init();
        ReputationHistoryManager.init();
        GUIManager.init(this);
        FortunePanelManager.init(this);

        loadBase();

        loadSection("activity", MarketState.activity);
        loadSection("impact", MarketState.impact);
        loadSection("rarity", MarketState.rarity);
        loadSection("weight", MarketState.weight);

        registerEvents(
                new ChatInputListener(),
                new BankChatInputListener(),
                new TransferConfirmChatListener(),
                new MineListener(),
                new ShopListener(),
                new PayListener(),
                new VaultBalanceTrackTask(),
                new InventoryGuardListener(),
                new InventoryCloseListener(),
                new GlobalGUIListener(),
                new FreezeListener(),
                new LootGenerateProtectionListener()
        );

        GUIManager.register("main_menu", new MainMenuHandler());
        GUIManager.register("bank_main", new BankHandler());
        GUIManager.register("iban_gui", new IbanHandler());
        GUIManager.register("transfer_type", new TransferTypeHandler());
        GUIManager.register("transfer_target", new TargetPlayerHandler());
        GUIManager.register("transfer_confirm", new TransferConfirmHandler());
        GUIManager.register("minerais", new PriceHandler());
        GUIManager.register("teleport", new TeleportHandler());
        GUIManager.register("profile_gui", new ProfileHandler());
        GUIManager.register("transaction_history", new TransactionHistoryHandler());
        GUIManager.register("eco_track", new EcoTrackHandler());

        registerCommand("menu", new MenuCommand());
        registerCommand("moodplmaj", new MoodPluginUpdateCommand());
        registerCommand("freeze", new FreezeCommand());
        FurnaceCommand furnaceCommand = new FurnaceCommand();
        registerCommand("furnace", furnaceCommand);
        registerCommand("cuire", furnaceCommand);
        registerCommand("fondre", furnaceCommand);

        registerCommand("banque", new BanqueCommand());
        registerCommand("depot", new BanqueCommand());
        registerCommand("retrait", new BanqueCommand());
        registerCommand("virement", new BanqueCommand());
        registerCommand("rib", new BanqueCommand());
        registerCommand("bankadmin", new BankAdminCommand());
        registerCommand("trackeco", new TrackEcoCommand());
        registerCommand("uuidtrack", new UuidTrackCommand());
        registerCommand("uuidclean", new UuidCleanCommand());
        registerCommand("fortune", new FortuneCommand());
        registerCommand("fortunepanel", new FortunePanelCommand());

        registerCommand("prix", new PrixCommand());
        registerCommand("sync", new SyncCommand());
        registerCommand("trend", new GetTrendCommand());
        registerCommand("ecoreload", new EcoReloadCommand());
        registerCommand("ecoreset", new EcoResetCommand());
        registerCommand("ecotest", new EcoTestCommand());
        registerCommand("marketadmin", new MarketAdminCommand());

        registerCommand("subvention", new SubventionCommand());

        registerCommand("reputation", new ReputationCommand());
        registerCommand("rep", new ReputationCommand());
        registerCommand("toprep", new ReputationCommand());

        Bukkit.getScheduler().runTaskLater(this, ShopIndex::rebuild, 40L);

        Bukkit.getScheduler().runTaskTimer(this, MarketEngine::tick, 20L, 20L * 45);
        MarketEventManager.start();

        getLogger().info("=================================");
        getLogger().info("✅ MoodCraftBridge chargé");
        getLogger().info("🏦 Banque: OK");
        getLogger().info("💬 Banque Chat Input: OK");
        getLogger().info("✅ Confirmation virement chat: OK");
        getLogger().info("📊 Marché: OK");
        getLogger().info("🎲 Événements économiques: OK");
        getLogger().info("🕵 TrackEco: OK");
        getLogger().info("🧩 UUID Track: OK");
        getLogger().info("🧹 UUID Clean: OK");
        getLogger().info("💰 Fortune: OK");
        getLogger().info("🏆 Panneaux fortune: OK");
        getLogger().info("🎮 GUI: OK");
        getLogger().info("🧠 Réputation: OK");
        getLogger().info("🏰 Loot Balance: OK");
        getLogger().info("📜 Contrats: MoodBusiness");
        getLogger().info("🏛️ Subventions: OK");
        getLogger().info("🔥 Furnace VIP: OK");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        MarketEventManager.stop();
        FortunePanelManager.stop();
        BankStorage.save();
        MarketStorage.save();
        TransactionManager.save();
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        if (getCommand(name) != null) getCommand(name).setExecutor(executor);
    }

    private void loadBase() {
        if (getConfig().getConfigurationSection("base") == null) return;
        for (String key : getConfig().getConfigurationSection("base").getKeys(false)) {
            double value = getConfig().getDouble("base." + key);
            MarketState.base.put(key, value);
            if (!MarketState.price.containsKey(key)) MarketState.price.put(key, value);
            MarketState.stock.putIfAbsent(key, 0.0);
            MarketState.buy.putIfAbsent(key, 0.0);
            MarketState.sell.putIfAbsent(key, 0.0);
            MarketState.mined.putIfAbsent(key, 0.0);
        }
    }

    private void loadSection(String path, Map<String, Double> map) {
        if (getConfig().getConfigurationSection(path) == null) return;
        for (String key : getConfig().getConfigurationSection(path).getKeys(false)) map.put(key, getConfig().getDouble(path + "." + key));
    }
}