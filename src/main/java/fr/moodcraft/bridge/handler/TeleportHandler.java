package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.gui.MainMenuGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class TeleportHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                500
        )) return;

        switch (slot) {

            case 10 -> teleport(
                    p,
                    "spawn",
                    false,
                    "§e✦ Spawn",
                    "§7Retour au centre de §aMood§6Craft§7.",
                    Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,
                    1.1f,
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    1.35f
            );

            case 12 -> teleport(
                    p,
                    "warp shop",
                    false,
                    "§6✦ AdminShop",
                    "§7Ouverture de la boutique officielle.",
                    Sound.BLOCK_NOTE_BLOCK_BELL,
                    1.15f,
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    1.4f
            );

            case 14 -> teleport(
                    p,
                    "warp mini-jeux",
                    false,
                    "§d✦ Mini-jeux",
                    "§7Chargement des activités.",
                    Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM,
                    1.15f,
                    Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    1.35f
            );

            case 16 -> teleport(
                    p,
                    "tpr",
                    false,
                    "§5✦ Exploration",
                    "§7Recherche d'une zone sauvage.",
                    Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                    1.15f,
                    Sound.BLOCK_PORTAL_AMBIENT,
                    1.6f
            );

            case 22 -> teleport(
                    p,
                    "t spawn",
                    true,
                    "§a✦ Ville",
                    "§7Retour à ton territoire.",
                    Sound.BLOCK_CHAIN_PLACE,
                    1.1f,
                    Sound.BLOCK_STONE_BUTTON_CLICK_ON,
                    1.5f
            );

            case 31 -> {
                premiumClick(p, Sound.UI_BUTTON_CLICK, 0.8f, Sound.BLOCK_CHEST_CLOSE, 1.2f);
                MainMenuGUI.open(p);
            }
        }
    }

    private void teleport(
            Player p,
            String command,
            boolean autoConfirm,
            String title,
            String subtitle,
            Sound mainSound,
            float mainPitch,
            Sound secondSound,
            float secondPitch
    ) {

        premiumClick(p, mainSound, mainPitch, secondSound, secondPitch);

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Téléportation §aMood§6Craft ✦ §8-----");
        p.sendMessage("§e➜ §fPréparation du voyage.");
        p.sendMessage("§8• §7Destination : " + title);
        p.sendMessage("§8• " + subtitle);
        p.sendMessage("§8• §7Merci de ne pas bouger si une attente est appliquée.");

        if (autoConfirm) {
            p.sendMessage("§8• §7Confirmation automatique : §e/confirm");
        }

        p.sendMessage("§8-----------------------------");
        p.sendMessage("");

        p.sendTitle(
                title,
                "§fTéléportation en cours...",
                5,
                25,
                10
        );

        p.closeInventory();
        p.performCommand(command);

        if (autoConfirm) {
            Bukkit.getScheduler().runTaskLater(
                    Main.getInstance(),
                    () -> {
                        if (p.isOnline()) {
                            p.performCommand("confirm");
                        }
                    },
                    2L
            );
        }
    }

    private void premiumClick(
            Player p,
            Sound main,
            float mainPitch,
            Sound second,
            float secondPitch
    ) {

        p.playSound(p.getLocation(), main, 0.75f, mainPitch);
        p.playSound(p.getLocation(), second, 0.35f, secondPitch);
    }
}