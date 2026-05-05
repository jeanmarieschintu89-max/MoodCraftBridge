package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class IbanGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§bVirement IBAN");

        // 🔲 background propre
        for (int i = 0; i < 27; i++) {
            SafeGUI.safeSet(inv, i,
                    SafeGUI.item(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // 💳 INPUT IBAN
        SafeGUI.safeSet(inv, 13,
                SafeGUI.item(Material.PAPER,
                        "§eEntrer un IBAN",
                        "§8────────────",
                        "§7Clique puis écris",
                        "§7l'IBAN dans le chat",
                        "",
                        "§aClique"));

        // 🔙 RETOUR
        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(Material.BARRIER,
                        "§cRetour",
                        "§7Retour au menu précédent"));

        GUIManager.open(p, "iban_gui", inv);
    }
}