package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.contract.Contract;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

public class ContractCompleteManager {

    public static boolean complete(
            Player p,
            Contract contract
    ) {

        //
        // ❌ NULL
        //

        if (contract == null) {

            return false;
        }

        Material material =
                contract.getMaterial();

        int amount =
                contract.getAmount();

        double reward =
                contract.getReward();

        //
        // 📦 COUNT
        //

        int total = 0;

        for (ItemStack item :

                p.getInventory().getContents()) {

            if (item == null)
                continue;

            if (item.getType() != material)
                continue;

            total += item.getAmount();
        }

        //
        // ❌ NOT ENOUGH
        //

        if (total < amount) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ §fLivraison refusée"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Le réseau logistique MoodCraft"
            );

            p.sendMessage(
                    "§7détecte un stock insuffisant."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Ressource demandée: §f"
                            + material.name()
            );

            p.sendMessage(
                    "§7Quantité requise: §e"
                            + amount
            );

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage("");

            p.playSound(

                    p.getLocation(),

                    Sound.ENTITY_VILLAGER_NO,

                    1f,

                    0.9f
            );

            return false;
        }

        //
        // 📦 REMOVE ITEMS
        //

        int remaining =
                amount;

        for (ItemStack item :

                p.getInventory().getContents()) {

            if (item == null)
                continue;

            if (item.getType() != material)
                continue;

            int stack =
                    item.getAmount();

            if (stack <= remaining) {

                remaining -= stack;

                item.setAmount(0);

            } else {

                item.setAmount(
                        stack - remaining
                );

                remaining = 0;
            }

            if (remaining <= 0)
                break;
        }

        //
        // 💰 PAY
        //

        VaultHook.add(
                p,
                reward
        );

        //
        // ⭐ REP
        //

        ReputationManager.addRepStyled(

                p,

                3,

                "Livraison économique"
        );

        //
        // 🗑 REMOVE CONTRACT
        //

        ContractManager.remove(
                contract.getId()
        );

        //
        // ✨ SUCCESS
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ §fLivraison validée"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Le réseau économique MoodCraft"
        );

        p.sendMessage(
                "§7confirme la réception des ressources."
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Ressource livrée: §f"
                        + material.name()
        );

        p.sendMessage(
                "§7Quantité transférée: §e"
                        + amount
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Paiement reçu: §a+"
                        + SafeGUI.money(reward)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§a✔ Paiement transféré automatiquement"
        );

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        p.playSound(

                p.getLocation(),

                Sound.ENTITY_PLAYER_LEVELUP,

                1f,

                1.05f
        );

        //
        // 🌍 BROADCAST
        //

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§6✦ §fLe réseau contrats annonce :"
        );

        Bukkit.broadcastMessage(
                "§e"
                        + p.getName()

                        + " §7a livré §e"

                        + amount

                        + "x "

                        + material.name()

                        + " §7contre §a"

                        + SafeGUI.money(reward)

                        + "€"
        );

        Bukkit.broadcastMessage("");

        return true;
    }
}