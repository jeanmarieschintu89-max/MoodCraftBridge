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

public class TransferTargetGUI {

    public static void open(Player p) {

        //
        // ⚠ Garde ce titre si ton listener dépend encore du nom exact.
        //

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§6✦ §8Choisir un joueur §6✦"
                );

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
                        item(
                                Material.PAPER,
                                "§6✦ §fChoisir un joueur §6✦",
                                "§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----",
                                "",
                                "§7Choisis le joueur",
                                "§7qui recevra le virement.",
                                "",
                                "§8• §7Joueur connecté",
                                "§8• §7Montant ensuite dans le chat",
                                "§8• §7Confirmation finale",
                                "",
                                "§eClique sur une tête"
                        )
                )
        );

        //
        // 👥 JOUEURS
        //

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
                break;
            }

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

        //
        // ❌ AUCUN JOUEUR
        //

        if (!found) {

            SafeGUI.safeSet(inv, 13,
                    item(
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

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,
                item(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au choix",
                        "§7du virement.",
                        "",
                        "§cClique pour revenir"
                )
        );

        GUIManager.open(
                p,
                "transfer_target",
                inv
        );
    }

    private static ItemStack item(
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