package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.TransferBuilder;
import fr.moodcraft.bridge.gui.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TransferAmountGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§a💸 Choix du montant");

        TransferBuilder data = TransferBuilder.get(p);

        String targetName = "§7Inconnu";

        if (data.target != null) {
            var t = Bukkit.getPlayer(data.target);
            if (t != null) targetName = "§e" + t.getName();
        }

        // 💳 INFO
        SafeGUI.safeSet(inv, 4, SafeGUI.item(Material.PAPER,
                "§e📤 Virement",
                "§8────────────",
                "§7Destinataire: " + targetName,
                "",
                "§7Choisis un montant"));

        // 💰 PETITS MONTANTS
        SafeGUI.safeSet(inv, 10, SafeGUI.item(Material.GOLD_NUGGET,
                "§a100€",
                "§8────────────",
                "§7Clique pour envoyer"));

        SafeGUI.safeSet(inv, 11, SafeGUI.item(Material.GOLD_INGOT,
                "§a1 000€",
                "§8────────────",
                "§7Clique pour envoyer"));

        SafeGUI.safeSet(inv, 12, SafeGUI.item(Material.GOLD_BLOCK,
                "§a10 000€",
                "§8────────────",
                "§7Clique pour envoyer"));

        // 💎 GROS MONTANTS
        SafeGUI.safeSet(inv, 14, SafeGUI.item(Material.EMERALD,
                "§a50 000€",
                "§8────────────",
                "§7Gros transfert"));

        SafeGUI.safeSet(inv, 15, SafeGUI.item(Material.DIAMOND,
                "§a100 000€",
                "§8────────────",
                "§7Transfert massif"));

        // ✏️ PERSONNALISÉ
        SafeGUI.safeSet(inv, 16, SafeGUI.item(Material.ANVIL,
                "§eMontant personnalisé",
                "§8────────────",
                "§7Entre le montant",
                "§7dans le chat",
                "",
                "§8Clique pour écrire"));

        // 🔙 RETOUR
        SafeGUI.safeSet(inv, 22, SafeGUI.item(Material.ARROW,
                "§c⬅ Retour"));

        GUIManager.open(p, "transfer_amount", inv);
    }
}