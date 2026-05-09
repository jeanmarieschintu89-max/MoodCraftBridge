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

        Inventory inv = Bukkit.createInventory(
                null,
                36,
                "§8✦ §6Profil MoodCraft"
        );

        SafeGUI.fill(inv, Material.BLACK_STAINED_GLASS_PANE, " ");

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

        ItemStack flag =
                null;

        String source =
                "player";

        if (hasTown) {

            flag =
                    MoodTownFlagAPI.getTownFlagItem(
                            townName
                    );

            if (flag != null) {

                source =
                        "town";
            }
        }

        if (flag == null
                && hasNation) {

            flag =
                    MoodTownFlagAPI.getNationFlagItem(
                            nationName
                    );

            if (flag != null) {

                source =
                        "nation";
            }
        }

        if (flag == null) {

            flag =
                    new ItemStack(
                            Material.PLAYER_HEAD
                    );

            if (flag.getItemMeta()
                    instanceof SkullMeta skullMeta) {

                skullMeta.setOwningPlayer(
                        Bukkit.getOfflinePlayer(
                                targetUUID
                        )
                );

                flag.setItemMeta(
                        skullMeta
                );
            }
        }

        List<String> territoryLore =
                new ArrayList<>();

        territoryLore.add("§8━━━━━━━━━━━━━━━━");
        territoryLore.add("§7Profil économique MoodCraft");
        territoryLore.add("");
        territoryLore.add("§7Ville: §e" + townName);
        territoryLore.add("§7Nation: §6" + nationName);
        territoryLore.add("");

        if (source.equalsIgnoreCase("town")) {

            territoryLore.add("§a✔ Drapeau municipal affiché");

        } else if (source.equalsIgnoreCase("nation")) {

            territoryLore.add("§a✔ Drapeau national affiché");

        } else {

            territoryLore.add("§7Aucun drapeau officiel.");
            territoryLore.add("§7Affichage du profil joueur.");
        }

        territoryLore.add("");
        territoryLore.add(
                "§7Banque: §6"
                        + SafeGUI.money(bank)
                        + "€"
        );

        territoryLore.add("");
        territoryLore.add("§7Réputation: §a" + rep);
        territoryLore.add("§7Rang: " + rank);
        territoryLore.add("");
        territoryLore.add("§d✦ Métiers");
        territoryLore.add("§8────────────");
        territoryLore.addAll(jobsLore);
        territoryLore.add("");
        territoryLore.add("§8• Activité économique");
        territoryLore.add("§8• Profil commercial");
        territoryLore.add("§8• Statistiques serveur");

        ItemMeta flagMeta =
                flag.getItemMeta();

        if (flagMeta != null) {

            flagMeta.setDisplayName(
                    "§6✦ §f" + name
            );

            flagMeta.setLore(
                    territoryLore
            );

            flag.setItemMeta(
                    flagMeta
            );
        }

        SafeGUI.safeSet(
                inv,
                13,
                SafeGUI.glow(flag)
        );

        SafeGUI.safeSet(inv, 21,

                SafeGUI.item(
                        Material.GOLD_INGOT,
                        "§6✦ Activité Économique",
                        "§8━━━━━━━━━━━━━━━━",
                        "§7Analyse financière",
                        "§7du joueur.",
                        "",
                        "§7Fortune bancaire: §6"
                                + SafeGUI.money(bank)
                                + "€",
                        "",
                        "§7Indice réputation: §a"
                                + rep,
                        "",
                        "§7Classe sociale:",
                        rank,
                        "",
                        "§e▶ Données économiques"
                )
        );

        SafeGUI.safeSet(inv, 23,

                SafeGUI.item(
                        Material.BOOK,
                        "§d✦ Réputation MoodCraft",
                        "§8━━━━━━━━━━━━━━━━",
                        "§7Le système de réputation",
                        "§7définit l'influence sociale.",
                        "",
                        "§8• Commerce",
                        "§8• Contrats",
                        "§8• Prestige",
                        "§8• Confiance",
                        "",
                        "§7Statut actuel:",
                        rank,
                        "",
                        "§e▶ Système social"
                )
        );

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
                viewer,
                "profile_gui",
                inv
        );
    }
}