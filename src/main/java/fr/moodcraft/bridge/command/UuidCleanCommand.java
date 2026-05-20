package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.manager.FortuneService;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UuidCleanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("econ.admin") && !sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 2) {
            help(sender);
            return true;
        }

        String uuidText = args[0];
        String confirm = args[1];

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidText);
        } catch (Exception e) {
            error(sender, "UUID invalide.");
            return true;
        }

        if (!confirm.equalsIgnoreCase("CONFIRM")) {
            error(sender, "Confirmation obligatoire : §e/uuidclean " + uuidText + " CONFIRM");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        double beforePocket = FortuneService.calculate(player).pocket();
        double beforeBank = BankStorage.get(uuidText);
        double beforeBusiness = getBusinessBalance(uuidText);

        boolean essentials = resetEssentials(uuidText);
        boolean bank = resetMoodBank(uuidText);
        int business = resetMoodBusiness(uuidText);

        FortuneService.invalidateTopCache();

        header(sender, "UUID Clean");
        sender.sendMessage("§a✔ §fNettoyage terminé pour : §e" + uuidText);
        sender.sendMessage("§8• §7Essentials/Vault avant : §e" + FortuneService.money(beforePocket) + " §8→ §f" + (essentials ? "reset" : "non trouvé"));
        sender.sendMessage("§8• §7Banque MoodCraft avant : §e" + FortuneService.money(beforeBank) + " §8→ §f" + (bank ? "reset" : "non trouvé"));
        sender.sendMessage("§8• §7Entreprises avant : §e" + FortuneService.money(beforeBusiness) + " §8→ §f" + business + " entreprise(s) reset");
        sender.sendMessage("§8• §7Lance ensuite : §e/fortunepanel refresh");
        footer(sender);

        return true;
    }

    private boolean resetEssentials(String uuidText) {
        File file = new File("plugins/Essentials/userdata", uuidText + ".yml");

        if (!file.exists()) return false;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("money", 0);

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean resetMoodBank(String uuidText) {
        if (!BankStorage.getAccountUuids().contains(uuidText)) return false;

        BankStorage.resetAccount(uuidText);
        return true;
    }

    private int resetMoodBusiness(String uuidText) {
        File file = new File(
                Bukkit.getPluginManager().getPlugin("MoodBusiness") != null
                        ? Bukkit.getPluginManager().getPlugin("MoodBusiness").getDataFolder()
                        : new File("plugins/MoodBusiness"),
                "businesses.yml"
        );

        if (!file.exists()) return 0;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("businesses");
        if (section == null) return 0;

        int reset = 0;

        for (String id : section.getKeys(false)) {
            String path = "businesses." + id + ".";
            String owner = config.getString(path + "owner.uuid", "");

            if (!owner.equalsIgnoreCase(uuidText)) continue;

            config.set(path + "balance", 0.0);
            reset++;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reset;
    }

    private double getBusinessBalance(String uuidText) {
        File file = new File(
                Bukkit.getPluginManager().getPlugin("MoodBusiness") != null
                        ? Bukkit.getPluginManager().getPlugin("MoodBusiness").getDataFolder()
                        : new File("plugins/MoodBusiness"),
                "businesses.yml"
        );

        if (!file.exists()) return 0.0;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("businesses");
        if (section == null) return 0.0;

        double total = 0.0;

        for (String id : section.getKeys(false)) {
            String path = "businesses." + id + ".";
            String owner = config.getString(path + "owner.uuid", "");

            if (!owner.equalsIgnoreCase(uuidText)) continue;

            total += config.getDouble(path + "balance", 0.0);
        }

        return total;
    }

    private void help(CommandSender sender) {
        header(sender, "UUID Clean");
        sender.sendMessage("§e➜ §7/uuidclean <uuid> CONFIRM");
        sender.sendMessage("§8• §7Remet à zéro Essentials, banque MoodCraft et banque entreprise pour cet UUID.");
        sender.sendMessage("§c⚠ §7À utiliser seulement sur l'ancien mauvais UUID.");
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "UUID Clean");
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
}
