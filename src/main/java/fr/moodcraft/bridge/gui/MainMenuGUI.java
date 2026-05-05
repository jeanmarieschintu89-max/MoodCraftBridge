package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MainMenuGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§6MoodCraft");

        // =========================
        // 💰 ARGENT
        // =========================
        double bank = BankStorage.get(p.getUniqueId().toString());

        double cash = 0;
        try {
            cash = VaultHook.getBalance(p);
        } catch (Exception ignored) {}

        double total = bank + cash;

        // =========================
        // 🔲 BORDURE
        // =========================
        SafeGUI.fillBorders(inv, Material.BLACK_STAINED_GLASS_PANE);

        // =========================
        // 👤 PROFIL
        // =========================
        List<String> lore = new ArrayList<>();

        lore.add("§8────────────");
        lore.add("§7Liquide: §a" + (int) cash + "€");
        lore.add("§7Banque: §6" + (int) bank + "€");
        lore.add("§7Total: §e" + (int) total + "€");
        lore.add("");
        lore.add("§e▶ Voir profil");

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (head.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§e" + p.getName());
            meta.setLore(lore);
            head.setItemMeta(meta);
        }

        inv.setItem(4, head);

        // =========================
        // 💰 BANQUE
        // =========================
        SafeGUI.safeSet(inv, 10, SafeGUI.item(
                Material.GOLD_NUGGET,
                "§6Banque",
                "§7Gérer ton argent",
                "",
                "§7Solde: §6" + (int) bank + "€",
                "",
                "§e▶ Ouvrir"
        ));

        // =========================
        // 💼 BOURSE
        // =========================
        SafeGUI.safeSet(inv, 14, SafeGUI.item(
                Material.CHEST,
                "§6Bourse",
                "§7Marché dynamique",
                "",
                "§7Minerais & ressources",
                "",
                "§e▶ Voir"
        ));

        // =========================
        // 🧭 TELEPORT
        // =========================
        SafeGUI.safeSet(inv, 16, SafeGUI.item(
                Material.COMPASS,
                "§dTéléportation",
                "§7Se déplacer",
                "",
                "§e▶ Ouvrir"
        ));

        // =========================
        // 🗺️ VILLE
        // =========================
        SafeGUI.safeSet(inv, 19, SafeGUI.item(
                Material.MAP,
                "§6Ville",
                "§7Gestion du territoire",
                "",
                "§e▶ Accéder"
        ));

        // =========================
        // ⛏ MÉTIERS
        // =========================
        SafeGUI.safeSet(inv, 21, SafeGUI.item(
                Material.DIAMOND_PICKAXE,
                "§aMétiers",
                "§7Progression & jobs",
                "",
                "§e▶ Ouvrir"
        ));

        // =========================
        // ❌ FERMER
        // =========================
        SafeGUI.safeSet(inv, 26, SafeGUI.item(
                Material.BARRIER,
                "§cFermer"
        ));

        GUIManager.open(p, "main_menu", inv);
    }
}