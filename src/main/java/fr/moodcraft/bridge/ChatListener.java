package fr.moodcraft.bridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void chat(AsyncPlayerChatEvent e) {

        if (e.isCancelled()) return;

        var p = e.getPlayer();
        String player = p.getName();
        String message = e.getMessage();

        // ✅ PAS de scheduler
        String rep = ReputationManager.format(player);
        if (rep == null) rep = "§7?";

        e.setFormat("§7[" + rep + "] §f" + player + " §8» §f" + message);
    }
}