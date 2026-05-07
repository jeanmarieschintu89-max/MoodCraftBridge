package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.bank.TransactionManager;

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
                    "§c✦ §fContrat impossible"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Ressources insuffisantes."
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Objet requis: §f"
                            + material.name()
            );

            p.sendMessage(
                    "§7Quantité: §e"
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

                    1f
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
        // 📜 HISTORY
        //

        TransactionManager.add(

                p.getUniqueId(),

                "[CONTRACT] +"
                        + SafeGUI.money(reward)
                        + "€"
        );

        //
        // ⭐ REP
        //

        ReputationManager.addRepStyled(

                p,

                3,

                "Contrat livré"
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
                "§6✦ §fContrat livré"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Objet: §f"
                        + material.name()
        );

        p.sendMessage(
                "§7Quantité: §e"
                        + amount
        );

        p.sendMessage(
                "§7Récompense: §a+"
                        + SafeGUI.money(reward)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§a✔ Livraison validée"
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

                1.1f
        );

        //
        // 🌍 BROADCAST
        //

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§6✦ §fContrat accompli par §e"
                        + p.getName()
        );

        Bukkit.broadcastMessage(
                "§7"
                        + amount
                        + "x "
                        + material.name()
                        + " §7livrés pour §a"
                        + SafeGUI.money(reward)
                        + "€"
        );

        Bukkit.broadcastMessage("");

        return true;
    }
}