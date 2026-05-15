package fr.moodcraft.bridge.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import fr.moodcraft.bridge.handler.GUIHandler;

import java.util.*;

public class GUIManager {

    private static final Map<String, GUIHandler> handlers = new HashMap<>();
    private static final Map<UUID, GUIHandler> directHandlers = new HashMap<>();
    private static final Map<UUID, String> open = new HashMap<>();
    private static final Set<UUID> opening = new HashSet<>();

    private static Plugin plugin;

    public static void init(Plugin pl) {
        plugin = pl;
    }

    // =========================
    // 📂 OPEN GUI
    // =========================
    public static void open(Player p, String id, Inventory inv) {

        UUID uuid = p.getUniqueId();

        if (id == null) {

            Bukkit.getLogger().warning("[GUI] ID NULL");

            return;
        }

        opening.add(uuid);
        open.put(uuid, id);
        directHandlers.remove(uuid);

        p.openInventory(inv);

        Bukkit.getScheduler().runTask(plugin, () ->
                opening.remove(uuid)
        );
    }

    // =========================
    // Compat ancien système : GUIManager.set(player, handler)
    // =========================
    public static void set(Player p, GUIHandler handler) {

        if (p == null || handler == null) {
            return;
        }

        UUID uuid = p.getUniqueId();
        directHandlers.put(uuid, handler);
        open.put(uuid, handler.getClass().getSimpleName());
    }

    // =========================
    public static String get(Player p) {
        return open.get(p.getUniqueId());
    }

    public static boolean isOpening(Player p) {
        return opening.contains(p.getUniqueId());
    }

    public static boolean hasOpen(Player p) {
        return open.containsKey(p.getUniqueId()) || directHandlers.containsKey(p.getUniqueId());
    }

    // =========================
    public static void close(Player p) {

        UUID uuid = p.getUniqueId();

        if (opening.contains(uuid)) return;

        open.remove(uuid);
        directHandlers.remove(uuid);
    }

    // =========================
    public static void register(String id, GUIHandler handler) {

        if (handlers.containsKey(id)) {

            Bukkit.getLogger().warning(
                    "[GUI] Déjà enregistré: " + id
            );
        }

        handlers.put(id, handler);
    }

    // =========================
    public static void handle(Player p, int slot) {

        UUID uuid = p.getUniqueId();

        GUIHandler handler = directHandlers.get(uuid);

        if (handler == null) {

            String id = get(p);

            if (id == null) return;

            handler = handlers.get(id);
        }

        if (handler == null) return;

        try {

            handler.onClick(p, slot);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    public static void forceClose(Player p) {

        open.remove(p.getUniqueId());
        opening.remove(p.getUniqueId());
        directHandlers.remove(p.getUniqueId());
    }
}