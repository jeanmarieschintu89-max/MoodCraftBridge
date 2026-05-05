package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.bank.BankStorage;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class WithdrawGUI {

    public static final String ID = "bank_withdraw";

    // ✅ FIX: static
    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§cRetrait");

        Economy eco = VaultHook.getEconomy();
        double cash = eco != null ? eco.getBalance(p) : 0;
        double bank = BankStorage.get(p.getUniqueId().toString());

        // 🔲 background
        for (int i = 0; i < 27; i++) {
            SafeGUI.safeSet(inv, i, SafeGUI.item(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // 💸 -100
        SafeGUI.safeSet(inv, 11, SafeGUI.item(Material.REDSTONE,
                "§c-100€",
                "§8────────────",
                "§7Liquide: §f" + SafeGUI.money(cash),
                "§7Banque: §f" + SafeGUI.money(bank),
                "",
                (bank >= 100 ? "§aDisponible" : "§cSolde insuffisant")));

        // 💸 -1000
        SafeGUI.safeSet(inv, 13, SafeGUI.item(Material.REDSTONE,
                "§c-1000€",
                "§8────────────",
                "§7Liquide: §f" + SafeGUI.money(cash),
                "§7Banque: §f" + SafeGUI.money(bank),
                "",
                (bank >= 1000 ? "§aDisponible" : "§cSolde insuffisant")));

        // 💸 -10000
        SafeGUI.safeSet(inv, 15, SafeGUI.item(Material.REDSTONE,
                "§c-10000€",
                "§8────────────",
                "§7Liquide: §f" + SafeGUI.money(cash),
                "§7Banque: §f" + SafeGUI.money(bank),
                "",
                (bank >= 10000 ? "§aDisponible" : "§cSolde insuffisant")));

        // 🔥 MAX
        SafeGUI.safeSet(inv, 20, SafeGUI.item(Material.GOLD_BLOCK,
                "§6Tout retirer",
                "§8────────────",
                "§7Retire tout ton solde bancaire",
                "",
                (bank > 0 ? "§eClique pour tout retirer" : "§cRien à retirer")));

        // 💬 CUSTOM
        SafeGUI.safeSet(inv, 24, SafeGUI.item(Material.OAK_SIGN,
                "§eMontant personnalisé",
                "§8────────────",
                "§7Clique puis écris un montant",
                "",
                "§aClique"));

        // 🔙 retour
        SafeGUI.safeSet(inv, 22, SafeGUI.item(Material.BARRIER, "§cRetour"));

        GUIManager.open(p, ID, inv);
    }
}