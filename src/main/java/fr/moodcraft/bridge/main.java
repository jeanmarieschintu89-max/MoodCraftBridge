package fr.moodcraft.bridge;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        // GUI
        GUIManager.register("main_menu", new MainMenuHandler());

        // Commande
        getCommand("menu").setExecutor(new MenuCommand());

        getLogger().info("✅ MoodCraftBridge chargé");
    }
}