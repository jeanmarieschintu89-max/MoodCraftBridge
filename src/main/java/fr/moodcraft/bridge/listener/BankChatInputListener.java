package fr.moodcraft.bridge.listener;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.bank.TransactionManager;

import fr.moodcraft.bridge.gui.BankGUI;
import fr.moodcraft.bridge.gui.TransferConfirmGUI;

import fr.moodcraft.bridge.manager.TransferBuilder;

import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankChatInputListener
        implements Listener {

    private static final Map<UUID, Draft> WAITING =
            new HashMap<>();

    public enum Type {

        DEPOSIT,
        WITHDRAW,
        TRANSFER_AMOUNT
    }

    //
    // 💰 DEPOT
    //

    public static void startDeposit(
            Player p
    ) {

        WAITING.put(
                p.getUniqueId(),
                new Draft(
                        Type.DEPOSIT,
                        null
                )
        );

        p.closeInventory();

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§fÉcris le montant à déposer.");
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple: §e5000");
        p.sendMessage("§8• §7L'argent sera retiré de ton liquide");
        p.sendMessage("§8• §7et ajouté à ta banque personnelle.");
        p.sendMessage("");
        p.sendMessage("§7Tape §cannuler §7pour quitter.");
        p.sendMessage("");

        soundClick(p);
    }

    //
    // 💸 RETRAIT
    //

    public static void startWithdraw(
            Player p
    ) {

        WAITING.put(
                p.getUniqueId(),
                new Draft(
                        Type.WITHDRAW,
                        null
                )
        );

        p.closeInventory();

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§fÉcris le montant à retirer.");
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple: §e5000");
        p.sendMessage("§8• §7L'argent sera retiré de ta banque");
        p.sendMessage("§8• §7et ajouté à ton liquide.");
        p.sendMessage("");
        p.sendMessage("§7Tape §cannuler §7pour quitter.");
        p.sendMessage("");

        soundClick(p);
    }

    //
    // 🔁 VIREMENT MONTANT
    //

    public static void startTransferAmount(
            Player p,
            UUID targetUUID
    ) {

        WAITING.put(
                p.getUniqueId(),
                new Draft(
                        Type.TRANSFER_AMOUNT,
                        targetUUID
                )
        );

        TransferBuilder.setAction(
                p,
                TransferBuilder.Action.PLAYER_TRANSFER
        );

        TransferBuilder.setTarget(
                p,
                targetUUID
        );

        Player target =
                targetUUID != null
                        ? Bukkit.getPlayer(targetUUID)
                        : null;

        String targetName =
                target != null
                        ? target.getName()
                        : "Inconnu";

        p.closeInventory();

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§fÉcris le montant du virement.");
        p.sendMessage("§7Destinataire: §e" + targetName);
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple: §e5000");
        p.sendMessage("§8• §7Les gros paiements professionnels");
        p.sendMessage("§8• §7doivent passer par §e/contrat§7.");
        p.sendMessage("");
        p.sendMessage("§7Tape §cannuler §7pour quitter.");
        p.sendMessage("");

        soundClick(p);
    }

    //
    // 💬 CHAT
    //

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player p =
                e.getPlayer();

        Draft draft =
                WAITING.get(
                        p.getUniqueId()
                );

        if (draft == null) {
            return;
        }

        e.setCancelled(true);

        String message =
                e.getMessage();

        Bukkit.getScheduler().runTask(
                fr.moodcraft.bridge.Main.getInstance(),
                () -> handle(
                        p,
                        draft,
                        message
                )
        );
    }

    //
    // 🧠 HANDLE
    //

    private void handle(
            Player p,
            Draft draft,
            String message
    ) {

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            WAITING.remove(
                    p.getUniqueId()
            );

            TransferBuilder.clear(p);

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
            p.sendMessage("§7Opération bancaire annulée.");
            p.sendMessage("");

            fail(p);

            return;
        }

        double amount =
                parseAmount(message);

        if (amount <= 0) {

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
            p.sendMessage("§cMontant invalide.");
            p.sendMessage("§7Écris un nombre supérieur à zéro.");
            p.sendMessage("§8Exemple: §e5000");
            p.sendMessage("");

            fail(p);

            return;
        }

        if (draft.type == Type.DEPOSIT) {

            deposit(
                    p,
                    amount
            );

            return;
        }

        if (draft.type == Type.WITHDRAW) {

            withdraw(
                    p,
                    amount
            );

            return;
        }

        if (draft.type == Type.TRANSFER_AMOUNT) {

            transferAmount(
                    p,
                    draft,
                    amount
            );
        }
    }

    //
    // 💰 DEPOT LOGIC
    //

    private void deposit(
            Player p,
            double amount
    ) {

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {

            error(
                    p,
                    "Erreur économie Vault."
            );

            return;
        }

        double cash =
                eco.getBalance(p);

        if (cash < amount) {

            error(
                    p,
                    "Fonds liquides insuffisants. Liquide: §a"
                            + SafeGUI.money(cash)
                            + "€"
            );

            return;
        }

        eco.withdrawPlayer(
                p,
                amount
        );

        String id =
                p.getUniqueId().toString();

        double newBank =
                BankStorage.get(id) + amount;

        BankStorage.set(
                id,
                newBank
        );

        TransactionManager.deposit(
                p.getUniqueId(),
                amount
        );

        WAITING.remove(
                p.getUniqueId()
        );

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§a✔ Dépôt effectué");
        p.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
        p.sendMessage("§7Banque: §6" + SafeGUI.money(newBank) + "€");
        p.sendMessage("");

        success(
                p,
                "§a+" + SafeGUI.money(amount) + "€",
                "§fDépôt effectué"
        );

        BankGUI.open(p);
    }

    //
    // 💸 RETRAIT LOGIC
    //

    private void withdraw(
            Player p,
            double amount
    ) {

        Economy eco =
                VaultHook.getEconomy();

        if (eco == null) {

            error(
                    p,
                    "Erreur économie Vault."
            );

            return;
        }

        String id =
                p.getUniqueId().toString();

        double bank =
                BankStorage.get(id);

        if (bank < amount) {

            error(
                    p,
                    "Fonds bancaires insuffisants. Banque: §6"
                            + SafeGUI.money(bank)
                            + "€"
            );

            return;
        }

        BankStorage.set(
                id,
                bank - amount
        );

        eco.depositPlayer(
                p,
                amount
        );

        TransactionManager.withdraw(
                p.getUniqueId(),
                amount
        );

        double newBank =
                BankStorage.get(id);

        WAITING.remove(
                p.getUniqueId()
        );

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§a✔ Retrait effectué");
        p.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
        p.sendMessage("§7Banque: §6" + SafeGUI.money(newBank) + "€");
        p.sendMessage("");

        success(
                p,
                "§a+" + SafeGUI.money(amount) + "€",
                "§fRetrait effectué"
        );

        BankGUI.open(p);
    }

    //
    // 🔁 VIREMENT MONTANT LOGIC
    //

    private void transferAmount(
            Player p,
            Draft draft,
            double amount
    ) {

        if (draft.targetUUID == null) {

            error(
                    p,
                    "Aucun destinataire sélectionné."
            );

            TransferBuilder.clear(p);

            WAITING.remove(
                    p.getUniqueId()
            );

            return;
        }

        Player target =
                Bukkit.getPlayer(
                        draft.targetUUID
                );

        if (target == null || !target.isOnline()) {

            error(
                    p,
                    "Le joueur sélectionné n'est plus connecté."
            );

            TransferBuilder.clear(p);

            WAITING.remove(
                    p.getUniqueId()
            );

            return;
        }

        if (target.equals(p)) {

            error(
                    p,
                    "Tu ne peux pas t'envoyer un virement."
            );

            TransferBuilder.clear(p);

            WAITING.remove(
                    p.getUniqueId()
            );

            return;
        }

        TransferBuilder.setAction(
                p,
                TransferBuilder.Action.PLAYER_TRANSFER
        );

        TransferBuilder.setTarget(
                p,
                draft.targetUUID
        );

        TransferBuilder.setAmount(
                p,
                amount
        );

        WAITING.remove(
                p.getUniqueId()
        );

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§a✔ Montant enregistré");
        p.sendMessage("§7Destinataire: §e" + target.getName());
        p.sendMessage("§7Montant: §e" + SafeGUI.money(amount) + "€");
        p.sendMessage("");
        p.sendMessage("§8• §7Une confirmation est nécessaire.");
        p.sendMessage("");

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                0.75f,
                1.25f
        );

        TransferConfirmGUI.open(p);
    }

    //
    // ❌ ERROR
    //

    private void error(
            Player p,
            String msg
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Banque §aMood§6Craft §6✦ §8-----");
        p.sendMessage("§cTransaction refusée.");
        p.sendMessage("§7" + msg);
        p.sendMessage("");

        fail(p);
    }

    //
    // 🔢 PARSE
    //

    private double parseAmount(
            String text
    ) {

        try {

            return Double.parseDouble(
                    text.replace(",", ".")
            );

        } catch (Exception e) {

            return -1;
        }
    }

    //
    // 🔊 SOUNDS
    //

    private static void soundClick(
            Player p
    ) {

        p.playSound(
                p.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.75f,
                1.2f
        );

        p.playSound(
                p.getLocation(),
                Sound.ITEM_BOOK_PAGE_TURN,
                0.35f,
                1.1f
        );
    }

    private void success(
            Player p,
            String title,
            String subtitle
    ) {

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                0.75f,
                1.25f
        );

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                0.35f,
                1.4f
        );

        p.sendTitle(
                title,
                subtitle,
                5,
                35,
                10
        );
    }

    private void fail(
            Player p
    ) {

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    private static class Draft {

        private final Type type;
        private final UUID targetUUID;

        private Draft(
                Type type,
                UUID targetUUID
        ) {

            this.type =
                    type;

            this.targetUUID =
                    targetUUID;
        }
    }
}