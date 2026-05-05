package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.handler.GUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GUIManager {

    private static final Map<UUID, String> open = new HashMap<>();
    private static final Map<String, GUIHandler> handlers = new HashMap<>();
    private static final Set<UUID> opening = new HashSet<>();

    // 🔥 plugin instance safe
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
            System.out.println("[GUI] ❌ ID NULL");
            return;
        }

        opening.add(uuid);
        open.put(uuid, id);

        p.openInventory(inv);

        Bukkit.getScheduler().runTask(plugin, () ->
                opening.remove(uuid)
        );
    }

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
    public static void close(Player p) {

        UUID uuid = p.getUniqueId();

        if (opening.contains(uuid)) return;

        open.remove(uuid);
    }

    // =========================
    public static void register(String id, GUIHandler handler) {

        if (handlers.containsKey(id)) {
            System.out.println("[GUI] ⚠ Déjà enregistré: " + id);
        }

        handlers.put(id, handler);
    }

    // =========================
    public static void handle(Player p, int slot) {

        String id = get(p);

        if (id == null) return;

        GUIHandler handler = handlers.get(id);

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
    }
}