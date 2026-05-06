package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TargetPlayerGUI {

    //
    // 🔥 SLOT → UUID
    //

    private static final Map<Integer, UUID> slotMap =
            new HashMap<>();

    //
    // 📂 OPEN
    //

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(

                        null,

                        54,

                        "§8✦ §fChoix du Joueur"
                );

        //
        // 🌌 RESET
        //

        slotMap.clear();

        //
        // 🌌 FOND
        //

        SafeGUI.fill(

                inv,

                Material.BLACK_STAINED_GLASS_PANE,

                " "
        );

        //
        // 📄 HEADER
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.PAPER,

                                "§6✦ Sélection du Destinataire",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Choisis un joueur",

                                "§7connecté au serveur.",

                                "",

                                "§8• Virement instantané",

                                "§8• Transaction sécurisée",

                                "§8• Historique bancaire",

                                "",

                                "§e▶ Sélectionner"
                        )
                )
        );

        //
        // 👥 JOUEURS
        //

        int slot = 10;

        for (Player target : Bukkit.getOnlinePlayers()) {

            //
            // ❌ SOI-MÊME
            //

            if (target.equals(p))
                continue;

            //
            // 🔥 ÉVITE BORDURES
            //

            if (slot == 17)
                slot = 19;

            if (slot == 26)
                slot = 28;

            if (slot == 35)
                break;

            //
            // 💾 SAVE UUID
            //

            slotMap.put(
                    slot,
                    target.getUniqueId()
            );

            //
            // 👤 HEAD
            //

            ItemStack head =
                    new ItemStack(
                            Material.PLAYER_HEAD
                    );

            if (head.getItemMeta()
                    instanceof SkullMeta meta) {

                meta.setOwningPlayer(target);

                meta.setDisplayName(
                        "§a✦ " + target.getName()
                );

                meta.setLore(java.util.List.of(

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Joueur actuellement",

                        "§7connecté au serveur.",

                        "",

                        "§8• Disponible",

                        "§8• Transaction instantanée",

                        "§8• Sécurisé",

                        "",

                        "§e▶ Sélectionner"
                ));

                head.setItemMeta(meta);
            }

            SafeGUI.safeSet(

                    inv,

                    slot,

                    SafeGUI.glow(head)
            );

            slot++;
        }

        //
        // 📊 INFOS
        //

        SafeGUI.safeSet(inv, 49,

                SafeGUI.item(

                        Material.BOOK,

                        "§6✦ Réseau Bancaire",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Les virements MoodCraft",

                        "§7sont protégés et",

                        "§7sauvegardés automatiquement.",

                        "",

                        "§8• Logs sécurisés",

                        "§8• Détection fraude",

                        "§8• Historique complet"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 45,

                SafeGUI.item(

                        Material.ARROW,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu précédent.",

                        "",

                        "§e▶ Revenir"
                )
        );

        //
        // 📂 OPEN
        //

        GUIManager.open(

                p,

                "transfer_target",

                inv
        );
    }

    //
    // 🔍 GET TARGET
    //

    public static UUID getTarget(int slot) {

        return slotMap.get(slot);
    }
}