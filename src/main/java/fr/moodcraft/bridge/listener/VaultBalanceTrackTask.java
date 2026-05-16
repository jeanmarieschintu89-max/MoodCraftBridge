package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.TransactionManager;
import fr.moodcraft.bridge.util.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VaultBalanceTrackTask implements Listener {

    private static final double MIN_DELTA = 0.01;
    private static final Map<UUID, Double> LAST_BALANCE = new HashMap<>();
    private static final Map<UUID, Long> SUPPRESSED_UNTIL = new HashMap<>();

    public VaultBalanceTrackTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(Main.getInstance(), 80L, 80L);
    }

    public static void suppress(Player player) {
        if (player == null) return;
        suppress(player.getUniqueId());
        LAST_BALANCE.put(player.getUniqueId(), VaultHook.getBalance(player));
    }

    public static void suppress(UUID uuid) {
        if (uuid == null) return;
        SUPPRESSED_UNTIL.put(uuid, System.currentTimeMillis() + 4500L);
    }

    private void tick() {
        if (VaultHook.getEconomy() == null) return;

        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            double current = VaultHook.getBalance(player);
            Double last = LAST_BALANCE.put(uuid, current);

            if (last == null) continue;

            double delta = current - last;
            if (Math.abs(delta) < MIN_DELTA) continue;

            Long suppressed = SUPPRESSED_UNTIL.get(uuid);
            if (suppressed != null && suppressed > now) continue;

            TransactionManager.essentialsChange(uuid, delta, "Variation Vault externe");
        }
    }
}
