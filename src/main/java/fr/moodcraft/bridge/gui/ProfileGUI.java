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
                        "§6✦ §8Profil §aMood§6Craft §6✦"
                );

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        String name =
                Bukkit.getOfflinePlayer(targetUUID)
                        .getName();

        if (name == null) {
            name = "Inconnu";
        }

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

        identityLore.add("§8----- §6✦ Identité ✦ §8-----");
        identityLore.add("");
        identityLore.add("§7Joueur: §f" + shortText(name, 18));
        identityLore.add("§7Ville: §b" + shortText(townName, 18));
        identityLore.add("§7Nation: §6" + shortText(nationName, 18));
        identityLore.add("");

        if (source.equalsIgnoreCase("town")) {

            identityLore.add("§a✔ Blason de ville");

        } else if (source.equalsIgnoreCase("nation")) {

            identityLore.add("§a✔ Blason de nation");

        } else {

            identityLore.add("§7Profil joueur affiché");
            identityLore.add("§8• §7Aucun blason défini");
        }

        identityLore.add("");
        identityLore.add("§7Banque: §6" + SafeGUI.money(bank) + "€");
        identityLore.add("§7Réputation: §a" + rep);
        identityLore.add("§7Rang: " + rank);
        identityLore.add("");

        identityLore.add("§6✦ §fMétiers");

        int added = 0;

        for (String line : jobsLore) {

            if (added >= 4) {
                identityLore.add("§8• §7...");
                break;
            }

            identityLore.add(
                    "§8• §7" + shortText(
                            cleanColor(line),
                            24
                    )
            );

            added++;
        }

        ItemMeta identityMeta =
                identity.getItemMeta();

        if (identityMeta != null) {

            identityMeta.setDisplayName(
                    "§6✦ §f" + shortText(name, 18) + " §6✦"
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
                        "§6✦ §fÉconomie §6✦",
                        "§7Argent et réputation.",
                        "",
                        "§8• §7Banque: §6"
                                + SafeGUI.money(bank)
                                + "€",
                        "§8• §7Réputation: §a"
                                + rep,
                        "",
                        "§eClique pour voir"
                )
        );

        SafeGUI.safeSet(inv, 23,
                button(
                        Material.BOOK,
                        "§6✦ §fRéputation §6✦",
                        "§7Montre ta place",
                        "§7dans la communauté.",
                        "",
                        "§8• §7Rang:",
                        rank,
                        "",
                        "§eClique pour consulter"
                )
        );

        SafeGUI.safeSet(inv, 31,
                button(
                        Material.ARROW,
                        "§c✦ Retour",
                        "§7Retour au menu précédent.",
                        "",
                        "§cClique pour revenir"
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

    private static String cleanColor(
            String text
    ) {

        if (text == null) {
            return "";
        }

        return text.replaceAll("§.", "");
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