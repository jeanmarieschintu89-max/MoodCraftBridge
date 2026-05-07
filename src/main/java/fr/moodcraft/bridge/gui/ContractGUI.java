package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.contract.Contract;

import fr.moodcraft.bridge.manager.ContractManager;
import fr.moodcraft.bridge.manager.GUIManager;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class ContractGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(

                null,

                36,

                "§8✦ §6Contrats MoodCraft"
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

                                "§6✦ Réseau de Contrats",

                                "§8━━━━━━━━━━━━━━━━",

                                "§7Système économique",

                                "§7de missions entre joueurs.",

                                "",

                                "§8• Livraison",

                                "§8• Réputation",

                                "§8• Paiement sécurisé",

                                "",

                                "§e▶ MoodCraft Contracts"
                        )
                )
        );

        //
        // 🌍 CONTRATS PUBLICS
        //

        ContractManager.clearSlots();

        int[] slots = {

                10,11,12,13,14,15,16,

                19,20,21,22,23,24,25
        };

        int index = 0;

        for (Contract contract :

                ContractManager.getAll()) {

            if (index >= slots.length)
                break;

            int slot =
                    slots[index];

            ContractManager.setSlot(
                    slot,
                    contract
            );

            SafeGUI.safeSet(inv, slot,

                    SafeGUI.item(

                            contract.getMaterial(),

                            "§6✦ Contrat #"
                                    + contract.getId(),

                            "§8━━━━━━━━━━━━━━━━",

                            "§7Objet: §f"
                                    + contract.getMaterial().name(),

                            "§7Quantité: §e"
                                    + contract.getAmount(),

                            "",

                            "§7Récompense: §a"
                                    + SafeGUI.money(
                                            contract.getReward()
                                    )
                                    + "€",

                            "",

                            "§8• Paiement sécurisé",

                            "§8• Livraison instantanée",

                            "",

                            "§e▶ Cliquer pour livrer"
                    )
            );

            index++;
        }

        //
        // ➕ CREATE
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(

                        Material.EMERALD,

                        "§a✦ Créer un Contrat",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Créer une mission",

                        "§7et proposer une récompense.",

                        "",

                        "§8• Livraison ressources",

                        "§8• Paiement automatique",

                        "",

                        "§e▶ Créer"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 35,

                SafeGUI.item(

                        Material.BARRIER,

                        "§c✦ Retour",

                        "§8━━━━━━━━━━━━━━━━",

                        "§7Retour au menu principal.",

                        "",

                        "§e▶ Revenir"
                )
        );

        GUIManager.open(

                p,

                "contracts",

                inv
        );
    }
}