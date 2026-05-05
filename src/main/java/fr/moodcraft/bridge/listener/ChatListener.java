package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.manager.ReputationManager;
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

        // ✅ récup réputation
        int rep = ReputationManager.get(p.getUniqueId().toString());

        // ✅ récup rang
        String rank = ReputationManager.getRank(rep);

        // 🧠 affichage stylé
        e.setFormat("§7[" + rank + "§7] §f" + player + " §8» §f" + message);
    }
}