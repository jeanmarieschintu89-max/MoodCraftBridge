package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class IpTrackManager implements Listener {

    private static File file;
    private static FileConfiguration config;

    public static void init(Main plugin) {
        file = new File(plugin.getDataFolder(), "ip-history.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = getCurrentIp(player);
        String platform = detectPlatform(player);

        if (ip == null || ip.isBlank()) return;

        recordIp(player.getUniqueId(), player.getName(), ip, platform);
    }

    public static void recordIp(UUID uuid, String name, String ip, String platform) {
        if (config == null || uuid == null || ip == null || ip.isBlank()) return;

        String path = "players." + uuid + ".";
        long now = System.currentTimeMillis();

        config.set(path + "name", name != null ? name : "Inconnu");
        config.set(path + "last-ip", ip);
        config.set(path + "last-platform", platform != null && !platform.isBlank() ? platform : "Inconnue");
        config.set(path + "last-seen", now);

        if (!config.contains(path + "first-seen")) {
            config.set(path + "first-seen", now);
        }

        List<Map<?, ?>> oldList = config.getMapList(path + "ips");
        List<Map<String, Object>> newList = new ArrayList<>();
        boolean found = false;

        for (Map<?, ?> old : oldList) {
            String storedIp = String.valueOf(old.containsKey("ip") ? old.get("ip") : "");
            long firstSeen = parseLong(old.get("first-seen"), now);
            long lastSeen = parseLong(old.get("last-seen"), now);
            int count = (int) parseLong(old.get("count"), 1);
            String storedPlatform = String.valueOf(old.containsKey("platform") ? old.get("platform") : platform);

            if (storedIp.equals(ip)) {
                found = true;
                lastSeen = now;
                count++;
                storedPlatform = platform != null && !platform.isBlank() ? platform : storedPlatform;
            }

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("ip", storedIp);
            entry.put("platform", storedPlatform != null && !storedPlatform.isBlank() ? storedPlatform : "Inconnue");
            entry.put("first-seen", firstSeen);
            entry.put("last-seen", lastSeen);
            entry.put("count", count);
            newList.add(entry);
        }

        if (!found) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("ip", ip);
            entry.put("platform", platform != null && !platform.isBlank() ? platform : "Inconnue");
            entry.put("first-seen", now);
            entry.put("last-seen", now);
            entry.put("count", 1);
            newList.add(entry);
        }

        config.set(path + "ips", newList);
        save();
    }

    public static IpReport getReport(UUID uuid) {
        if (uuid == null) return IpReport.empty();

        String currentIp = null;
        String currentPlatform = null;
        Player online = Bukkit.getPlayer(uuid);

        if (online != null && online.isOnline()) {
            currentIp = getCurrentIp(online);
            currentPlatform = detectPlatform(online);
        }

        Set<String> ips = new HashSet<>();
        List<IpEntry> history = new ArrayList<>();
        String storedName = null;
        long firstSeen = 0L;
        long lastSeen = 0L;
        String lastIp = null;
        String lastPlatform = null;

        if (config != null) {
            String path = "players." + uuid + ".";
            storedName = config.getString(path + "name");
            firstSeen = config.getLong(path + "first-seen", 0L);
            lastSeen = config.getLong(path + "last-seen", 0L);
            lastIp = config.getString(path + "last-ip");
            lastPlatform = config.getString(path + "last-platform");

            if (lastIp != null && !lastIp.isBlank()) ips.add(lastIp);
            if (currentIp != null && !currentIp.isBlank()) ips.add(currentIp);

            List<Map<?, ?>> list = config.getMapList(path + "ips");
            for (Map<?, ?> raw : list) {
                String ip = String.valueOf(raw.containsKey("ip") ? raw.get("ip") : "");
                if (ip == null || ip.isBlank()) continue;

                String platform = String.valueOf(raw.containsKey("platform") ? raw.get("platform") : "Inconnue");
                long entryFirst = parseLong(raw.get("first-seen"), 0L);
                long entryLast = parseLong(raw.get("last-seen"), 0L);
                int count = (int) parseLong(raw.get("count"), 0L);

                ips.add(ip);
                history.add(new IpEntry(ip, platform, entryFirst, entryLast, count));
            }
        }

        if (currentPlatform != null && !currentPlatform.isBlank()) {
            lastPlatform = currentPlatform;
        }

        history.sort((a, b) -> Long.compare(b.lastSeen(), a.lastSeen()));

        return new IpReport(
                currentIp,
                lastIp,
                currentPlatform,
                lastPlatform,
                storedName,
                firstSeen,
                lastSeen,
                history,
                ips
        );
    }

    public static List<LinkedAccount> findLinkedAccounts(Set<String> ips, UUID ignoredUuid) {
        if (config == null || ips == null || ips.isEmpty()) return Collections.emptyList();

        ConfigurationSection section = config.getConfigurationSection("players");
        if (section == null) return Collections.emptyList();

        List<LinkedAccount> results = new ArrayList<>();

        for (String uuidText : section.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidText);
            } catch (Exception e) {
                continue;
            }

            if (ignoredUuid != null && ignoredUuid.equals(uuid)) continue;

            String path = "players." + uuid + ".";
            String name = config.getString(path + "name", "Inconnu");
            Set<String> shared = new HashSet<>();

            String lastIp = config.getString(path + "last-ip");
            if (lastIp != null && ips.contains(lastIp)) shared.add(lastIp);

            for (Map<?, ?> raw : config.getMapList(path + "ips")) {
                String ip = String.valueOf(raw.containsKey("ip") ? raw.get("ip") : "");
                if (ips.contains(ip)) shared.add(ip);
            }

            if (!shared.isEmpty()) {
                OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                String displayName = offline.getName() != null ? offline.getName() : name;
                results.add(new LinkedAccount(uuid, displayName, shared));
            }
        }

        results.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));
        return results;
    }

    public static String getCurrentIp(Player player) {
        if (player == null) return null;

        InetSocketAddress address = player.getAddress();
        if (address == null || address.getAddress() == null) return null;

        return address.getAddress().getHostAddress();
    }

    public static String detectPlatform(Player player) {
        if (player == null) return "Inconnue";

        String geyser = detectGeyserPlatform(player.getUniqueId());
        if (geyser != null && !geyser.isBlank()) {
            return geyser;
        }

        if (isFloodgatePlayer(player.getUniqueId())) {
            return "Bedrock";
        }

        return "Java PC";
    }

    private static String detectGeyserPlatform(UUID uuid) {
        try {
            Class<?> geyserApiClass = Class.forName("org.geysermc.geyser.api.GeyserApi");
            Method apiMethod = geyserApiClass.getMethod("api");
            Object api = apiMethod.invoke(null);

            Method connectionMethod = api.getClass().getMethod("connectionByUuid", UUID.class);
            Object connection = connectionMethod.invoke(api, uuid);

            if (connection == null) return null;

            try {
                Method platformTypeMethod = connection.getClass().getMethod("platformType");
                Object platform = platformTypeMethod.invoke(connection);
                return readablePlatform(String.valueOf(platform));
            } catch (Exception ignored) {
                return "Bedrock";
            }

        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean isFloodgatePlayer(UUID uuid) {
        try {
            Class<?> floodgateClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Method getInstance = floodgateClass.getMethod("getInstance");
            Object api = getInstance.invoke(null);
            Method isFloodgatePlayer = api.getClass().getMethod("isFloodgatePlayer", UUID.class);
            Object result = isFloodgatePlayer.invoke(api, uuid);
            return result instanceof Boolean bool && bool;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String readablePlatform(String platform) {
        if (platform == null) return "Bedrock";

        String normalized = platform.toUpperCase();

        if (normalized.contains("ANDROID")) return "Téléphone Android";
        if (normalized.contains("IOS")) return "Téléphone iOS";
        if (normalized.contains("XBOX")) return "Xbox";
        if (normalized.contains("PLAYSTATION") || normalized.contains("PS4") || normalized.contains("PS5")) return "PlayStation";
        if (normalized.contains("NINTENDO") || normalized.contains("SWITCH")) return "Nintendo Switch";
        if (normalized.contains("WINDOWS") || normalized.contains("WIN10") || normalized.contains("UWP")) return "Windows Bedrock";
        if (normalized.contains("UNKNOWN")) return "Bedrock inconnu";

        return "Bedrock " + platform;
    }

    private static long parseLong(Object object, long fallback) {
        if (object instanceof Number number) return number.longValue();

        try {
            return Long.parseLong(String.valueOf(object));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static void save() {
        if (config == null || file == null) return;

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record IpReport(
            String currentIp,
            String lastIp,
            String currentPlatform,
            String lastPlatform,
            String storedName,
            long firstSeen,
            long lastSeen,
            List<IpEntry> history,
            Set<String> allIps
    ) {
        public static IpReport empty() {
            return new IpReport(null, null, null, null, null, 0L, 0L, Collections.emptyList(), Collections.emptySet());
        }
    }

    public record IpEntry(String ip, String platform, long firstSeen, long lastSeen, int count) {}

    public record LinkedAccount(UUID uuid, String name, Set<String> sharedIps) {}
}