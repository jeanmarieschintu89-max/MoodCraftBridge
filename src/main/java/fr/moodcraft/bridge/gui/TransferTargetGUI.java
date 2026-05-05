package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TransferTargetGUI {

    public static void open(Player p) {

        // 🔥 titre aligné avec ton listener
        Inventory inv = Bukkit.createInventory(null, 27, "§eChoisir joueur virement");

        int slot = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {

            if (target.equals(p)) continue;
            if (slot >= 27) break;

            SafeGUI.safeSet(inv, slot,
                    SafeGUI.item(Material.PLAYER_HEAD,
                            "§a" + target.getName(),
                            "§8────────",
                            "§7Cliquer pour sélectionner"));

            slot++;
        }

        GUIManager.open(p, "transfer_target", inv);
    }
}