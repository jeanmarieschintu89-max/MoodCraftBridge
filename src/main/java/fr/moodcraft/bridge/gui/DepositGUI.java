package fr.moodcraft.bridge.gui;

fr.moodcraft.bridge.bank.BankStorage
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.milkbowl.vault.economy.Economy;

public class DepositGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§aDépôt");

        // 💰 Argent liquide (Vault)
        Economy eco = VaultHook.getEconomy();
        double cash = eco != null ? eco.getBalance(p) : 0;

        // 🏦 Banque (API propre)
        double bank = BankAPI.get(p.getUniqueId().toString());

        // 🔲 Background
        for (int i = 0; i < 27; i++) {
            SafeGUI.safeSet(inv, i, SafeGUI.item(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // 💰 +100
        SafeGUI.safeSet(inv, 11, SafeGUI.item(Material.LIME_DYE,
                "§a+100€",
                "§8────────────",
                "§7Liquide: §f" + SafeGUI.money(cash),
                "§7Banque: §f" + SafeGUI.money(bank),
                "",
                (cash >= 100 ? "§aDisponible" : "§cFonds insuffisants")));

        // 💰 +1000
        SafeGUI.safeSet(inv, 13, SafeGUI.item(Material.LIME_DYE,
                "§a+1000€",
                "§8────────────",
                "§7Liquide: §f" + SafeGUI.money(cash),
                "§7Banque: §f" + SafeGUI.money(bank),
                "",
                (cash >= 1000 ? "§aDisponible" : "§cFonds insuffisants")));

        // 💰 +10000
        SafeGUI.safeSet(inv, 15, SafeGUI.item(Material.LIME_DYE,
                "§a+10000€",
                "§8────────────",
                "§7Liquide: §f" + SafeGUI.money(cash),
                "§7Banque: §f" + SafeGUI.money(bank),
                "",
                (cash >= 10000 ? "§aDisponible" : "§cFonds insuffisants")));

        // 🔥 MAX
        SafeGUI.safeSet(inv, 20, SafeGUI.item(Material.EMERALD_BLOCK,
                "§aTout déposer",
                "§8────────────",
                "§7Dépose tout ton liquide",
                "",
                (cash > 0 ? "§aClique pour tout déposer" : "§cRien à déposer")));

        // 💬 CUSTOM
        SafeGUI.safeSet(inv, 24, SafeGUI.item(Material.OAK_SIGN,
                "§eMontant personnalisé",
                "§8────────────",
                "§7Clique puis écris un montant",
                "",
                "§aClique"));

        // 🔙 Retour
        SafeGUI.safeSet(inv, 22, SafeGUI.item(Material.BARRIER, "§cRetour"));

        GUIManager.open(p, "bank_deposit", inv);
    }
}