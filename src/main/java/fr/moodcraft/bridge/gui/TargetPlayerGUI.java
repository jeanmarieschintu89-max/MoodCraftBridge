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
                        "§6✦ §8Choisir un joueur §6✦"
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
                                "§6✦ §fChoisir un joueur §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Sélectionne un joueur",
                                "§7connecté au serveur.",
                                "",
                                "§8• §7Choix du joueur",
                                "§8• §7Montant dans le chat",
                                "§8• §7Confirmation finale"
                        )
                )
        );

        int slot =
                10;

        boolean found =
                false;

        for (Player target : Bukkit.getOnlinePlayers()) {

            if (target.equals(p)) {
                continue;
            }

            if (slot == 17) {
                slot = 19;
            }

            if (slot == 26) {
                slot = 28;
            }

            if (slot == 35) {
                slot = 37;
            }

            if (slot >= 44) {
                break;
            }

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
                        "§6✦ §f"
                                + shortText(
                                target.getName(),
                                16
                        )
                );

                meta.setLore(java.util.List.of(
                        "§7Joueur connecté.",
                        "",
                        "§8• §7Clique pour choisir",
                        "§8• §7puis écris le montant",
                        "",
                        "§eSélectionner"
                ));

                hide(meta);

                head.setItemMeta(meta);
            }

            SafeGUI.safeSet(
                    inv,
                    slot,
                    SafeGUI.glow(head)
            );

            found =
                    true;

            slot++;
        }

        if (!found) {

            SafeGUI.safeSet(inv, 22,
                    button(
                            Material.BARRIER,
                            "§c✦ Aucun joueur",
                            "§7Aucun autre joueur",
                            "§7n'est connecté.",
                            "",
                            "§8• §7Pour envoyer hors ligne",
                            "§8• §7utilise l'IBAN",
                            "",
                            "§cAucun choix disponible"
                    )
            );
        }

        SafeGUI.safeSet(inv, 45,
                button(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au choix",
                        "§7du virement.",
                        "",
                        "§cClique pour revenir"
                )
        );

        SafeGUI.safeSet(inv, 49,
                button(
                        Material.NAME_TAG,
                        "§6✦ §fIBAN §6✦",
                        "§7Pour envoyer à",
                        "§7un joueur absent.",
                        "",
                        "§8• §7Retour",
                        "§8• §7puis choisis IBAN"
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

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(
                0,
                Math.max(1, max - 3)
        ) + "...";
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