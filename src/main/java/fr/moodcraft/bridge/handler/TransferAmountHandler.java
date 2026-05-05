package fr.moodcraft.bridge.handler;

import fr.moodcraft.bridge.Main;
import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransferConfirmGUI;
import fr.moodcraft.bridge.manager.TransferBuilder;
import fr.moodcraft.bridge.manager.AmountInputManager;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TransferAmountHandler implements GUIHandler {

    @Override
    public void onClick(Player p, int slot) {

        TransferBuilder data = TransferBuilder.get(p);

        // 🔙 RETOUR
        if (slot == 22) {
            p.closeInventory();
            BankGUI.open(p);
            return;
        }

        double amount = switch (slot) {
            case 10 -> 100;
            case 11 -> 1000;
            case 12 -> 10000;
            case 14 -> 50000;
            case 15 -> 100000;
            default -> 0;
        };

        // ✏️ MONTANT PERSONNALISÉ
        if (slot == 16) {
            p.closeInventory();

            p.setMetadata("input_active", new FixedMetadataValue(Main.getInstance(), true));
            AmountInputManager.wait(p, AmountInputManager.Type.PLAYER_TRANSFER);

            p.sendMessage("§eEntre le montant à envoyer dans le chat.");
            return;
        }

        if (amount <= 0) return;

        data.amount = amount;

        if (data.action == null) {
            p.sendMessage("§cErreur: action inconnue");
            return;
        }

        switch (data.action) {

            case DEPOSIT -> {
                BankStorage.add(p.getUniqueId().toString(), amount);
                p.sendMessage("§a✔ Déposé: §e" + (int) amount + "€");

                TransferBuilder.clear(p);
                p.closeInventory();
                BankGUI.open(p);
            }

            case WITHDRAW -> {

                double bank = BankStorage.get(p.getUniqueId().toString());

                if (bank < amount) {
                    p.sendMessage("§cFonds insuffisants");
                    return;
                }

                BankStorage.remove(p.getUniqueId().toString(), amount);

                p.sendMessage("§a✔ Retiré: §e" + (int) amount + "€");

                TransferBuilder.clear(p);
                p.closeInventory();
                BankGUI.open(p);
            }

            case PLAYER_TRANSFER, IBAN_TRANSFER -> {
                p.closeInventory();
                TransferConfirmGUI.open(p);
                // ❗ PAS DE CLEAR ICI
            }
        }
    }
}