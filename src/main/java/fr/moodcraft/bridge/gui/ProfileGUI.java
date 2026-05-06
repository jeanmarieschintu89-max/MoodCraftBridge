package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.hook.JobsHook;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.ReputationManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        //
        // 🌌 FOND
        //

        SafeGUI.fill(

                inv,

                Material.BLACK_STAINED_GLASS_PANE,

                " "
        );

        //
        // 👤 INFOS
        //

        String name =
                Bukkit.getOfflinePlayer(targetUUID)
                        .getName();

        if (name == null)
            name = "Inconnu";

        double bank =
                BankStorage.get(
                        targetUUID.toString()
                );

        //
        // 🧠 RÉPUTATION
        //

        int rep =
                ReputationManager.get(
                        targetUUID.toString()
                );

        String rank =
                ReputationManager.getRank(rep);

        //
        // 🛠️ MÉTIERS
        //

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

            jobsLore.add(
                    "§7Joueur hors ligne"
            );
        }

        if (jobsLore.isEmpty()) {

            jobsLore.add(
                    "§7Aucun métier"
            );
        }

        //
        // 👤 LORE PROFIL
        //

        List<String> lore =
                new ArrayList<>();

        lore.add("§8━━━━━━━━━━━━━━━━");
        lore.add("§7Profil économique MoodCraft");
        lore.add("");

        lore.add(
                "§7Banque: §6"
                        + SafeGUI.money(bank)
                        + "€"
        );

        lore.add("");

        lore.add(
                "§7Réputation: §a"
                        + rep
        );

        lore.add(
                "§7Rang: "
                        + rank
        );

        lore.add("");

        lore.add(
                "§d✦ Métiers"
        );

        lore.add("§8────────────");

        lore.addAll(jobsLore);

        lore.add("");

        lore.add(
                "§8• Activité économique"
        );

        lore.add(
                "§8• Profil commercial"
        );

        lore.add(
                "§8• Statistiques serveur"
        );

        //
        // 🧑 TÊTE
        //

        ItemStack head =
                new ItemStack(
                        Material.PLAYER_HEAD
                );

        if (head.getItemMeta()
                instanceof SkullMeta meta) {

            meta.setOwningPlayer(
                    Bukkit.getOfflinePlayer(
                            targetUUID
                    )
            );

            meta.setDisplayName(
                    "§6✦ §f" + name
            );

            meta.setLore(lore);

            head.setItemMeta(meta);
        }

        SafeGUI.safeSet(
                inv,
                13,
                SafeGUI.glow(head)
        );

        //
        // 🏦 INFOS ÉCO
        //

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

        //
        // 📜 RÉPUTATION
        //

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

                viewer,

                "profile_gui",

                inv
        );
    }
}