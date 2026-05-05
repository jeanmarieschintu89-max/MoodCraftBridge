package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.MainMenuGUI;
import fr.moodcraft.bridge.handler.GUIHandler;
import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.util.VaultHook;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PriceHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        switch (slot) {

            case 4:
                MainMenuGUI.open(p);
                return;

            case 10: sell(p, "netherite", Material.NETHERITE_INGOT); return;
            case 11: sell(p, "emerald", Material.EMERALD); return;
            case 12: sell(p, "diamond", Material.DIAMOND); return;

            case 13: sell(p, "gold", Material.GOLD_INGOT); return;
            case 14: sell(p, "copper", Material.COPPER_INGOT); return;
            case 15: sell(p, "iron", Material.IRON_INGOT); return;

            case 16: sell(p, "glowstone", Material.GLOWSTONE_DUST); return;

            case 19: sell(p, "quartz", Material.QUARTZ); return;
            case 20: sell(p, "amethyst", Material.AMETHYST_SHARD); return;
            case 21: sell(p, "redstone", Material.REDSTONE); return;
            case 22: sell(p, "lapis", Material.LAPIS_LAZULI); return;
            case 23: sell(p, "coal", Material.COAL); return;
        }
    }

    private void sell(Player p, String id, Material mat) {

        int amount = count(p, mat);

        if (amount <= 0) {
            p.sendMessage("§cTu n'as aucun " + mat.name().toLowerCase());
            return;
        }

        double unit = MarketEngine.getPrice(id);
        double gross = unit * amount;

        double taxRate = 0.20;
        double tax = gross * taxRate;
        double total = gross - tax;

        VaultHook.getEconomy().depositPlayer(p, total);

        remove(p, mat, amount);

        p.sendMessage("§a✔ Vente: §f" + amount + "x " + id +
                "\n§7Brut: §f" + String.format("%.2f", gross) + "€" +
                "\n§cTaxe (20%): §f-" + String.format("%.2f", tax) + "€" +
                "\n§eNet: §f" + String.format("%.2f", total) + "€");

        p.sendTitle("§a+" + String.format("%.2f", total) + "€",
                "§cTaxe: -" + String.format("%.2f", tax) + "€",
                5, 20, 5);
    }

    private int count(Player p, Material mat) {
        int total = 0;

        for (var item : p.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                total += item.getAmount();
            }
        }

        return total;
    }

    private void remove(Player p, Material mat, int amount) {

        int left = amount;

        for (var item : p.getInventory().getContents()) {

            if (item == null || item.getType() != mat) continue;

            int take = Math.min(item.getAmount(), left);
            item.setAmount(item.getAmount() - take);

            left -= take;

            if (left <= 0) break;
        }
    }
}