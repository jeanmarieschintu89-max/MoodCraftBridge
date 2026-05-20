package fr.moodcraft.bridge.command;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class FortuneCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("econ.admin") && !sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 1) {
            header(sender, "Fortune");
            sender.sendMessage("§e➜ §7Utilisation : §e/fortune <joueur>");
            footer(sender);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null || target.getUniqueId() == null) {
            error(sender, "Joueur introuvable.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        String uuidText = uuid.toString();
        String name = target.getName() != null ? target.getName() : args[0];

        double pocket = VaultHook.getBalance(target);
        double personalBank = BankStorage.get(uuidText);

        TownReport townReport = getTownReport(name);
        double townBank = townReport.isMayor() ? townReport.balance() : 0.0;

        BusinessReport businessReport = getBusinessReport(uuidText);
        double businessBank = businessReport.balance();

        double total = pocket + personalBank + townBank + businessBank;

        header(sender, "Fortune de " + name);
        sender.sendMessage(line("Argent de poche", money(pocket)));
        sender.sendMessage(line("Banque personnelle", money(personalBank)));

        if (townReport.isMayor()) {
            sender.sendMessage(line("Banque ville", money(townBank) + " §8(§b" + townReport.townName() + "§8)"));
        } else if (townReport.townName() != null) {
            sender.sendMessage(line("Banque ville", "§8non comptée §7(§f" + townReport.townName() + "§7, pas maire§8)"));
        } else {
            sender.sendMessage(line("Banque ville", "§80€ §7(aucune ville)"));
        }

        if (businessReport.hasBusiness()) {
            sender.sendMessage(line("Banque entreprise", money(businessBank) + " §8(§d" + businessReport.businessName() + "§8)"));
        } else {
            sender.sendMessage(line("Banque entreprise", "§80€ §7(aucune entreprise dirigée)"));
        }

        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("§6✦ §fTotal estimé : §a" + money(total));
        footer(sender);

        return true;
    }

    private TownReport getTownReport(String playerName) {

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

    private double getTownBalance(Town town) {

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

    private BusinessReport getBusinessReport(String uuidText) {

        File file = new File(
                Bukkit.getPluginManager().getPlugin("MoodBusiness") != null
                        ? Bukkit.getPluginManager().getPlugin("MoodBusiness").getDataFolder()
                        : new File("plugins/MoodBusiness"),
                "businesses.yml"
        );

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

    private String line(String label, String value) {
        return "§8• §7" + label + " : §f" + value;
    }

    private String money(double value) {
        return NumberFormat.getInstance(Locale.FRANCE).format(value) + "€";
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Fortune");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §aMood§6Craft §f" + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
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
}