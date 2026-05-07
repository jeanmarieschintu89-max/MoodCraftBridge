package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.gui.ContractGUI;

import fr.moodcraft.bridge.manager.ContractCreationManager;
import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.manager.InputManager;

import fr.moodcraft.bridge.util.ActionLock;

import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

public class CreateContractHandler implements GUIHandler {

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
            // ✅ CONTINUER
            //

            case 22 -> {

                ItemStack item =

                        p.getOpenInventory()
                                .getTopInventory()
                                .getItem(13);

                //
                // ❌ EMPTY
                //

                if (item == null
                        || item.getType() == Material.AIR) {

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage(
                            "§c✦ Aucun item détecté"
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§7Dépose un item"
                    );

                    p.sendMessage(
                            "§7dans le slot central."
                    );

                    p.sendMessage("");

                    p.sendMessage(
                            "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    p.sendMessage("");

                    p.playSound(

                            p.getLocation(),

                            Sound.ENTITY_VILLAGER_NO,

                            1f,

                            1f
                    );

                    return;
                }

                //
                // ❌ INVALID
                //

                Material mat =
                        item.getType();

                if (mat == Material.BARRIER
                        || mat == Material.BEDROCK
                        || mat == Material.COMMAND_BLOCK
                        || mat == Material.CHAIN_COMMAND_BLOCK
                        || mat == Material.REPEATING_COMMAND_BLOCK
                        || mat == Material.STRUCTURE_BLOCK
                        || mat == Material.STRUCTURE_VOID
                        || mat == Material.JIGSAW) {

                    p.sendMessage(
                            "§cItem interdit."
                    );

                    p.playSound(

                            p.getLocation(),

                            Sound.ENTITY_VILLAGER_NO,

                            1f,

                            1f
                    );

                    return;
                }

                //
                // 💾 SAVE ITEM
                //

                ContractCreationManager.setItem(

                        p.getUniqueId(),

                        mat
                );

                //
                // 💬 INPUT
                //

                InputManager.wait(
                        p,
                        "contract_amount"
                );

                //
                // ✨ FEEDBACK
                //

                p.closeInventory();

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage(
                        "§6✦ §fCréation de Contrat"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Item sélectionné:"
                );

                p.sendMessage(
                        "§e" + mat.name()
                );

                p.sendMessage("");

                p.sendMessage(
                        "§7Entre maintenant"
                );

                p.sendMessage(
                        "§7la quantité dans le chat."
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8Exemple: §e256"
                );

                p.sendMessage("");

                p.sendMessage(
                        "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                );

                p.sendMessage("");

                p.playSound(

                        p.getLocation(),

                        Sound.BLOCK_NOTE_BLOCK_PLING,

                        1f,

                        1.15f
                );
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

                ContractGUI.open(p);
            }
        }
    }
}