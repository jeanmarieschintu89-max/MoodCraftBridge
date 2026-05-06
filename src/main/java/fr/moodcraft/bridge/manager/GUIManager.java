package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class GUIManager {

    private static final Map<String, GUIHandler> handlers =
            new HashMap<>();

    private static final Map<UUID, String> open =
            new HashMap<>();

    private static final Set<UUID> opening =
            new HashSet<>();

    private static Plugin plugin;

    private GUIManager() {}

    // =========================
    // 🔧 LOGGER SAFE
    // =========================
    private static Logger log() {
        return Main.getInstance().getLogger();
    }

    // =========================
    // 🔧 INIT
    // =========================
    public static void init(Plugin pl) {
        plugin = pl;
    }

    // =========================
    // 📂 OPEN GUI
    // =========================
    public static void open(Player p, String id, Inventory inv) {

        UUID uuid = p.getUniqueId();

        if (id == null) {
            log().warning("[GUI] ID NULL");
            return;
        }

        if (inv == null) {
            log().warning("[GUI] Inventory NULL: " + id);
            return;
        }

        opening.add(uuid);
        open.put(uuid, id);

        p.openInventory(inv);

        Bukkit.getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                opening.remove(uuid);
            }
        });
    }

    // =========================
    // 🔍 GET
    // =========================
    public static String get(Player p) {
        return open.get(p.getUniqueId());
    }

    public static boolean isOpening(Player p) {
        return opening.contains(p.getUniqueId());
    }

    public static boolean hasOpen(Player p) {
        return open.containsKey(p.getUniqueId());
    }

    // =========================
    // ❌ CLOSE
    // =========================
    public static void close(Player p) {

        UUID uuid = p.getUniqueId();

        if (opening.contains(uuid)) {
            return;
        }

        open.remove(uuid);
    }

    // =========================
    // 🧠 REGISTER
    // =========================
    public static void register(String id, GUIHandler handler) {

        if (id == null) {
            log().warning("[GUI] Tentative register ID NULL");
            return;
        }

        if (handler == null) {
            log().warning("[GUI] Handler NULL: " + id);
            return;
        }

        if (handlers.containsKey(id)) {
            log().warning("[GUI] Déjà enregistré: " + id);
        }

        handlers.put(id, handler);

        log().info("[GUI] Handler enregistré: " + id);
    }

    // =========================
    // 🖱 HANDLE CLICK
    // =========================
    public static void handle(Player p, int slot) {

        String id = get(p);

        if (id == null) {
            return;
        }

        GUIHandler handler = handlers.get(id);

        if (handler == null) {
            log().warning("[GUI] Aucun handler: " + id);
            return;
        }

        try {

            handler.onClick(p, slot);

        } catch (Exception e) {

            log().severe("[GUI] Erreur handler: " + id);

            e.printStackTrace();
        }
    }

    // =========================
    // 🔥 FORCE CLOSE
    // =========================
    public static void forceClose(Player p) {

        UUID uuid = p.getUniqueId();

        open.remove(uuid);
        opening.remove(uuid);
    }
}