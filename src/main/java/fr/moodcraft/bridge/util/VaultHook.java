package fr.moodcraft.bridge.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy eco;

    // =========================
    // 🔌 INIT / GET ECONOMY
    // =========================
    public static Economy getEconomy() {

        if (eco != null) return eco;

        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            eco = rsp.getProvider();
        }

        if (eco == null) {
            System.out.println("[VaultHook] ❌ Aucun provider trouvé !");
        }

        return eco;
    }

    // =========================
    // 💰 GET BALANCE
    // =========================
    public static double getBalance(Player p) {
        Economy e = getEconomy();
        if (e == null) return 0;
        return e.getBalance(p);
    }

    // =========================
    // ➕ ADD MONEY
    // =========================
    public static boolean add(Player p, double amount) {

        Economy e = getEconomy();
        if (e == null) return false;

        var res = e.depositPlayer(p, amount);

        return res.transactionSuccess();
    }

    // =========================
    // ➖ REMOVE MONEY
    // =========================
    public static boolean remove(Player p, double amount) {

        Economy e = getEconomy();
        if (e == null) return false;

        var res = e.withdrawPlayer(p, amount);

        return res.transactionSuccess();
    }
}