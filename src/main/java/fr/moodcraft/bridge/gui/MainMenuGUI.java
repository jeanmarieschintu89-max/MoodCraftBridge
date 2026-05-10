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

        identityLore.add("§8----- §6Identité §8-----");
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
        identityLore.add("§7Liquide: §a" + SafeGUI.money(cash) + "€");
        identityLore.add("§7Banque: §6" + SafeGUI.money(bank) + "€");
        identityLore.add("§7Total: §e" + SafeGUI.money(total) + "€");
        identityLore.add("");
        identityLore.add("§e▶ Voir le profil");

        ItemMeta identityMeta =
                identity.getItemMeta();

        if (identityMeta != null) {

            identityMeta.setDisplayName(
                    source.equalsIgnoreCase("player")
                            ? "§6✦ Ton Profil"
                            : "§6✦ Ton Blason"
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
                                "§6✦ Banque",
                                "§7Gère ton argent.",
                                "",
                                "§8• §7Solde: §6"
                                        + SafeGUI.money(bank)
                                        + "€",
                                "§8• §7Liquide: §a"
                                        + SafeGUI.money(cash)
                                        + "€",
                                "",
                                "§e▶ Ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 12,
                button(
                        Material.EMERALD,
                        "§e✦ Marché",
                        "§7Suis les prix du serveur.",
                        "",
                        "§8• §7Prix dynamiques",
                        "§8• §7Offre et demande",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 14,
                SafeGUI.glow(
                        button(
                                Material.WRITABLE_BOOK,
                                "§b✦ Projets",
                                "§7Participe à l'urbanisme.",
                                "",
                                "§8• §7Déposer",
                                "§8• §7Voter",
                                "§8• §7Classement",
                                "",
                                "§e▶ Ouvrir"
                        )
                )
        );

        SafeGUI.safeSet(inv, 16,
                button(
                        Material.COMPASS,
                        "§b✦ Téléportation",
                        "§7Voyage rapidement.",
                        "",
                        "§8• §7Spawn",
                        "§8• §7Ville",
                        "§8• §7Exploration",
                        "",
                        "§e▶ Voyager"
                )
        );

        SafeGUI.safeSet(inv, 21,
                button(
                        Material.MAP,
                        "§a✦ Ville",
                        "§7Gère ton territoire.",
                        "",
                        "§8• §7Claims",
                        "§8• §7Town",
                        "§8• §7Nation",
                        "",
                        "§e▶ Gérer"
                )
        );

        SafeGUI.safeSet(inv, 23,
                button(
                        Material.EXPERIENCE_BOTTLE,
                        "§d✦ Métiers",
                        "§7Progresse et gagne plus.",
                        "",
                        "§8• §7Niveaux",
                        "§8• §7Récompenses",
                        "§8• §7Spécialisations",
                        "",
                        "§e▶ Ouvrir"
                )
        );

        SafeGUI.safeSet(inv, 31,
                button(
                        Material.BARRIER,
                        "§c✦ Fermer",
                        "§7Quitter le menu.",
                        "",
                        "§c▶ Fermer"
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