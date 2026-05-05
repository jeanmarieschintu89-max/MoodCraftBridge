package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.hook.JobsHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileGUI {

    public static void open(org.bukkit.entity.Player viewer, UUID targetUUID) {

        Inventory inv = Bukkit.createInventory(null, 27, "§6Profil");

        String name = Bukkit.getOfflinePlayer(targetUUID).getName();
        if (name == null) name = "Inconnu";

        double bank = BankStorage.get(targetUUID.toString());

        // 🔥 TEMP (reputation non implémentée)
        int rep = 0;
        String rank = "§7Aucun";

        // =========================
        // 🛠️ MÉTIERS
        // =========================
        List<String> jobsLore = new ArrayList<>();

        var targetPlayer = Bukkit.getPlayer(targetUUID);

        if (targetPlayer != null) {
            jobsLore.addAll(JobsHook.getJobsLore(targetPlayer));
        } else {
            jobsLore.add("§7Joueur hors ligne");
        }

        if (jobsLore.isEmpty()) {
            jobsLore.add("§7Aucun métier");
        }

        // =========================
        // 👤 PROFIL
        // =========================
        List<String> lore = new ArrayList<>();

        lore.add("§8────────────");
        lore.add("§7Banque: §6" + (int) bank + "€");
        lore.add("");

        lore.add("§7Réputation: §a" + rep);
        lore.add("§7Statut: " + rank);
        lore.add("");

        lore.add("§7Métiers:");
        lore.addAll(jobsLore);

        // 🔥 tête avec skin
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (head.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(targetUUID));
            meta.setDisplayName("§e" + name);
            meta.setLore(lore);
            head.setItemMeta(meta);
        }

        inv.setItem(13, head);

        // =========================
        // 🔥 BORDURES
        // =========================
        SafeGUI.fillBorders(inv, Material.BLACK_STAINED_GLASS_PANE);

        // =========================
        // 🔙 RETOUR
        // =========================
        SafeGUI.safeSet(inv, 26,
                SafeGUI.item(Material.BARRIER, "§cRetour")
        );

        GUIManager.open(viewer, "profile_gui", inv);
    }
}