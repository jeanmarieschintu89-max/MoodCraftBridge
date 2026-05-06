package fr.moodcraft.bridge.util;

import fr.moodcraft.bridge.Main;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;
import java.util.logging.Logger;

public class VaultHook {

    private static Economy eco;

    private static final Logger log =
            Main.getInstance().getLogger();

    // =========================
    // 🔌 GET ECONOMY
    // =========================
    public static Economy getEconomy() {

        if (eco != null) {
            return eco;
        }

        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServicesManager()
                        .getRegistration(Economy.class);

        if (rsp != null) {
            eco = rsp.getProvider();
        }

        if (eco == null) {
            log.severe("[VaultHook] Aucun provider Vault trouvé");
        }

        return eco;
    }

    // =========================
    // 💰 BALANCE PLAYER
    // =========================
    public static double getBalance(Player p) {

        Economy e = getEconomy();

        if (e == null || p == null) {
            return 0;
        }

        return e.getBalance(p);
    }

    // =========================
    // 💰 BALANCE UUID
    // =========================
    public static double getBalance(UUID uuid) {

        Economy e = getEconomy();

        if (e == null || uuid == null) {
            return 0;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (player.getName() == null) {
            return 0;
        }

        return e.getBalance(player.getName());
    }

    // =========================
    // ➕ ADD PLAYER
    // =========================
    public static boolean add(Player p, double amount) {

        Economy e = getEconomy();

        if (e == null || p == null || amount <= 0) {
            return false;
        }

        EconomyResponse res =
                e.depositPlayer(p, amount);

        return res.transactionSuccess();
    }

    // =========================
    // ➕ ADD UUID
    // =========================
    public static boolean add(UUID uuid, double amount) {

        Economy e = getEconomy();

        if (e == null || uuid == null || amount <= 0) {
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (player.getName() == null) {
            return false;
        }

        EconomyResponse res =
                e.depositPlayer(player.getName(), amount);

        return res.transactionSuccess();
    }

    // =========================
    // ➖ REMOVE PLAYER
    // =========================
    public static boolean remove(Player p, double amount) {

        Economy e = getEconomy();

        if (e == null || p == null || amount <= 0) {
            return false;
        }

        EconomyResponse res =
                e.withdrawPlayer(p, amount);

        return res.transactionSuccess();
    }

    // =========================
    // ➖ REMOVE UUID
    // =========================
    public static boolean remove(UUID uuid, double amount) {

        Economy e = getEconomy();

        if (e == null || uuid == null || amount <= 0) {
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (player.getName() == null) {
            return false;
        }

        EconomyResponse res =
                e.withdrawPlayer(player.getName(), amount);

        return res.transactionSuccess();
    }

    // =========================
    // 🧠 HAS MONEY
    // =========================
    public static boolean has(Player p, double amount) {

        Economy e = getEconomy();

        if (e == null || p == null) {
            return false;
        }

        return e.has(p, amount);
    }

    // =========================
    // 🔄 RESET CACHE
    // =========================
    public static void reset() {
        eco = null;
    }
}