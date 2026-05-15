package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.MainMenuGUI;
import fr.moodcraft.bridge.gui.PriceGUI;

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
    public void onClick(
            Player p,
            int slot
    ) {

        if (slot == PriceGUI.RETURN_SLOT) {

            p.closeInventory();

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    0.8f,
                    Sound.BLOCK_CHEST_CLOSE,
                    1.2f
            );

            MainMenuGUI.open(p);

            return;
        }

        PriceGUI.MarketItem item = PriceGUI.getMarketItemBySlot(slot);

        if (item == null) {
            return;
        }

        if (!PriceGUI.isKnown(item)) {
            header(p);
            p.sendMessage("§c✖ §fPrix indisponible.");
            p.sendMessage("§8• §7Ressource : §e" + item.displayName());
            p.sendMessage("§8• §7Le marché n'a pas encore chargé cette valeur.");
            footer(p);
            fail(p);
            return;
        }

        sell(p, item.id(), item.material(), item.displayName());
    }

    private void sell(
            Player p,
            String id,
            Material mat,
            String display
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        int amount = count(p, mat);

        if (amount <= 0) {

            header(p);

            p.sendMessage("§c✖ §fAucune ressource détectée.");
            p.sendMessage("§8• §7Objet demandé : §e" + display);
            p.sendMessage("§8• §7Mets-en dans ton inventaire puis réessaie.");

            footer(p);

            fail(p);

            return;
        }

        double unit = MarketEngine.getPrice(id);
        double gross = unit * amount;
        double tax = gross * TAX_RATE;
        double total = gross - tax;

        String trend = MarketState.trend.getOrDefault(id, "§7Stable");

        VaultHook.getEconomy().depositPlayer(p, total);

        remove(p, mat, amount);

        MarketEngine.recordSell(id, amount);

        TransactionManager.marketSell(
                p.getUniqueId(),
                id,
                total,
                amount
        );

        header(p);

        p.sendMessage("§a✔ §fVente effectuée.");
        p.sendMessage("§8• §7Objet vendu : §e" + amount + "x " + display);
        p.sendMessage("§8• §7Prix unité : §6" + SafeGUI.money(unit) + "€");
        p.sendMessage("§8• §7Tendance : " + cleanTrend(trend));
        p.sendMessage("§8• §7Brut : §e" + SafeGUI.money(gross) + "€");
        p.sendMessage("§8• §7Taxe marché : §c-" + SafeGUI.money(tax) + "€");
        p.sendMessage("§8• §7Gain net : §a+" + SafeGUI.money(total) + "€");

        if (total >= 50000) {
            p.sendMessage("§e➜ §fVente majeure détectée.");
        }

        footer(p);

        if (total >= 50000) {

            premiumClick(
                    p,
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    1f,
                    Sound.BLOCK_BEACON_ACTIVATE,
                    1.2f
            );

        } else {

            premiumClick(
                    p,
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    1.15f,
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    1.35f
            );
        }

        p.sendTitle(
                "§a+" + SafeGUI.money(total) + "€",
                "§fMarché §aMood§6Craft",
                5,
                35,
                10
        );
    }

    private void header(Player p) {
        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Marché §aMood§6Craft ✦ §8-----");
    }

    private void footer(Player p) {
        p.sendMessage("§8-----------------------------");
        p.sendMessage("");
    }

    private String cleanTrend(String trend) {

        if (trend == null || trend.isBlank()) {
            return "§7Stable";
        }

        return trend.replace("▬", "").trim();
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

            if (item == null || item.getType() != mat) {
                continue;
            }

            int take = Math.min(item.getAmount(), left);

            item.setAmount(item.getAmount() - take);

            left -= take;

            if (left <= 0) {
                break;
            }
        }
    }

    private void fail(Player p) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    private void premiumClick(
            Player p,
            Sound main,
            float mainPitch,
            Sound second,
            float secondPitch
    ) {

        p.playSound(
                p.getLocation(),
                main,
                0.75f,
                mainPitch
        );

        p.playSound(
                p.getLocation(),
                second,
                0.35f,
                secondPitch
        );
    }
}
