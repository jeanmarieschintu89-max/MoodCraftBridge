package fr.moodcraft.bridge.manager;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class FortuneService {

    private FortuneService() {}

    public static FortuneResult calculate(OfflinePlayer target) {

        if (target == null || target.getUniqueId() == null) {
            return FortuneResult.empty("Inconnu");
        }

        UUID uuid = target.getUniqueId();
        String uuidText = uuid.toString();
        String name = target.getName() != null ? target.getName() : uuidText;

        double pocket = VaultHook.getBalance(target);
        double personalBank = BankStorage.get(uuidText);

        TownReport townReport = getTownReport(name);
        double townBank = townReport.isMayor() ? townReport.balance() : 0.0;

        BusinessReport businessReport = getBusinessReport(uuidText);
        double businessBank = businessReport.balance();

        double total = pocket + personalBank + townBank + businessBank;

        return new FortuneResult(
                uuid,
                name,
                pocket,
                personalBank,
                townBank,
                townReport.townName(),
                townReport.isMayor(),
                businessBank,
                businessReport.businessName(),
                businessReport.hasBusiness(),
                total
        );
    }

    public static List<FortuneResult> top(int limit) {

        List<FortuneResult> results = new ArrayList<>();

        for (UUID uuid : collectCandidateUuids()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            if (player == null) continue;
            if (player.getName() == null) continue;

            FortuneResult result = calculate(player);

            if (result.total() <= 0) continue;

            results.add(result);
        }

        results.sort((a, b) -> Double.compare(b.total(), a.total()));

        if (results.size() <= limit) {
            return results;
        }

        return new ArrayList<>(results.subList(0, limit));
    }

    private static Set<UUID> collectCandidateUuids() {

        Set<UUID> uuids = new HashSet<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player != null && player.getUniqueId() != null) {
                uuids.add(player.getUniqueId());
            }
        }

        uuids.addAll(getBusinessOwnerUuids());

        return uuids;
    }

    private static Set<UUID> getBusinessOwnerUuids() {

        Set<UUID> uuids = new HashSet<>();

        File file = getBusinessFile();

        if (!file.exists()) {
            return uuids;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("businesses");

        if (section == null) {
            return uuids;
        }

        for (String id : section.getKeys(false)) {
            String owner = config.getString("businesses." + id + ".owner.uuid", "");

            try {
                uuids.add(UUID.fromString(owner));
            } catch (Exception ignored) {}
        }

        return uuids;
    }

    private static TownReport getTownReport(String playerName) {

        try {
            Resident resident = TownyAPI.getInstance().getResident(playerName);
            if (resident == null) {
                return TownReport.none();
            }

            Town town = resident.getTownOrNull();
            if (town == null) {
                return TownReport.none();
            }

            boolean mayor = town.getMayor() != null
                    && town.getMayor().getName().equalsIgnoreCase(playerName);

            double balance = mayor ? getTownBalance(town) : 0.0;

            return new TownReport(town.getName(), mayor, balance);

        } catch (Exception ignored) {
            return TownReport.none();
        }
    }

    private static double getTownBalance(Town town) {

        try {
            Object account = town.getAccount();

            for (String methodName : new String[] {
                    "getHoldingBalance",
                    "getBalance",
                    "getCachedBalance"
            }) {
                try {
                    Method method = account.getClass().getMethod(methodName);
                    Object result = method.invoke(account);

                    if (result instanceof Number number) {
                        return number.doubleValue();
                    }
                } catch (Exception ignored) {}
            }

        } catch (Exception ignored) {}

        return 0.0;
    }

    private static BusinessReport getBusinessReport(String uuidText) {

        File file = getBusinessFile();

        if (!file.exists()) {
            return BusinessReport.none();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("businesses");

        if (section == null) {
            return BusinessReport.none();
        }

        double total = 0.0;
        String names = null;
        int count = 0;

        for (String id : section.getKeys(false)) {

            String path = "businesses." + id + ".";
            String owner = config.getString(path + "owner.uuid", "");
            String status = config.getString(path + "status", "ACTIVE");

            if (!owner.equalsIgnoreCase(uuidText)) continue;
            if (status.equalsIgnoreCase("ARCHIVEE")) continue;

            double balance = config.getDouble(path + "balance", 0.0);
            String name = config.getString(path + "name", id);

            total += balance;
            count++;

            if (names == null) {
                names = name;
            } else {
                names += ", " + name;
            }
        }

        if (count <= 0) {
            return BusinessReport.none();
        }

        if (count > 1) {
            names = count + " entreprises";
        }

        return new BusinessReport(true, names, total);
    }

    private static File getBusinessFile() {
        return new File(
                Bukkit.getPluginManager().getPlugin("MoodBusiness") != null
                        ? Bukkit.getPluginManager().getPlugin("MoodBusiness").getDataFolder()
                        : new File("plugins/MoodBusiness"),
                "businesses.yml"
        );
    }

    public static String money(double value) {
        return NumberFormat.getInstance(Locale.FRANCE).format(value) + "€";
    }

    private record TownReport(String townName, boolean isMayor, double balance) {
        private static TownReport none() {
            return new TownReport(null, false, 0.0);
        }
    }

    private record BusinessReport(boolean hasBusiness, String businessName, double balance) {
        private static BusinessReport none() {
            return new BusinessReport(false, null, 0.0);
        }
    }

    public record FortuneResult(
            UUID uuid,
            String name,
            double pocket,
            double personalBank,
            double townBank,
            String townName,
            boolean mayor,
            double businessBank,
            String businessName,
            boolean hasBusiness,
            double total
    ) {
        private static FortuneResult empty(String name) {
            return new FortuneResult(
                    null,
                    name,
                    0.0,
                    0.0,
                    0.0,
                    null,
                    false,
                    0.0,
                    null,
                    false,
                    0.0
            );
        }
    }
}