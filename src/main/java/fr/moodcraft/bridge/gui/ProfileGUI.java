package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.hook.JobsHook;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.ReputationManager;
import fr.moodcraft.bridge.util.SafeGUI;

import fr.moodcraft.flag.api.MoodTownFlagAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileGUI {

    public static void open(
            org.bukkit.entity.Player viewer,
            UUID targetUUID
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§8✦ §6Profil"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        String name =
                Bukkit.getOfflinePlayer(targetUUID)
                        .getName();

        if (name == null)
            name = "Inconnu";

        double bank =
                BankStorage.get(
                        targetUUID.toString()
                );

        int rep =
                ReputationManager.get(
                        targetUUID.toString()
                );

        String rank =
                ReputationManager.getRank(rep);

        List<String> jobsLore =
                new ArrayList<>();

        var targetPlayer =
                Bukkit.getPlayer(targetUUID);

        if (targetPlayer != null) {

            jobsLore.addAll(
                    JobsHook.getJobsLore(
                            targetPlayer
                    )
            );

        } else {

            jobsLore.add("§7Joueur hors ligne");
        }

        if (jobsLore.isEmpty()) {

            jobsLore.add("§7Aucun métier");
        }

        var resident =
                com.palmergames.bukkit.towny.TownyAPI
                        .getInstance()
                        .getResident(targetUUID);

        String townName =
                "Aucune ville";

        String nationName =
                "Aucune nation";

        boolean hasTown =
                false;

        boolean hasNation =
                false;

        if (resident != null
                && resident.hasTown()
                && resident.getTownOrNull() != null) {

            hasTown =
                    true;

            townName =
                    resident.getTownOrNull()
                            .getName();
        }

        if (resident != null
                && resident.hasNation()
                && resident.getNationOrNull() != null) {

            hasNation =
                    true;

            nationName =
                    resident.getNationOrNull()
                            .getName();
        }

        ItemStack identity =
                null;

        String source =
                "player";

        if (hasTown) {

            identity =
                    MoodTownFlagAPI.getTownShieldItem(
                            townName
                    );

            if (identity != null) {

                source =
                        "town";
            }
        }

        if (identity == null
                && hasNation) {

            identity =
                    MoodTownFlagAPI.getNationShieldItem(
                            nationName
                    );

            if (identity != null) {

                source =
                        "nation";
            }
        }

        if (identity == null) {

            identity =
                    new ItemStack(
                            Material.PLAYER_HEAD
                    );

            if (identity.getItemMeta()
                    instanceof SkullMeta skullMeta) {

                skullMeta.setOwningPlayer(
                        Bukkit.getOfflinePlayer(
                                targetUUID
                        )
                );

                identity.setItemMeta(
                        skullMeta
                );
            }
        }

        List<String> identityLore =
                new ArrayList<>();

        identityLore.add("§8----- §6Identité §8-----");
        identityLore.add("§7Joueur: §f" + name);
        identityLore.add("§7Ville: §b" + townName);
        identityLore.add("§7Nation: §6" + nationName);
        identityLore.add("");

        if (source.equalsIgnoreCase("town")) {

            identityLore.add("§a✔ Blason de ville");

        } else if (source.equalsIgnoreCase("nation")) {

            identityLore.add("§a✔ Blason de nation");

        } else {

            identityLore.add("§7Aucun blason défini");
            identityLore.add("§7Profil joueur affiché");
        }

        identityLore.add("");
        identityLore.add("§7Banque: §6" + SafeGUI.money(bank) + "€");
        identityLore.add("§7Réputation: §a" + rep);
        identityLore.add("§7Rang: " + rank);
        identityLore.add("");
        identityLore.add("§d✦ Métiers");

        for (String line : jobsLore) {

            identityLore.add("§8• " + line);
        }

        ItemMeta identityMeta =
                identity.getItemMeta();

        if (identityMeta != null) {

            identityMeta.setDisplayName(
                    "§6✦ " + name
            );

            identityMeta.setLore(
                    identityLore
            );

            hide(identityMeta);

            identity.setItemMeta(
                    identityMeta
            );
        }

        SafeGUI.safeSet(
                inv,
                13,
                SafeGUI.glow(identity)
        );

        SafeGUI.safeSet(inv, 21,
                button(
                        Material.GOLD_INGOT,
                        "§6✦ Économie",
                        "§7Résumé bancaire du joueur.",
                        "",
                        "§8• §7Banque: §6"
                                + SafeGUI.money(bank)
                                + "€",
                        "§8• §7Réputation: §a"
                                + rep,
                        "",
                        "§e▶ Détails"
                )
        );

        SafeGUI.safeSet(inv, 23,
                button(
                        Material.BOOK,
                        "§d✦ Réputation",
                        "§7Influence sociale MoodCraft.",
                        "",
                        "§8• §7Rang actuel:",
                        rank,
                        "",
                        "§e▶ Consulter"
                )
        );

        SafeGUI.safeSet(inv, 31,
                button(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au menu précédent.",
                        "",
                        "§c▶ Retour"
                )
        );

        GUIManager.open(
                viewer,
                "profile_gui",
                inv
        );
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

            item.setItemMeta(
                    meta
            );
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
                ItemFlag.HIDE_ITEM_SPECIFICS,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );
    }
}