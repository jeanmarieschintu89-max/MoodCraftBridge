package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.TransactionManager;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EcoTrackGUI {

    private static final int PAGE_SIZE = 28;
    private static final Map<UUID, UUID> TARGETS = new HashMap<>();
    private static final Map<UUID, String> FILTERS = new HashMap<>();
    private static final Map<UUID, Integer> PAGES = new HashMap<>();
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private EcoTrackGUI() {}

    public static void open(Player admin, OfflinePlayer target) {
        if (admin == null || target == null) return;
        open(admin, target.getUniqueId(), null, 1);
    }

    public static void open(Player admin, UUID target, String filter, int page) {
        if (admin == null || target == null) return;

        List<String> all = TransactionManager.getFiltered(target, filter, null);
        int maxPage = Math.max(1, (int) Math.ceil(all.size() / (double) PAGE_SIZE));
        page = Math.max(1, Math.min(page, maxPage));

        TARGETS.put(admin.getUniqueId(), target);
        FILTERS.put(admin.getUniqueId(), filter);
        PAGES.put(admin.getUniqueId(), page);

        OfflinePlayer offline = Bukkit.getOfflinePlayer(target);
        String targetName = offline.getName() == null ? target.toString().substring(0, 8) : offline.getName();
        String filterLabel = label(filter);

        Inventory inv = Bukkit.createInventory(null, 54, GuiTitle.of("TrackEco §8• §e" + shortName(targetName)));
        SafeGUI.fill(inv, Material.BLACK_STAINED_GLASS_PANE, " ");

        SafeGUI.safeSet(inv, 4, SafeGUI.glow(SafeGUI.item(
                Material.SPYGLASS,
                "§6✦ §fTrackEco Admin §6✦",
                "§8----- §6✦ §aMood§6Craft §fÉconomie §6✦ §8-----",
                "",
                "§8• §7Joueur : §e" + targetName,
                "§8• §7UUID : §f" + target,
                "§8• §7Filtre : §e" + filterLabel,
                "§8• §7Mouvements : §e" + all.size(),
                "§8• §7Page : §e" + page + "§8/§e" + maxPage,
                "",
                "§e➜ §fBanque, bourse, virements, achats"
        )));

        List<String> pageData = TransactionManager.getPage(all, page, PAGE_SIZE);
        if (pageData.isEmpty()) {
            SafeGUI.safeSet(inv, 22, SafeGUI.item(
                    Material.PAPER,
                    "§6✦ §fAucun mouvement §6✦",
                    "§8• §7Aucune action trouvée",
                    "§8• §7Filtre : §e" + filterLabel
            ));
        } else {
            for (int i = 0; i < pageData.size() && i < SLOTS.length; i++) {
                SafeGUI.safeSet(inv, SLOTS[i], itemFor(pageData.get(i)));
            }
        }

        SafeGUI.safeSet(inv, 45, filterItem(Material.BOOK, "Toutes", null, filter));
        SafeGUI.safeSet(inv, 46, filterItem(Material.EMERALD, "Banque", "BANK", filter));
        SafeGUI.safeSet(inv, 47, filterItem(Material.WRITABLE_BOOK, "Virements", "TRANSFER", filter));
        SafeGUI.safeSet(inv, 48, filterItem(Material.CHEST_MINECART, "Achats", "MARKET_BUY", filter));
        SafeGUI.safeSet(inv, 49, filterItem(Material.GOLD_INGOT, "Ventes", "MARKET_SELL", filter));
        SafeGUI.safeSet(inv, 50, filterItem(Material.DIAMOND, "Essentials", "ESSENTIALS", filter));

        SafeGUI.safeSet(inv, 51, page > 1
                ? SafeGUI.item(Material.SPECTRAL_ARROW, "§6✦ §fPage précédente §6✦", "§8• §7Page §e" + (page - 1), "", "§e➜ §fOuvrir")
                : SafeGUI.item(Material.GRAY_DYE, "§6✦ §fPremière page §6✦", "§8• §7Aucune page avant"));

        SafeGUI.safeSet(inv, 52, page < maxPage
                ? SafeGUI.item(Material.SPECTRAL_ARROW, "§6✦ §fPage suivante §6✦", "§8• §7Page §e" + (page + 1), "", "§e➜ §fOuvrir")
                : SafeGUI.item(Material.GRAY_DYE, "§6✦ §fDernière page §6✦", "§8• §7Aucune page après"));

        SafeGUI.safeSet(inv, 53, SafeGUI.item(Material.BARRIER, "§c✦ §fFermer §c✦", "§8• §7Fermer le suivi"));
        GUIManager.open(admin, "eco_track", inv);
    }

    public static UUID getTarget(Player admin) { return admin == null ? null : TARGETS.get(admin.getUniqueId()); }
    public static String getFilter(Player admin) { return admin == null ? null : FILTERS.get(admin.getUniqueId()); }
    public static int getPage(Player admin) { return admin == null ? 1 : PAGES.getOrDefault(admin.getUniqueId(), 1); }
    public static void clear(Player admin) {
        if (admin == null) return;
        TARGETS.remove(admin.getUniqueId());
        FILTERS.remove(admin.getUniqueId());
        PAGES.remove(admin.getUniqueId());
    }

    private static ItemStack itemFor(String line) {
        Material mat = Material.PAPER;
        String name = "§6✦ §fAction économique §6✦";
        if (line.contains("[DEPOSIT]")) { mat = Material.EMERALD; name = "§6✦ §aDépôt §6✦"; }
        else if (line.contains("[WITHDRAW]")) { mat = Material.REDSTONE; name = "§6✦ §cRetrait §6✦"; }
        else if (line.contains("[TRANSFER]") || line.contains("[PAY_")) { mat = Material.WRITABLE_BOOK; name = "§6✦ §eVirement / Pay §6✦"; }
        else if (line.contains("[MARKET_BUY]")) { mat = Material.CHEST_MINECART; name = "§6✦ §fAchat bourse §6✦"; }
        else if (line.contains("[MARKET_SELL]")) { mat = Material.GOLD_INGOT; name = "§6✦ §bVente bourse §6✦"; }
        else if (line.contains("[ESSENTIALS]")) { mat = Material.DIAMOND; name = "§6✦ §fEssentials/Vault §6✦"; }

        return SafeGUI.item(mat, name,
                "§8• §7" + crop(line, 56),
                "§8• §7Type : §e" + typeOf(line),
                "§8• §7Traçabilité admin",
                "",
                "§e➜ §fAction enregistrée");
    }

    private static ItemStack filterItem(Material mat, String label, String value, String current) {
        boolean active = value == null ? current == null : value.equals(current);
        ItemStack item = SafeGUI.item(mat, "§6✦ §f" + label + " §6✦",
                active ? "§a✔ §fFiltre actif" : "§8• §7Filtrer l'historique",
                active ? "§8• §7Sélection actuelle" : "§e➜ §fOuvrir");
        return active ? SafeGUI.glow(item) : item;
    }

    private static String label(String filter) {
        if (filter == null) return "Toutes actions";
        return switch (filter) {
            case "BANK" -> "Banque";
            case "TRANSFER" -> "Virements";
            case "MARKET_BUY" -> "Achats bourse";
            case "MARKET_SELL" -> "Ventes bourse";
            case "ESSENTIALS" -> "Essentials/Vault";
            default -> filter;
        };
    }

    private static String typeOf(String line) {
        if (line.contains("[DEPOSIT]")) return "Dépôt bancaire";
        if (line.contains("[WITHDRAW]")) return "Retrait bancaire";
        if (line.contains("[TRANSFER]")) return "Virement bancaire";
        if (line.contains("[PAY_SENT]")) return "Essentials /pay envoyé";
        if (line.contains("[PAY_RECEIVED]")) return "Essentials /pay reçu";
        if (line.contains("[MARKET_BUY]")) return "Achat bourse";
        if (line.contains("[MARKET_SELL]")) return "Vente bourse";
        if (line.contains("[ESSENTIALS]")) return "Variation Vault";
        return "Action économique";
    }

    private static String crop(String text, int max) {
        if (text == null || text.isBlank()) return "Aucune donnée";
        String clean = text.replaceAll("§.", "").trim();
        return clean.length() <= max ? clean : clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private static String shortName(String name) {
        if (name == null || name.isBlank()) return "Inconnu";
        return name.length() <= 14 ? name : name.substring(0, 14);
    }
}
