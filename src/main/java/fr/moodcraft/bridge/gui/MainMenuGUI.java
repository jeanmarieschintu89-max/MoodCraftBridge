package fr.moodcraft.bridge.gui;

import com.palmergames.bukkit.towny.TownyAPI;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import fr.moodcraft.flag.api.MoodTownFlagAPI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;

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
        // 🏛 TOWNY
        //

        String townName =
                "Aucune ville";

        String nationName =
                "Aucune nation";

        boolean hasTown =
                false;

        Resident resident =
                TownyAPI.getInstance()
                        .getResident(
                                p.getUniqueId()
                        );

        if (resident != null
                && resident.hasTown()) {

            Town town =
                    resident.getTownOrNull();

            if (town != null) {

                hasTown =
                        true;

                townName =
                        town.getName();
            }
        }

        if (resident != null
                && resident.hasNation()) {

            Nation nation =
                    resident.getNationOrNull();

            if (nation != null) {

                nationName =
                        nation.getName();
            }
        }

        //
        // 🌌 FOND
        //

        SafeGUI.fill(

                inv,

                Material.BLACK_STAINED_GLASS_PANE,

                " "
        );

        //
        // 🎌 REGISTRE TERRITORIAL
        //

        ItemStack flag =
                null;

        if (hasTown) {

            flag =
                    MoodTownFlagAPI.getTownFlagItem(
                            townName
                    );
        }

        if (flag == null) {

            flag =
                    new ItemStack(
                            hasTown
                                    ? Material.WHITE_BANNER
                                    : Material.BARRIER
                    );
        }

        List<String> flagLore =
                new ArrayList<>();

        flagLore.add("§8━━━━━━━━━━━━━━━━");

        if (hasTown) {

            flagLore.add("§7Identité territoriale MoodCraft");
            flagLore.add("");
            flagLore.add("§7Ville: §e" + townName);
            flagLore.add("§7Nation: §6" + nationName);
            flagLore.add("");
            flagLore.add("§8• Drapeau municipal");
            flagLore.add("§8• Registre héraldique");
            flagLore.add("§8• Identité officielle");
            flagLore.add("");
            flagLore.add("§e▶ Consulter");

        } else {

            flagLore.add("§7Aucune identité territoriale.");
            flagLore.add("");
            flagLore.add("§cAucune ville enregistrée.");
            flagLore.add("§7Rejoignez une ville pour");
            flagLore.add("§7obtenir un drapeau officiel.");
            flagLore.add("");
            flagLore.add("§8• Ville requise");
            flagLore.add("§8• Nation optionnelle");
            flagLore.add("");
            flagLore.add("§c▶ Indisponible");
        }

        ItemMeta flagMeta =
                flag.getItemMeta();

        if (flagMeta != null) {

            flagMeta.setDisplayName(
                    hasTown
                            ? "§6✦ Registre Territorial"
                            : "§c✦ Aucun Territoire"
            );

            flagMeta.setLore(
                    flagLore
            );

            flag.setItemMeta(
                    flagMeta
            );
        }

        inv.setItem(
                4,
                flag
        );

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