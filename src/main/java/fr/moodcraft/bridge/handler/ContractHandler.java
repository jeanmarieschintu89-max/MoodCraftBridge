package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.contract.Contract;

fr.moodcraft.bridge.gui.CreateContractGUI
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
            // ➕ CREATE
            //

            case 31 -> {

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

            case 35 -> {

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