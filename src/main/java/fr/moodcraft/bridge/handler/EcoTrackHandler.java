package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.EcoTrackGUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EcoTrackHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {
        UUID target = EcoTrackGUI.getTarget(p);
        if (target == null) {
            p.closeInventory();
            return;
        }

        String filter = EcoTrackGUI.getFilter(p);
        int page = EcoTrackGUI.getPage(p);

        switch (slot) {
            case 45 -> reopen(p, target, null, 1);
            case 46 -> reopen(p, target, "BANK", 1);
            case 47 -> reopen(p, target, "TRANSFER", 1);
            case 48 -> reopen(p, target, "MARKET_BUY", 1);
            case 49 -> reopen(p, target, "MARKET_SELL", 1);
            case 50 -> reopen(p, target, "ESSENTIALS", 1);
            case 51 -> reopen(p, target, filter, Math.max(1, page - 1));
            case 52 -> reopen(p, target, filter, page + 1);
            case 53 -> {
                EcoTrackGUI.clear(p);
                p.closeInventory();
                click(p, Sound.BLOCK_CHEST_CLOSE, 0.9f);
            }
            default -> click(p, Sound.ITEM_BOOK_PAGE_TURN, 1.25f);
        }
    }

    private void reopen(Player p, UUID target, String filter, int page) {
        click(p, Sound.UI_BUTTON_CLICK, 1.1f);
        EcoTrackGUI.open(p, target, filter, page);
    }

    private void click(Player p, Sound sound, float pitch) {
        p.playSound(p.getLocation(), sound, 0.75f, pitch);
    }
}
