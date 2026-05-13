package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.PriceGUI;
import fr.moodcraft.bridge.gui.ProfileGUI;
import fr.moodcraft.bridge.gui.TeleportGUI;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class MainMenuHandler implements GUIHandler {

    @Override
    public void onClick(
            Player p,
            int slot
    ) {

        if (ActionLock.isLocked(
                p.getUniqueId(),
                250
        )) return;

        switch (slot) {

            //
            // 👤 PROFIL
            //

            case 4 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.25f,
                        Sound.UI_BUTTON_CLICK,
                        1.6f
                );

                openNext(
                        p,
                        () -> ProfileGUI.open(
                                p,
                                p.getUniqueId()
                        )
                );
            }

            //
            // 🏦 BANQUE
            //

            case 10 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.2f,
                        Sound.BLOCK_ENDER_CHEST_OPEN,
                        1.4f
                );

                quick(
                        p,
                        "Banque",
                        "Ouverture du compte..."
                );

                openNext(
                        p,
                        () -> BankGUI.open(p)
                );
            }

            //
            // 📊 MARCHÉ
            //

            case 12 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        1.35f,
                        Sound.BLOCK_BEACON_AMBIENT,
                        1.2f
                );

                quick(
                        p,
                        "Marché",
                        "Chargement des prix..."
                );

                openNext(
                        p,
                        () -> PriceGUI.open(p)
                );
            }

            //
            // 🏢 BUREAU DES ENTREPRISES
            // Ancien accès Projets retiré du /menu.
            // Les projets urbains restent uniquement dans MoodTownMenu.
            //

            case 14 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_BEACON_ACTIVATE,
                        1.15f,
                        Sound.ITEM_BOOK_PAGE_TURN,
                        1.2f
                );

                if (!Bukkit.getPluginManager()
                        .isPluginEnabled("MoodBusiness")) {

                    p.closeInventory();

                    p.sendMessage("");
                    p.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
                    p.sendMessage("");
                    p.sendMessage("§c✘ §fModule indisponible.");
                    p.sendMessage("");
                    p.sendMessage("§7Le service économique");
                    p.sendMessage("§7n'est pas chargé.");
                    p.sendMessage("");
                    p.sendMessage("§8• §7Contactez le staff");
                    p.sendMessage("§8• §7ou réessayez plus tard");
                    p.sendMessage("");
                    p.sendMessage("§8-----------------------------");
                    p.sendMessage("");

                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_VILLAGER_NO,
                            1f,
                            0.85f
                    );

                    return;
                }

                quick(
                        p,
                        "Bureau des Entreprises",
                        "Ouverture..."
                );

                openNext(
                        p,
                        () -> p.performCommand("entreprise")
                );
            }

            //
            // 🧭 TÉLÉPORTATION
            //

            case 16 -> {

                premiumClick(
                        p,
                        Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                        1.25f,
                        Sound.BLOCK_PORTAL_AMBIENT,
                        1.6f
                );

                quick(
                        p,
                        "Téléportation",
                        "Choisis ta destination."
                );

                openNext(
                        p,
                        () -> TeleportGUI.open(p)
                );
            }

            //
            // 🏘 VILLE
            //

            case 21 -> {

                premiumClick(
                        p,
                        Sound.BLOCK_CHAIN_PLACE,
                        1.1f,
                        Sound.BLOCK_STONE_BUTTON_CLICK_ON,
                        1.5f
                );

                quick(
                        p,
                        "Ville",
                        "Ouverture du menu ville..."
                );

                openNext(
                        p,
                        () -> p.performCommand("townmenu")
                );
            }

            //
            // 🧰 MÉTIERS
            //

            case 23 -> {

                premiumClick(
                        p,
                        Sound.ENTITY_VILLAGER_WORK_TOOLSMITH,
                        1.05f,
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        1.4f
                );

                quick(
                        p,
                        "Métiers",
                        "Chargement..."
                );

                openNext(
                        p,
                        () -> p.performCommand("jobs join")
                );
            }

            //
            // ❌ FERMER
            //

            case 31 -> {

                premiumClick(
                        p,
                        Sound.UI_BUTTON_CLICK,
                        0.75f,
                        Sound.BLOCK_CHEST_CLOSE,
                        1.2f
                );

                p.closeInventory();

                p.sendMessage(
                        "§8✦ §7Menu fermé."
                );
            }
        }
    }

    //
    // 💬 MESSAGE COURT
    //

    private void quick(
            Player p,
            String module,
            String message
    ) {

        p.sendMessage(
                "§8✦ §6"
                        + module
                        + " §8• §7"
                        + message
        );
    }

    //
    // 🔊 SOUND
    //

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

    //
    // ⏳ OPEN NEXT TICK
    //

    private void openNext(
            Player p,
            Runnable action
    ) {

        p.closeInventory();

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                action,
                1L
        );
    }
}