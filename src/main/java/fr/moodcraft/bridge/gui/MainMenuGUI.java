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

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§6✦ §8Menu §aMood§6Craft §6✦"
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

                skullMeta.setOwningPlayer(p);

                identity.setItemMeta(
                        skullMeta
                );
            }
        }

        List<String> identityLore =
                new ArrayList<>();

        identityLore.add("§8----- §6✦ Identité ✦ §8-----");
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
        identityLore.add("§7Liquide: §a" + SafeGUI.money(cash) + "€");
        identityLore.add("§7Banque: §6" + SafeGUI.money(bank) + "€");
        identityLore.add("§7Total: §e" + SafeGUI.money(total) + "€");
        identityLore.add("");
        identityLore.add("§eClique pour voir");

        ItemMeta identityMeta =
                identity.getItemMeta();

        if (identityMeta != null) {

            identityMeta.setDisplayName(
                    source.equalsIgnoreCase("player")
                            ? "§6✦ §fTon profil §6✦"
                            : "§6✦ §fTon blason §6✦"
            );

            identityMeta.setLore(
                    identityLore
            );

            hide(identityMeta);

            identity.setItemMeta(
                    identityMeta
            );
        }

        inv.setItem(
                4,
                identity
        );

        SafeGUI.safeSet(inv, 10,
                SafeGUI.glow(
                        button(
                                Material.GOLD_INGOT,
                                "§6✦ §fBanque §6✦",
                                "§7Gère ton argent.",
                                "",
                                "§8• §7Déposer",
                                "§8• §7Retirer",
                                "§8• §7Virement",
                                "",
                                "§7Banque: §6"
                                        + SafeGUI.money(bank)
                                        + "€",
                                "§7Liquide: §a"
                                        + SafeGUI.money(cash)
                                        + "€",
                                "",
                                "§eClique pour ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                button(
                        Material.EMERALD,
                        "§6✦ §fMarché §6✦",
                        "§7Vends tes ressources.",
                        "",
                        "§8• §7Prix qui bougent",
                        "§8• §7Taxe du marché",
                        "§8• §7Gain direct",
                        "",
                        "§eClique pour ouvrir"
                )
        );

        /*
         * Ancien bouton Projets retiré.
         * Les projets urbains restent uniquement dans MoodTownMenu.
         */

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        button(
                                Material.LECTERN,
                                "§6✦ §fBureau des Entreprises §6✦",
                                "§7Crée ou gère",
                                "§7une entreprise.",
                                "",
                                "§8• §7Employés",
                                "§8• §7Stages",
                                "§8• §7Demandes",
                                "§8• §7Contrats",
                                "§8• §7Banque entreprise",
                                "",
                                "§a✔ Service §aMood§6Craft",
                                "§eClique pour ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                button(
                        Material.COMPASS,
                        "§6✦ §fTéléportation §6✦",
                        "§7Va rapidement",
                        "§7à un lieu utile.",
                        "",
                        "§8• §7Spawn",
                        "§8• §7Ville",
                        "§8• §7Exploration",
                        "",
                        "§eClique pour voyager"
                )
        );

        SafeGUI.safeSet(inv, 21,
                button(
                        Material.MAP,
                        "§6✦ §fVille §6✦",
                        "§7Gère ta ville",
                        "§7ou rejoins-en une.",
                        "",
                        "§8• §7Ville",
                        "§8• §7Claims",
                        "§8• §7Nation",
                        "§8• §7Projets",
                        "",
                        "§eClique pour ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 23,
                button(
                        Material.EXPERIENCE_BOTTLE,
                        "§6✦ §fMétiers §6✦",
                        "§7Progresse avec",
                        "§7tes activités.",
                        "",
                        "§8• §7Niveaux",
                        "§8• §7Récompenses",
                        "§8• §7Progression",
                        "",
                        "§eClique pour ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 31,
                button(
                        Material.BARRIER,
                        "§c✦ Fermer",
                        "§7Quitter le menu.",
                        "",
                        "§cClique pour fermer"
                )
        );

        GUIManager.open(
                p,
                "main_menu",
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
            return "Aucun";
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