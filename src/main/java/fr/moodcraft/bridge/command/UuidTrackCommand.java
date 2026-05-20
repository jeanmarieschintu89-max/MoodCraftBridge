package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.manager.FortuneService;
import fr.moodcraft.bridge.manager.FortuneService.FortuneResult;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class UuidTrackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("econ.admin") && !sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 1) {
            header(sender, "UUID Track");
            sender.sendMessage("§e➜ §7Utilisation : §e/uuidtrack <pseudo>");
            sender.sendMessage("§8• §7Exemple : §e/uuidtrack Steven2621");
            footer(sender);
            return true;
        }

        String name = args[0];
        Map<UUID, UuidReport> reports = new LinkedHashMap<>();

        scanBukkit(name, reports);
        scanEssentials(name, reports);
        scanMoodBank(name, reports);
        scanMoodBusiness(name, reports);

        header(sender, "UUID Track " + name);

        if (reports.isEmpty()) {
            sender.sendMessage("§c✖ §fAucun UUID trouvé pour §e" + name + "§f.");
            footer(sender);
            return true;
        }

        OfflinePlayer current = Bukkit.getOfflinePlayer(name);
        UUID currentUuid = current != null ? current.getUniqueId() : null;

        sender.sendMessage("§8• §7UUID actuel Bukkit : §a" + (currentUuid != null ? currentUuid : "inconnu"));
        sender.sendMessage("§8-----------------------------");

        int index = 1;
        for (UuidReport report : reports.values()) {
            boolean currentOne = currentUuid != null && currentUuid.equals(report.uuid());
            OfflinePlayer player = Bukkit.getOfflinePlayer(report.uuid());
            FortuneResult fortune = FortuneService.calculate(player);

            sender.sendMessage("§6#" + index + " §f" + report.uuid() + (currentOne ? " §a(UUID actuel)" : " §c(possible ancien UUID)"));
            sender.sendMessage("§8• §7Nom(s) trouvé(s) : §e" + report.names());
            sender.sendMessage("§8• §7Sources : §f" + report.sources());
            sender.sendMessage("§8• §7Argent de poche : §e" + FortuneService.money(fortune.pocket()));
            sender.sendMessage("§8• §7Banque personnelle : §e" + FortuneService.money(fortune.personalBank()));

            if (fortune.mayor()) {
                sender.sendMessage("§8• §7Banque ville : §e" + FortuneService.money(fortune.townBank()) + " §8(§b" + fortune.townName() + "§8)");
            } else if (fortune.townName() != null) {
                sender.sendMessage("§8• §7Banque ville : §8non comptée §7(§f" + fortune.townName() + "§7, pas maire§8)");
            } else {
                sender.sendMessage("§8• §7Banque ville : §80€ §7(aucune ville)");
            }

            if (fortune.hasBusiness()) {
                sender.sendMessage("§8• §7Banque entreprise : §e" + FortuneService.money(fortune.businessBank()) + " §8(§d" + fortune.businessName() + "§8)");
            } else {
                sender.sendMessage("§8• §7Banque entreprise : §80€ §7(aucune entreprise dirigée)");
            }

            sender.sendMessage("§6✦ §fTotal /fortune : §a" + FortuneService.money(fortune.total()));
            sender.sendMessage("§8-----------------------------");
            index++;
        }

        sender.sendMessage("§7Le mauvais UUID est celui marqué §cpossible ancien UUID§7 avec encore de l'argent.");
        footer(sender);

        return true;
    }

    private void scanBukkit(String name, Map<UUID, UuidReport> reports) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (player != null && player.getUniqueId() != null) {
                add(reports, player.getUniqueId(), "Bukkit actuel", player.getName() != null ? player.getName() : name);
            }
        } catch (Exception ignored) {}
    }

    private void scanEssentials(String name, Map<UUID, UuidReport> reports) {
        File folder = new File("plugins/Essentials/userdata");
        File[] files = folder.listFiles((dir, fileName) -> fileName.toLowerCase(Locale.ROOT).endsWith(".yml"));

        if (files == null) return;

        for (File file : files) {
            String uuidText = file.getName().replace(".yml", "");
            UUID uuid = parseUuid(uuidText);
            if (uuid == null) continue;

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String foundName = firstNonEmpty(
                    config.getString("last-account-name"),
                    config.getString("lastAccountName"),
                    config.getString("last-account"),
                    config.getString("lastAccount"),
                    config.getString("nickname"),
                    config.getString("name")
            );

            if (equalsName(foundName, name) || containsName(config.saveToString(), name)) {
                add(reports, uuid, "Essentials userdata", foundName != null ? foundName : name);
            }
        }
    }

    private void scanMoodBank(String name, Map<UUID, UuidReport> reports) {
        for (String uuidText : BankStorage.getAccountUuids()) {
            UUID uuid = parseUuid(uuidText);
            if (uuid == null) continue;

            String storedName = BankStorage.getName(uuidText);
            if (equalsName(storedName, name)) {
                add(reports, uuid, "MoodCraftBridge bank.yml", storedName);
            }
        }
    }

    private void scanMoodBusiness(String name, Map<UUID, UuidReport> reports) {
        File file = new File(
                Bukkit.getPluginManager().getPlugin("MoodBusiness") != null
                        ? Bukkit.getPluginManager().getPlugin("MoodBusiness").getDataFolder()
                        : new File("plugins/MoodBusiness"),
                "businesses.yml"
        );

        if (!file.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("businesses");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            String path = "businesses." + id + ".";
            String ownerUuid = config.getString(path + "owner.uuid", "");
            String ownerName = config.getString(path + "owner.name", "");

            UUID uuid = parseUuid(ownerUuid);
            if (uuid != null && equalsName(ownerName, name)) {
                add(reports, uuid, "MoodBusiness owner:" + id, ownerName);
            }

            ConfigurationSection members = config.getConfigurationSection(path + "members");
            if (members == null) continue;

            for (String memberUuid : members.getKeys(false)) {
                UUID member = parseUuid(memberUuid);
                if (member == null) continue;

                String memberName = config.getString(path + "members." + memberUuid + ".name", "");
                if (equalsName(memberName, name)) {
                    add(reports, member, "MoodBusiness member:" + id, memberName);
                }
            }
        }
    }

    private void add(Map<UUID, UuidReport> reports, UUID uuid, String source, String name) {
        UuidReport existing = reports.get(uuid);

        if (existing == null) {
            reports.put(uuid, new UuidReport(uuid, cleanName(name), source));
            return;
        }

        existing.addSource(source);
        existing.addName(name);
    }

    private UUID parseUuid(String text) {
        try {
            return UUID.fromString(text);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean equalsName(String a, String b) {
        if (a == null || b == null) return false;
        return cleanName(a).equalsIgnoreCase(cleanName(b));
    }

    private boolean containsName(String text, String name) {
        if (text == null || name == null) return false;
        return text.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT));
    }

    private String cleanName(String name) {
        if (name == null || name.isBlank()) return "Inconnu";
        return name.replace("§", "").trim();
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }

    private void error(CommandSender sender, String message) {
        header(sender, "UUID Track");
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

    private static final class UuidReport {
        private final UUID uuid;
        private String names;
        private String sources;

        private UuidReport(UUID uuid, String names, String sources) {
            this.uuid = uuid;
            this.names = names;
            this.sources = sources;
        }

        private UUID uuid() { return uuid; }
        private String names() { return names; }
        private String sources() { return sources; }

        private void addSource(String source) {
            if (source == null || source.isBlank()) return;
            if (sources.contains(source)) return;
            sources += ", " + source;
        }

        private void addName(String name) {
            if (name == null || name.isBlank()) return;
            if (names.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))) return;
            names += ", " + name;
        }
    }
}