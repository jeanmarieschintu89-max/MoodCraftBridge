package fr.moodcraft.bridge.bank;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IbanManager {

    private static File file;
    private static FileConfiguration config;

    // joueur → IBAN
    private static final Map<UUID, String> playerIban = new HashMap<>();

    // IBAN → joueur
    private static final Map<String, UUID> ibanToPlayer = new HashMap<>();

    // =========================
    // 🔄 INIT
    // =========================
    public static void init() {

        file = new File(Main.getInstance().getDataFolder(), "iban.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {

            String iban = config.getString(key);
            if (iban == null) continue;

            UUID uuid = UUID.fromString(key);
            iban = normalize(iban);

            playerIban.put(uuid, iban);
            ibanToPlayer.put(iban, uuid);
        }

        log("§a✔ IBAN system chargé (" + playerIban.size() + " comptes)");
    }

    // =========================
    // 💾 SAVE
    // =========================
    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 🔍 GET
    // =========================
    public static String get(UUID uuid) {
        return playerIban.get(uuid);
    }

    public static UUID getOwner(String iban) {
        return ibanToPlayer.get(normalize(iban));
    }

    // =========================
    // 🔒 UNIQUE CHECK
    // =========================
    public static boolean isUnique(String iban, UUID requester) {

        iban = normalize(iban);

        if (!ibanToPlayer.containsKey(iban)) return true;

        return ibanToPlayer.get(iban).equals(requester);
    }

    // =========================
    // ➕ SET IBAN
    // =========================
    public static boolean set(UUID uuid, String iban) {

        iban = normalize(iban);

        // 🔒 validation format
        if (!iban.matches("FR[0-9A-Z]{8,32}")) {
            log("§c❌ IBAN invalide: " + iban);
            return false;
        }

        // 🔒 unicité
        if (!isUnique(iban, uuid)) {
            log("§c❌ Doublon IBAN: " + iban);
            return false;
        }

        // 🔥 supprimer ancien IBAN
        String old = playerIban.get(uuid);
        if (old != null) {
            ibanToPlayer.remove(old);
            log("§7Ancien IBAN supprimé: " + old);
        }

        // 🔥 save nouveau
        playerIban.put(uuid, iban);
        ibanToPlayer.put(iban, uuid);

        config.set(uuid.toString(), iban);
        save();

        log("§a✔ IBAN défini: " + iban + " → " + uuid);

        return true;
    }

    // =========================
    // ❌ REMOVE
    // =========================
    public static void remove(UUID uuid) {

        String iban = playerIban.get(uuid);

        if (iban != null) {
            ibanToPlayer.remove(iban);
            playerIban.remove(uuid);

            config.set(uuid.toString(), null);
            save();

            log("§eIBAN supprimé: " + iban);
        }
    }

    // =========================
    // 🧠 NORMALIZE
    // =========================
    private static String normalize(String iban) {
        return iban.replace(" ", "").toUpperCase();
    }

    // =========================
    // 📜 LOG
    // =========================
    private static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§b[IBAN] " + msg);
    }
}