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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
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

        String townName =
                "Aucune ville";

        String nationName =
                "Aucune nation";

        boolean hasTown =
                false;

        boolean hasNation =
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

                hasNation =
                        true;

                nationName =
                        nation.getName();
            }
        }

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        ItemStack shield =
                null;

        String source =
                "player";

        if (hasTown) {

            shield =
                    MoodTownFlagAPI.getTownShieldItem(
                            townName
                    );

            if (shield != null) {

                source =
                        "town";
            }
        }

        if (shield == null
                && hasNation) {

            shield =
                    MoodTownFlagAPI.getNationShieldItem(
                            nationName
                    );

            if (shield != null) {

                source =
                        "nation";
            }
        }

        if (shield == null) {

            shield =
                    new ItemStack(
                            Material.PLAYER_HEAD
                    );

            if (shield.getItemMeta()
                    instanceof SkullMeta skullMeta) {

                skullMeta.setOwningPlayer(p);

                shield.setItemMeta(
                        skullMeta
                );
            }
        }

        List<String> shieldLore =
                new ArrayList<>();

        shieldLore.add("§8━━━━━━━━━━━━━━━━");
        shieldLore.add("§7Profil territorial MoodCraft");
        shieldLore.add("");
        shieldLore.add("§7Ville: §e" + townName);
        shieldLore.add("§7Nation: §6" + nationName);
        shieldLore.add("");

        if (source.equalsIgnoreCase("town")) {

            shieldLore.add("§a✔ Blason municipal affiché");

        } else if (source.equalsIgnoreCase("nation")) {

            shieldLore.add("§a✔ Blason national affiché");

        } else {

            shieldLore.add("§7Aucun blason officiel.");
            shieldLore.add("§7Affichage du profil joueur.");
        }

        shieldLore.add("");
        shieldLore.add("§7Liquide: §a" + SafeGUI.money(cash) + "€");
        shieldLore.add("§7Banque: §6" + SafeGUI.money(bank) + "€");
        shieldLore.add("§7Patrimoine: §e" + SafeGUI.money(total) + "€");
        shieldLore.add("");
        shieldLore.add("§8• Profil");
        shieldLore.add("§8• Identité territoriale");
        shieldLore.add("§8• Registre héraldique");
        shieldLore.add("");
        shieldLore.add("§e▶ Consulter le profil");

        ItemMeta shieldMeta =
                shield.getItemMeta();

        if (shieldMeta != null) {

            shieldMeta.setDisplayName(
                    source.equalsIgnoreCase("player")
                            ? "§6✦ Profil Joueur"
                            : "§6✦ Registre Territorial"
            );

            shieldMeta.setLore(
                    shieldLore
            );

            shieldMeta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_ITEM_SPECIFICS,
                    ItemFlag.HIDE_ADDITIONAL_TOOLTIP
            );

            shield.setItemMeta(
                    shieldMeta
            );
        }

        inv.setItem(
                4,
                shield
        );

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