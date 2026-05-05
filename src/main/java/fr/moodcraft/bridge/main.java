package fr.moodcraft.bridge;

import fr.moodcraft.bridge.command.EcoReloadCommand;
import fr.moodcraft.bridge.command.EcoResetCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

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
        // 🎧 LISTENERS
        // =========================
        registerEvents(
                new GlobalGUIListener(),
                new ChatInputListener()
        );

        // =========================
        // 🧠 GUI HANDLERS
        // =========================
        GUIManager.register("minerais", new PriceHandler());
        GUIManager.register("bank_deposit", new DepositHandler());
        GUIManager.register("iban_gui", new IbanHandler());

        // =========================
        // 📜 COMMANDES
        // =========================
        registerCommand("ecoreload", new EcoReloadCommand());
        registerCommand("ecoreset", new EcoResetCommand());
        registerCommand("ibanpay", new IbanPayCommand());
        registerCommand("iban", new IbanCommand());
        registerCommand("ecotest", new EcoTestCommand());

        // =========================
        // 🚀 LOG
        // =========================
        getLogger().info("=================================");
        getLogger().info("✅ MoodCraftBridge chargé");
        getLogger().info("🎮 GUI + Input + Commands OK");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
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