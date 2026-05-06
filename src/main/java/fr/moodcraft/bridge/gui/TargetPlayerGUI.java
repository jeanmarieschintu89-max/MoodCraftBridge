package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TargetPlayerGUI {

    // 🔥 mapping slot → joueur
    private static final Map<Integer, UUID> slotMap = new HashMap<>();

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        "§fChoix du joueur"
                );

        // 🔥 reset mapping
        slotMap.clear();

        int slot = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {

            // ❌ soi-même
            if (target.equals(p)) continue;

            // ❌ sécurité taille
            if (slot >= 45) break;

            // 💾 save UUID
            slotMap.put(slot, target.getUniqueId());

            // 👤 tête joueur
            SafeGUI.safeSet(
                    inv,
                    slot,
                    SafeGUI.item(
                            Material.PLAYER_HEAD,
                            "§a" + target.getName(),
                            "§8────────────",
                            "§7Clique pour sélectionner",
                            "",
                            "§e▶ Virement"
                    )
            );

            slot++;
        }

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(
                inv,
                49,
                SafeGUI.item(
                        Material.ARROW,
                        "§cRetour"
                )
        );

        //
        // 📂 IMPORTANT
        // ⚠ ancien bug ici
        //

        GUIManager.open(
                p,
                "transfer_target",
                inv
        );
    }

    public static UUID getTarget(int slot) {

        return slotMap.get(slot);
    }
}