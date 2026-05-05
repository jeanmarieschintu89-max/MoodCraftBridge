package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.TransferBuilder;
import fr.moodcraft.bridge.gui.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Sound;

public class TransferConfirmGUI {

    public static void open(Player p) {

        TransferBuilder data = TransferBuilder.get(p);

        Inventory inv = Bukkit.createInventory(null, 9, "§6Confirmer le virement");

        String targetName = "Inconnu";

        if (data.target != null) {
            var offline = Bukkit.getOfflinePlayer(data.target);
            if (offline.getName() != null) {
                targetName = offline.getName();
            }
        }

        int amount = (int) data.amount;

        // ❌ ANNULER
        SafeGUI.safeSet(inv, 3,
                SafeGUI.item(Material.REDSTONE,
                        "§c✖ Annuler",
                        "§8────────────",
                        "§7Retour au menu"));

        // ✅ CONFIRMER
        SafeGUI.safeSet(inv, 5,
                SafeGUI.item(Material.LIME_DYE,
                        "§a✔ Confirmer",
                        "§8────────────",
                        "§7Destinataire: §e" + targetName,
                        "§7Montant: §6" + amount + "€",
                        "",
                        "§e▶ Clique pour valider"));

        // 🔊 feedback
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);

        GUIManager.open(p, "transfer_confirm", inv);
    }
}