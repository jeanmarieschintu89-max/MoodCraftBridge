package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.manager.FortuneService;
import fr.moodcraft.bridge.manager.FortuneService.FortuneResult;
import fr.moodcraft.bridge.manager.IpTrackManager;
import fr.moodcraft.bridge.manager.IpTrackManager.IpEntry;
import fr.moodcraft.bridge.manager.IpTrackManager.IpReport;
import fr.moodcraft.bridge.manager.IpTrackManager.LinkedAccount;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class UuidTrackCommand implements CommandExecutor {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("econ.admin") && !sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration économique.");
            return true;
        }

        if (args.length < 1) {
            header(sender, "Panel Track Admin");
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

        header(sender, "Panel Track Admin " + name);

        if (reports.isEmpty()) {
            sender.sendMessage("§c✖ §fAucun UUID trouvé pour §e" + name + "§f.");
            footer(sender);
            return true;
        }

        OfflinePlayer current = Bukkit.getOfflinePlayer(name);
        UUID currentUuid = current != null ? current.getUniqueId() : null;

        sender.sendMessage("§8• §7UUID actuel Bukkit : §a" + (currentUuid != null ? currentUuid : "inconnu"));
        sender.sendMessage("§8• §7Résultats trouvés : §e" + reports.size());
        sender.sendMessage("§8-----------------------------");

        int index = 1;
        for (UuidReport report : reports.values()) {
            boolean currentOne = currentUuid != null && currentUuid.equals(report.uuid());
            OfflinePlayer player = Bukkit.getOfflinePlayer(report.uuid());
            FortuneResult fortune = FortuneService.calculate(player);
            IpReport ipReport = IpTrackManager.getReport(report.uuid());

            sender.sendMessage("§6#" + index + " §f" + report.uuid() + (currentOne ? " §a(UUID actuel)" : " §c(possible ancien UUID)"));
            sender.sendMessage("§8• §7Nom(s) trouvé(s) : §e" + report.names());
            sender.sendMessage("§8• §7Sources : §f" + report.sources());
            sender.sendMessage("§8• §7Statut : " + (player.isOnline() ? "§aEn ligne" : "§cHors ligne"));
            sender.sendMessage("§8• §7Première connexion : §e" + formatDate(player.getFirstPlayed()));
            sender.sendMessage("§8• §7Dernière connexion : §e" + formatDate(player.getLastPlayed()));

            String essentialsIp = getEssentialsIp(report.uuid());
            sender.sendMessage("§8• §7IP actuelle : §e" + valueOrNone(ipReport.currentIp()));
            sender.sendMessage("§8• §7Dernière IP bridge : §e" + valueOrNone(ipReport.lastIp()));
            sender.sendMessage("§8• §7IP Essentials : §e" + valueOrNone(essentialsIp));

            if (!ipReport.history().isEmpty()) {
                sender.sendMessage("§8• §7Historique IP bridge :");

                int shown = 0;
                for (IpEntry entry : ipReport.history()) {
                    if (shown >= 5) {
                        sender.sendMessage("§8  • §7... et §e" + (ipReport.history().size() - shown) + " §7autre(s) IP");
                        break;
                    }

                    sender.sendMessage("§8  • §e" + entry.ip()
                            + " §7- vues §e" + entry.count()
                            + "x §8(dernier: §7" + formatDate(entry.lastSeen()) + "§8)");
                    shown++;
                }
            } else {
                sender.sendMessage("§8• §7Historique IP bridge : §8aucun historique");
            }

            List<LinkedAccount> linked = IpTrackManager.findLinkedAccounts(ipReport.allIps(), report.uuid());
            if (!linked.isEmpty()) {
                sender.sendMessage("§8• §7Comptes liés par IP connue :");

                int shown = 0;
                for (LinkedAccount account : linked) {
                    if (shown >= 6) {
                        sender.sendMessage("§8  • §7... et §e" + (linked.size() - shown) + " §7autre(s) compte(s)");
                        break;
                    }

                    sender.sendMessage("§8  • §b" + account.name() + " §8(" + account.uuid() + ") §7IP partagée: §e" + String.join(", ", account.sharedIps()));
                    shown++;
                }
            } else {
                sender.sendMessage("§8• §7Comptes liés par IP connue : §8aucun depuis l'historique bridge");
            }

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

        sender.sendMessage("§7Les IP sont des données staff. Le bridge stocke l'historique à partir de maintenant.");
        sender.sendMessage("§7Pour nettoyer un mauvais UUID : §e/uuidclean <uuid> CONFIRM");
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

    private String getEssentialsIp(UUID uuid) {
        if (uuid == null) return null;

        File file = new File("plugins/Essentials/userdata", uuid + ".yml");
        if (!file.exists()) return null;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getString("ip-address");
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

    private String valueOrNone(String value) {
        return value == null || value.isBlank() ? "§8aucune" : value;
    }

    private String formatDate(long time) {
        if (time <= 0) return "inconnue";
        return DATE_FORMAT.format(new Date(time));
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Track Admin");
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