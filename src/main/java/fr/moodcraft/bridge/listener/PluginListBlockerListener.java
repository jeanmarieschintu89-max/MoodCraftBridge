package fr.moodcraft.bridge.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Set;

public class PluginListBlockerListener implements Listener {

    private static final String BYPASS_PERMISSION = "moodcraftbridge.plugins.bypass";

    private static final Set<String> BLOCKED_COMMANDS = Set.of(
            "plugins",
            "pl",
            "bukkit:plugins",
            "bukkit:pl",
            "?",
            "bukkit:?",
            "help",
            "bukkit:help",
            "ver",
            "version",
            "about",
            "bukkit:ver",
            "bukkit:version",
            "bukkit:about"
    );

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission(BYPASS_PERMISSION)
                || event.getPlayer().hasPermission("moodcraft.admin")) {
            return;
        }

        String message = event.getMessage();
        if (message == null || message.isBlank()) return;

        String command = message.substring(1).split(" ")[0].toLowerCase();

        if (!BLOCKED_COMMANDS.contains(command)) return;

        event.setCancelled(true);

        event.getPlayer().sendMessage("§8----- §6✦ §aMood§6Craft §fSécurité ✦ §8-----");
        event.getPlayer().sendMessage("§c✖ §fLa liste des plugins est privée.");
        event.getPlayer().sendMessage("§8• §7Utilise §e/menu §7pour accéder aux fonctions du serveur.");
        event.getPlayer().sendMessage("§8-----------------------------");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().hasPermission(BYPASS_PERMISSION)
                || event.getPlayer().hasPermission("moodcraft.admin")) {
            return;
        }

        event.getCommands().removeIf(command -> BLOCKED_COMMANDS.contains(command.toLowerCase()));
    }
}