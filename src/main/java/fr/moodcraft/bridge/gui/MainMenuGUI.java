package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class MainMenuGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §6MoodCraft"
        );

        //
        // 💰 ARGENT
        //

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        double cash = 0;

        try {

            cash =
                    VaultHook.getBalance(p);

        } catch (Exception ignored) {}

        double total =
                bank + cash;

        //
        // 🌌 FOND
        //

        SafeGUI.fill(

                inv,

                Material.BLACK_STAINED_GLASS_PANE,

                " "
        );

        //
        // 👤 PROFIL
        //

        List<String> lore =
                new ArrayList<>();

        lore.add("§8━━━━━━━━━━━━━━━━");
        lore.add("§7Profil économique MoodCraft");
        lore.add("");
        lore.add("§7Liquide: §a" + SafeGUI.money(cash) + "€");
        lore.add("§7Banque: §6" + SafeGUI.money(bank) + "€");
        lore.add("§7Patrimoine: §e" + SafeGUI.money(total) + "€");
        lore.add("");
        lore.add("§8• Statistiques");
        lore.add("§8• Réputation");
        lore.add("§8• Progression");
        lore.add("");
        lore.add("§e▶ Consulter");

        ItemStack head =
                new ItemStack(Material.PLAYER_HEAD);

        if (head.getItemMeta() instanceof SkullMeta meta) {

            meta.setOwningPlayer(p);

            if (p.getName() != null) {

                meta.setOwnerProfile(
                        Bukkit.createPlayerProfile(
                                p.getUniqueId(),
                                p.getName()
                        )
                );
            }

            meta.setDisplayName(
                    "§6✦ §f" + p.getName()
            );

            meta.setLore(lore);

            head.setItemMeta(meta);
        }

        inv.setItem(4, head);

        //
        // 🏦 BANQUE
        //

        SafeGUI.safeSet(inv, 11,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.GOLD_INGOT,

                                "§6✦ Banque",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Gestion bancaire",

                                "§7et transactions sécurisées.",

                                "",

                                "§7Solde bancaire: §6"
                                        + SafeGUI.money(bank)
                                        + "€",

                                "",

                                "§8• Dépôt",

                                "§8• Retrait",

                                "§8• Virements",

                                "§8• Historique",

                                "",

                                "§e▶ Accéder"
                        )
                )
        );

        //
        // 📈 BOURSE
        //

        SafeGUI.safeSet(inv, 13,

                SafeGUI.item(

                        Material.CHEST_MINECART,

                        "§e✦ Marché Boursier",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Économie dynamique",

                        "§7pilotée par les joueurs.",

                        "",

                        "§8• Prix variables",

                        "§8• Rareté",

                        "§8• Offre & demande",

                        "",

                        "§e▶ Ouvrir"
                )
        );

        //
        // 🏛 PROJETS URBAINS
        //

        SafeGUI.safeSet(inv, 16,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.WRITABLE_BOOK,

                                "§6✦ Projets Urbains",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Nouveauté municipale",

                                "§7et financement national.",

                                "",

                                "§8• Déposer un projet",

                                "§8• Voter pour les villes",

                                "§8• Notation nationale",

                                "§8• Subventions urbaines",

                                "",

                                "§e▶ Ouvrir /projet"
                        )
                )
        );

        //
        // 🏛️ VILLE
        //

        SafeGUI.safeSet(inv, 20,

                SafeGUI.item(

                        Material.BRICKS,

                        "§a✦ Ville",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Gestion territoriale",

                        "§7et développement urbain.",

                        "",

                        "§8• Town",

                        "§8• Claims",

                        "§8• Économie locale",

                        "",

                        "§e▶ Gérer"
                )
        );

        //
        // ⛏️ MÉTIERS
        //

        SafeGUI.safeSet(inv, 22,

                SafeGUI.item(

                        Material.DIAMOND_PICKAXE,

                        "§d✦ Métiers",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Système de progression",

                        "§7et activités économiques.",

                        "",

                        "§8• Progression",

                        "§8• Récompenses",

                        "§8• Spécialisations",

                        "",

                        "§e▶ Ouvrir"
                )
        );

        //
        // 🌍 TÉLÉPORTATION
        //

        SafeGUI.safeSet(inv, 24,

                SafeGUI.item(

                        Material.COMPASS,

                        "§b✦ Téléportation",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Navigation rapide",

                        "§7dans l'univers MoodCraft.",

                        "",

                        "§8• Spawn",

                        "§8• Ville",

                        "§8• Ressources",

                        "",

                        "§e▶ Voyager"
                )
        );

        //
        // ❌ FERMER
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.BARRIER,

                        "§c✦ Fermer",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Fermer le menu MoodCraft.",

                        "",

                        "§e▶ Quitter"
                )
        );

        GUIManager.open(

                p,

                "main_menu",

                inv
        );
    }
}