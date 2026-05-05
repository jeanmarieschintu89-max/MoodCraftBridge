package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage; // ✅ FIX
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.manager.GUIManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BankGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§6Banque MoodCraft");

        double bank = BankStorage.get(p.getUniqueId().toString());
        double cash = 0;

        try {
            cash = VaultHook.getBalance(p);
        } catch (Exception ignored) {}

        // 💰 INFO
        SafeGUI.safeSet(inv, 4, SafeGUI.item(Material.GOLD_INGOT,
                "§e💰 Ton argent",
                "§8────────────",
                "§7Liquide: §a" + (int) cash + "€",
                "§7Banque: §6" + (int) bank + "€"
        ));

        // 📥 DEPOT
        SafeGUI.safeSet(inv, 10, SafeGUI.item(Material.CHEST,
                "§aDéposer",
                "§7Mettre argent en banque",
                "",
                "§e▶ Cliquer"
        ));

        // 📤 RETRAIT
        SafeGUI.safeSet(inv, 12, SafeGUI.item(Material.HOPPER,
                "§cRetirer",
                "§7Retirer argent",
                "",
                "§e▶ Cliquer"
        ));

        // 💸 VIREMENT
        SafeGUI.safeSet(inv, 14, SafeGUI.item(Material.PAPER,
                "§eVirement",
                "§7Envoyer de l'argent",
                "",
                "§e▶ Cliquer"
        ));

        // 🏦 IBAN
        SafeGUI.safeSet(inv, 16, SafeGUI.item(Material.BOOK,
                "§bIBAN",
                "§7Voir ton IBAN",
                "",
                "§e▶ Cliquer"
        ));

        // 🔙 RETOUR
        SafeGUI.safeSet(inv, 22, SafeGUI.item(Material.ARROW,
                "§cRetour"
        ));

        GUIManager.open(p, "bank_main", inv);
    }
}