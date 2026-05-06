package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.SkullMeta;

public class TransferTargetGUI {

    public static void open(Player p) {

        //
        // 🔥 GARDE LE MÊME TITRE
        // sinon ton listener casse
        //

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§eChoisir joueur virement"
        );

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

                                "§7Choisis le joueur",

                                "§7à qui envoyer",

                                "§7un virement bancaire.",

                                "",

                                "§8• Transactions sécurisées",

                                "§8• Historique sauvegardé",

                                "",

                                "§e▶ Sélectionner joueur"
                        )
                )
        );

        //
        // 👥 JOUEURS
        //

        int slot = 10;

        for (Player target : Bukkit.getOnlinePlayers()) {

            if (target.equals(p))
                continue;

            //
            // 🔥 évite bordures
            //

            if (slot == 17)
                slot = 19;

            if (slot == 26)
                break;

            //
            // 👤 TÊTE
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

                        "§7Joueur connecté",

                        "§7et disponible.",

                        "",

                        "§8• Virement instantané",

                        "§8• Transaction sécurisée",

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
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.ARROW,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu précédent.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "transfer_target",

                inv
        );
    }
}