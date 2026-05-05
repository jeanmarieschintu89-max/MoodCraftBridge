package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.gui.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TransferTypeGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 9, "§eType de virement");

        SafeGUI.safeSet(inv, 2,
                SafeGUI.item(Material.PLAYER_HEAD, "§aVers joueur"));

        SafeGUI.safeSet(inv, 6,
                SafeGUI.item(Material.PAPER, "§bVia IBAN"));

        SafeGUI.safeSet(inv, 8,
                SafeGUI.item(Material.BARRIER, "§cRetour"));

        GUIManager.open(p, "transfer_type", inv);
    }
}