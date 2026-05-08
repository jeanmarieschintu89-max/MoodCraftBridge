package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.manager.TransactionManager;

import fr.moodcraft.bridge.gui.MainMenuGUI;

import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;

import fr.moodcraft.bridge.util.ActionLock;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Material;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class PriceHandler implements GUIHandler {

    private static final double TAX_RATE = 0.20;

    @Override
    public void onClick(Player p,
                        int slot) {

        switch (slot) {

            //
            // 🔙 RETOUR
            //

            case 31 -> {

                p.closeInventory();

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.9f
                );

                MainMenuGUI.open(p);

                return;
            }

            //
            // 💎 LIGNE 1
            //

            case 10 -> sell(
                    p,
                    "netherite",
                    Material.NETHERITE_INGOT,
                    "Netherite"
            );

            case 11 -> sell(
                    p,
                    "emerald",
                    Material.EMERALD,
                    "Émeraude"
            );

            case 12 -> sell(
                    p,
                    "diamond",
                    Material.DIAMOND,
                    "Diamant"
            );

            case 13 -> sell(
                    p,
                    "gold",
                    Material.GOLD_INGOT,
                    "Or"
            );

            case 14 -> sell(
                    p,
                    "copper",
                    Material.COPPER_INGOT,
                    "Cuivre"
            );

            case 15 -> sell(
                    p,
                    "iron",
                    Material.IRON_INGOT,
                    "Fer"
            );

            case 16 -> sell(
                    p,
                    "glowstone",
                    Material.GLOWSTONE_DUST,
                    "Glowstone"
            );

            //
            // 💎 LIGNE 2
            //

            case 20 -> sell(
                    p,
                    "quartz",
                    Material.QUARTZ,
                    "Quartz"
            );

            case 21 -> sell(
                    p,
                    "amethyst",
                    Material.AMETHYST_SHARD,
                    "Améthyste"
            );

            case 22 -> sell(
                    p,
                    "redstone",
                    Material.REDSTONE,
                    "Redstone"
            );

            case 23 -> sell(
                    p,
                    "lapis",
                    Material.LAPIS_LAZULI,
                    "Lapis"
            );

            case 24 -> sell(
                    p,
                    "coal",
                    Material.COAL,
                    "Charbon"
            );
        }
    }

    //
    // 💸 VENTE
    //

    private void sell(Player p,
                      String id,
                      Material mat,
                      String display) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        //
        // 📦 COUNT
        //

        int amount =
                count(p, mat);

        //
        // ❌ AUCUN ITEM
        //

        if (amount <= 0) {

            p.sendMessage("");

            p.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            p.sendMessage(
                    "§c✦ Marché MoodCraft"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Aucune ressource détectée:"
            );

            p.sendMessage(
                    "§e" + display
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

            return;
        }

        //
        // 💰 PRIX
        //

        double unit =
                MarketEngine.getPrice(id);

        double gross =
                unit * amount;

        //
        // 🏛 TAXE
        //

        double tax =
                gross * TAX_RATE;

        double total =
                gross - tax;

        //
        // 📉 TENDANCE
        //

        String trend =
                MarketState.trend.getOrDefault(
                        id,
                        "§7▬ Stable"
                );

        //
        // 💰 ARGENT
        //

        VaultHook.getEconomy()
                .depositPlayer(
                        p,
                        total
                );

        //
        // 📦 REMOVE
        //

        remove(
                p,
                mat,
                amount
        );

        //
        // 📊 IMPACT ÉCO
        //

        MarketEngine.recordSell(
                id,
                amount
        );

        //
        // 📜 HISTORIQUE
        //

        TransactionManager.marketSell(

                p.getUniqueId(),

                id,

                total,

                amount
        );

        //
        // ✨ MESSAGE
        //

        p.sendMessage("");

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage(
                "§6✦ Transaction Marché"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Ressource vendue:"
        );

        p.sendMessage(
                "§e"
                        + amount
                        + "x "
                        + display
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Cours actuel:"
        );

        p.sendMessage(
                "§6"
                        + SafeGUI.money(unit)
                        + "€ §8/unité"
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Tendance du marché:"
        );

        p.sendMessage(
                trend
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Valeur brute:"
        );

        p.sendMessage(
                "§f"
                        + SafeGUI.money(gross)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§cTaxe économique:"
        );

        p.sendMessage(
                "§c-"
                        + SafeGUI.money(tax)
                        + "€"
        );

        p.sendMessage("");

        p.sendMessage(
                "§aProfit net:"
        );

        p.sendMessage(
                "§a+"
                        + SafeGUI.money(total)
                        + "€"
        );

        p.sendMessage("");

        //
        // 🌟 GROS PROFITS
        //

        if (total >= 50000) {

            p.sendMessage(
                    "§6✦ Vente majeure détectée"
            );

            p.sendMessage("");

            p.sendMessage(
                    "§7Les échanges commerciaux"
            );

            p.sendMessage(
                    "§7du marché MoodCraft"
            );

            p.sendMessage(
                    "§7connaissent une forte activité."
            );

            p.sendMessage("");
        }

        p.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        p.sendMessage("");

        //
        // 🔊 SOUND
        //

        if (total >= 50000) {

            p.playSound(

                    p.getLocation(),

                    Sound.UI_TOAST_CHALLENGE_COMPLETE,

                    1f,

                    1f
            );
        }

        else {

            p.playSound(

                    p.getLocation(),

                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,

                    1f,

                    1.15f
            );
        }

        //
        // 🎬 TITLE
        //

        p.sendTitle(

                "§a+"
                        + SafeGUI.money(total)
                        + "€",

                "§fMarché MoodCraft",

                5,

                35,

                10
        );
    }

    //
    // 📦 COUNT
    //

    private int count(Player p,
                      Material mat) {

        int total = 0;

        for (var item :
                p.getInventory().getContents()) {

            if (item != null
                    && item.getType() == mat) {

                total += item.getAmount();
            }
        }

        return total;
    }

    //
    // 🗑 REMOVE
    //

    private void remove(Player p,
                        Material mat,
                        int amount) {

        int left = amount;

        for (var item :
                p.getInventory().getContents()) {

            if (item == null
                    || item.getType() != mat)
                continue;

            int take = Math.min(
                    item.getAmount(),
                    left
            );

            item.setAmount(
                    item.getAmount() - take
            );

            left -= take;

            if (left <= 0)
                break;
        }
    }
}