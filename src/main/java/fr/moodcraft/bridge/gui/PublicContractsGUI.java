package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.contract.Contract;

import fr.moodcraft.bridge.manager.ContractManager;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.ReputationManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class PublicContractsGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                54,

                "§8✦ §6Contrats Publics"
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
        // 📜 HEADER
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.glow(

                        SafeGUI.item(

                                Material.WRITABLE_BOOK,

                                "§6✦ Contrats Publics",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Liste des missions",

                                "§7économiques disponibles.",

                                "",

                                "§8• Livraison",

                                "§8• Paiement sécurisé",

                                "§8• Réputation",

                                "",

                                "§e▶ MoodCraft Network"
                        )
                )
        );

        //
        // 📦 CONTRATS
        //

        int slot = 19;

        for (Contract contract :
                ContractManager.getAll()) {

            //
            // 🔒 OPEN ONLY
            //

            if (contract.getStatus()
                    != Contract.Status.OPEN)
                continue;

            OfflinePlayer owner =
                    Bukkit.getOfflinePlayer(
                            contract.getOwner()
                    );

            String ownerName =
                    owner.getName() == null
                            ? "Inconnu"
                            : owner.getName();

            int rep =
                    ReputationManager.get(
                            contract.getOwner()
                                    .toString()
                    );

            SafeGUI.safeSet(

                    inv,

                    slot,

                    SafeGUI.item(

                            contract.getItem(),

                            "§e✦ Contrat #"
                                    + contract.getId(),

                            "§8━━━━━━━━━━━━━━━━",

                            "§7Objet: §f"
                                    + contract.getItem().name(),

                            "§7Quantité: §e"
                                    + contract.getAmount(),

                            "",

                            "§7Récompense: §a"
                                    + SafeGUI.money(
                                    contract.getReward()
                            )
                                    + "€",

                            "",

                            "§7Créateur: §f"
                                    + ownerName,

                            "§7Réputation: "
                                    + ReputationManager.getRank(rep),

                            "",

                            "§e▶ Accepter"
                    )
            );

            slot++;

            //
            // 📦 LIMITE GUI
            //

            if (slot >= 44)
                break;
        }

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 49,

                SafeGUI.item(

                        Material.BARRIER,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au réseau contrats.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "public_contracts",

                inv
        );
    }
}