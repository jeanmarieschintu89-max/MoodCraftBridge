package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TargetPlayerGUI {

    private static final Map<Integer, UUID> slotMap =
            new HashMap<>();

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        "§8✦ §aDestinataire"
                );

        slotMap.clear();

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        SafeGUI.safeSet(inv, 4,
                SafeGUI.glow(
                        button(
                                Material.PAPER,
                                "§6✦ Choisir un joueur",
                                "§8----- §6Virement §8-----",
                                "§7Sélectionne un joueur connecté.",
                                "",
                                "§8• §7Instantané",
                                "§8• §7Sécurisé",
                                "§8• §7Historique sauvegardé"
                        )
                )
        );

        int slot = 10;

        for (Player target : Bukkit.getOnlinePlayers()) {

            if (target.equals(p))
                continue;

            if (slot == 17)
                slot = 19;

            if (slot == 26)
                slot = 28;

            if (slot == 35)
                slot = 37;

            if (slot >= 44)
                break;

            slotMap.put(
                    slot,
                    target.getUniqueId()
            );

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
                        "§7Joueur connecté.",
                        "",
                        "§8• §7Virement instantané",
                        "§8• §7Transaction sécurisée",
                        "",
                        "§e▶ Sélectionner"
                ));

                hide(meta);

                head.setItemMeta(meta);
            }

            SafeGUI.safeSet(
                    inv,
                    slot,
                    SafeGUI.glow(head)
            );

            slot++;
        }

        if (slotMap.isEmpty()) {

            SafeGUI.safeSet(inv, 22,
                    button(
                            Material.BARRIER,
                            "§c✖ Aucun joueur",
                            "§7Aucun autre joueur connecté.",
                            "",
                            "§8• §7Utilise plutôt l'IBAN",
                            "",
                            "§c▶ Retour"
                    )
            );
        }

        SafeGUI.safeSet(inv, 45,
                button(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au type de virement.",
                        "",
                        "§c▶ Retour"
                )
        );

        SafeGUI.safeSet(inv, 49,
                button(
                        Material.NAME_TAG,
                        "§b✦ IBAN",
                        "§7Besoin d'envoyer hors ligne ?",
                        "",
                        "§8• §7Retour puis choisis IBAN"
                )
        );

        GUIManager.open(
                p,
                "transfer_target",
                inv
        );
    }

    public static UUID getTarget(int slot) {

        return slotMap.get(slot);
    }

    private static ItemStack button(
            Material material,
            String name,
            String... lore
    ) {

        ItemStack item =
                SafeGUI.item(
                        material,
                        name,
                        lore
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            hide(meta);

            item.setItemMeta(meta);
        }

        return item;
    }

    private static void hide(
            ItemMeta meta
    ) {

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );

        try {

            ItemFlag flag =
                    ItemFlag.valueOf(
                            "HIDE_ITEM_SPECIFICS"
                    );

            meta.addItemFlags(flag);

        } catch (IllegalArgumentException ignored) {}
    }
}