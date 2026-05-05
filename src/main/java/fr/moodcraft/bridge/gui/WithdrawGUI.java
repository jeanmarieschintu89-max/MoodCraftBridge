package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.handler.GUIHandler;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.InputManager;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.bank.BankStorage;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class WithdrawGUI implements GUIHandler {

    private static final String ID = "bank_withdraw";

    // 🔥 enregistrement handler
    public WithdrawGUI() {
        GUIManager.register(ID, this);
    }

    public void open(Player p) {

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

    @Override
    public void onClick(Player p, int slot) {

        Economy eco = VaultHook.getEconomy();
        if (eco == null) return;

        double bank = BankStorage.get(p.getUniqueId().toString());
        double amount = 0;

        // 💸 montants fixes
        if (slot == 11) amount = 100;
        if (slot == 13) amount = 1000;
        if (slot == 15) amount = 10000;

        // 🔙 retour
        if (slot == 22) {
            BankGUI.open(p);
            return;
        }

        // 🔥 MAX
        if (slot == 20) {

            if (bank <= 0) {
                p.sendMessage("§cTu n'as rien en banque.");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            }

            BankStorage.remove(p.getUniqueId().toString(), bank);
            eco.depositPlayer(p, bank);

            p.sendMessage("§aTu as tout retiré : §f" + SafeGUI.money(bank));
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            open(p);
            return;
        }

        // 💬 CUSTOM
        if (slot == 24) {
            p.closeInventory();
            AmountInputManager.wait(p, AmountInputManager.Type.WITHDRAW);
            InputManager.wait(p, "amount_input");
            p.sendMessage("§eEntre le montant à retirer dans le chat.");
            return;
        }

        // ❌ rien cliqué
        if (amount == 0) return;

        // ❌ pas assez
        if (bank < amount) {
            p.sendMessage("§cPas assez d'argent en banque.");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        // 💰 retrait
        BankStorage.remove(p.getUniqueId().toString(), amount);
        eco.depositPlayer(p, amount);

        p.sendMessage("§aRetrait de §f" + SafeGUI.money(amount));
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        open(p);
    }
}