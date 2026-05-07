package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.contract.Contract;

import fr.moodcraft.bridge.gui.ContractCreateGUI;
import fr.moodcraft.bridge.gui.ContractGUI;
import fr.moodcraft.bridge.gui.MainMenuGUI;

import fr.moodcraft.bridge.manager.ContractCompleteManager;
import fr.moodcraft.bridge.manager.ContractManager;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Bukkit;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

public class ContractHandler implements GUIHandler {

    @Override
    public void onClick(Player p,
                        int slot) {

        //
        // 🔒 ANTI SPAM
        //

        if (ActionLock.isLocked(
                p.getUniqueId(),
                250
        )) return;

        switch (slot) {

            //
            // 🌍 CONTRATS PUBLICS
            //

            case 11 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.BLOCK_NOTE_BLOCK_CHIME,

                        1f,

                        1.1f
                );

                p.sendMessage(
                        "§8✦ §7Chargement des contrats publics..."
                );

                ContractGUI.open(p);
            }

            //
            // 📦 MES CONTRATS
            //

            case 13 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.BLOCK_CHEST_OPEN,

                        1f,

                        1f
                );

                p.sendMessage(
                        "§8✦ §7Chargement de tes contrats..."
                );

                //
                // 🚧 FUTUR GUI
                //

                p.sendMessage(
                        "§cSystème bientôt disponible."
                );
            }

            //
            // ➕ CREATE
            //

            case 15 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,

                        1f,

                        1.15f
                );

                p.sendMessage(
                        "§8✦ §7Initialisation du contrat..."
                );

                ContractCreateGUI.open(p);
            }

            //
            // 🔙 RETOUR
            //

            case 31 -> {

                p.playSound(

                        p.getLocation(),

                        Sound.UI_BUTTON_CLICK,

                        1f,

                        0.8f
                );

                MainMenuGUI.open(p);
            }

            //
            // 📜 CONTRATS PUBLICS
            //

            default -> {

                Contract contract =
                        ContractManager.getBySlot(slot);

                if (contract == null) {
                    return;
                }

                ContractCompleteManager.complete(
                        p,
                        contract
                );

                Bukkit.getScheduler().runTaskLater(

                        Main.getInstance(),

                        () -> ContractGUI.open(p),

                        2L
                );
            }
        }
    }
}