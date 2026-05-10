package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.bank.BankStorage;

import fr.moodcraft.bridge.gui.TransferConfirmGUI;
import fr.moodcraft.bridge.gui.TargetPlayerGUI;

import fr.moodcraft.bridge.manager.AmountInputManager;
import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.metadata.FixedMetadataValue;

public class TransferAmountHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        if (slot == 31) {

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    0.8f,
                    Sound.BLOCK_CHEST_CLOSE,
                    1.2f
            );

            TargetPlayerGUI.open(p);

            return;
        }

        double amount = switch (slot) {

            case 10 -> 100;

            case 12 -> 1000;

            case 14 -> 10000;

            case 16 -> 50000;

            case 22 -> 100000;

            default -> 0;
        };

        if (slot == 23) {

            p.closeInventory();

            p.setMetadata(
                    "input_active",
                    new FixedMetadataValue(
                            Main.getInstance(),
                            true
                    )
            );

            AmountInputManager.wait(
                    p,
                    AmountInputManager.Type.PLAYER_TRANSFER
            );

            p.sendMessage("");
            p.sendMessage("§8----- §6Banque MoodCraft §8-----");
            p.sendMessage("§7Entre le montant à envoyer.");
            p.sendMessage("§8Exemple: §e25000");
            p.sendMessage("");

            premiumClick(
                    p,
                    Sound.UI_BUTTON_CLICK,
                    1.1f,
                    Sound.ITEM_BOOK_PAGE_TURN,
                    1.2f
            );

            return;
        }

        if (amount <= 0)
            return;

        TransferBuilder.setAmount(
                p,
                amount
        );

        TransferBuilder.Action action =
                TransferBuilder.getAction(p);

        if (action == null) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Banque MoodCraft §8-----");
            p.sendMessage("§cErreur bancaire.");
            p.sendMessage("§7Action inconnue.");
            p.sendMessage("");

            fail(p);

            return;
        }

        switch (action) {

            case PLAYER_TRANSFER,
                 IBAN_TRANSFER -> {

                double bank =
                        BankStorage.get(
                                p.getUniqueId().toString()
                        );

                if (bank < amount) {

                    p.sendMessage("");
                    p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                    p.sendMessage("§cFonds insuffisants.");
                    p.sendMessage("§7Banque: §6"
                            + SafeGUI.money(bank)
                            + "€");
                    p.sendMessage("");

                    fail(p);

                    return;
                }

                if (amount >= 50000) {

                    p.sendMessage("");
                    p.sendMessage("§8----- §6Banque MoodCraft §8-----");
                    p.sendMessage("§6⚠ Transfert important");
                    p.sendMessage("§7Montant: §e"
                            + SafeGUI.money(amount)
                            + "€");
                    p.sendMessage("§7Confirmation requise.");
                    p.sendMessage("");

                    premiumClick(
                            p,
                            Sound.BLOCK_BEACON_AMBIENT,
                            1.0f,
                            Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                            1.2f
                    );

                } else {

                    premiumClick(
                            p,
                            Sound.UI_BUTTON_CLICK,
                            1.25f,
                            Sound.BLOCK_NOTE_BLOCK_CHIME,
                            1.3f
                    );
                }

                p.closeInventory();

                TransferConfirmGUI.open(p);
            }

            default -> {

                p.sendMessage("§cAction bancaire invalide.");

                fail(p);
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