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
                        GuiTitle.moodCraft()
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

                townName = town.getName();
                hasTown = true;

                Nation nation =
                        town.getNationOrNull();

                if (nation != null) {
                    nationName = nation.getName();
                    hasNation = true;
                }
            }
        }

        SafeGUI.fill(inv);

        inv.setItem(
                4,
                profileItem(
                        p,
                        bank,
                        cash,
                        total,
                        townName,
                        nationName,
                        hasTown,
                        hasNation
                )
        );

        inv.setItem(
                10,
                button(
                        Material.EMERALD,
                        "Banque",
                        List.of(
                                "§8• §7Gérer votre argent",
                                "§8• §7Solde banque : §e" + format(bank),
                                "",
                                "§e➜ §fOuvrir"
                        )
                )
        );

        inv.setItem(
                12,
                button(
                        Material.DIAMOND,
                        "Bourses",
                        List.of(
                                "§8• §7Prix dynamiques",
                                "§8• §7Marché des ressources",
                                "",
                                "§e➜ §fVoir les prix"
                        )
                )
        );

        inv.setItem(
                14,
                button(
                        Material.LECTERN,
                        "Entreprises",
                        List.of(
                                "§8• §7Bureau des Entreprises",
                                "§8• §7Demandes et contrats",
                                "",
                                "§e➜ §fOuvrir"
                        )
                )
        );

        inv.setItem(
                16,
                button(
                        Material.ENDER_PEARL,
                        "Téléportation",
                        List.of(
                                "§8• §7Destinations utiles",
                                "§8• §7Spawn, villes, monde",
                                "",
                                "§e➜ §fChoisir"
                        )
                )
        );

        inv.setItem(
                21,
                button(
                        Material.BELL,
                        "Ville",
                        List.of(
                                "§8• §7Menu ville",
                                "§8• §7Gestion Towny",
                                "",
                                "§e➜ §fOuvrir"
                        )
                )
        );

        inv.setItem(
                23,
                button(
                        Material.GOLDEN_PICKAXE,
                        "Métiers",
                        List.of(
                                "§8• §7Gagnez argent et XP",
                                "§8• §7Mineur, Bûcheron, Fermier, Chasseur",
                                "",
                                "§e➜ §fOuvrir"
                        )
                )
        );

        inv.setItem(
                31,
                button(
                        Material.BARRIER,
                        "Fermer",
                        List.of(
                                "§8• §7Retour au jeu",
                                "",
                                "§c✖ §fFermer"
                        )
                )
        );

        p.openInventory(inv);

        GUIManager.set(
                p,
                new fr.moodcraft.bridge.handler.MainMenuHandler()
        );
    }

    private static ItemStack profileItem(
            Player p,
            double bank,
            double cash,
            double total,
            String townName,
            String nationName,
            boolean hasTown,
            boolean hasNation
    ) {

        ItemStack item =
                getHeraldicItem(p, hasTown, townName, hasNation, nationName);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§6✦ §fProfil §6✦");

        List<String> lore = new ArrayList<>();
        lore.add("§8• §7Joueur : §e" + p.getName());
        lore.add("§8• §7Argent poche : §e" + format(cash));
        lore.add("§8• §7Banque : §e" + format(bank));
        lore.add("§8• §7Total : §a" + format(total));
        lore.add("");
        lore.add("§8• §7Ville : §b" + townName);
        lore.add("§8• §7Nation : §b" + nationName);
        lore.add("");
        lore.add(hasTown
                ? "§8• §7Blason municipal affiché si disponible"
                : "§8• §7Aucun blason municipal");
        lore.add("§e➜ §fVoir le profil");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getHeraldicItem(
            Player p,
            boolean hasTown,
            String townName,
            boolean hasNation,
            String nationName
    ) {

        if (hasTown) {
            ItemStack townShield = MoodTownFlagAPI.getTownShieldItem(townName);
            if (townShield != null) {
                return townShield.clone();
            }
        }

        if (hasNation) {
            ItemStack nationShield = MoodTownFlagAPI.getNationShieldItem(nationName);
            if (nationShield != null) {
                return nationShield.clone();
            }
        }

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(p);
            skull.setItemMeta(meta);
        }

        return skull;
    }

    private static ItemStack button(
            Material material,
            String name,
            List<String> lore
    ) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§6✦ §f" + name + " §6✦");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private static String format(double value) {
        return String.format("%,.0f€", value).replace(",", " ");
    }
}
