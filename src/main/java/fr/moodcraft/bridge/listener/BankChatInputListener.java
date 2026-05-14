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

        header(p);

        p.sendMessage(info("Écris le montant à déposer."));
        p.sendMessage("");
        p.sendMessage(detail("Exemple : §e5000"));
        p.sendMessage(detail("Liquide §8➜ §6Banque"));
        p.sendMessage("");
        p.sendMessage(detail("Tape §cannuler §7pour quitter."));

        footer(p);

        soundClick(p);
    }

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

        header(p);

        p.sendMessage(info("Écris le montant à retirer."));
        p.sendMessage("");
        p.sendMessage(detail("Exemple : §e5000"));
        p.sendMessage(detail("Banque §8➜ §aLiquide"));
        p.sendMessage("");
        p.sendMessage(detail("Tape §cannuler §7pour quitter."));

        footer(p);

        soundClick(p);
    }

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

        header(p);

        p.sendMessage(info("Écris le montant du virement."));
        p.sendMessage("");
        p.sendMessage(detail("Destinataire : §e" + targetName));
        p.sendMessage(detail("Exemple : §e5000"));
        p.sendMessage(detail("Paiement pro : §e/contrat"));
        p.sendMessage("");
        p.sendMessage(detail("Tape §cannuler §7pour quitter."));

        footer(p);

        soundClick(p);
    }

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

            header(p);

            p.sendMessage(info("Opération bancaire annulée."));

            footer(p);

            fail(p);

            return;
        }

        double amount =
                parseAmount(message);

        if (amount <= 0) {

            header(p);

            p.sendMessage(errorLine("Montant invalide."));
            p.sendMessage("");
            p.sendMessage(detail("Écris un nombre supérieur à zéro."));
            p.sendMessage(detail("Exemple : §e5000"));

            footer(p);

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
                    "Fonds liquides insuffisants."
            );

            header(p);

            p.sendMessage(detail("Liquide disponible : §a" + SafeGUI.money(cash) + "€"));
            p.sendMessage(detail("Montant demandé : §e" + SafeGUI.money(amount) + "€"));

            footer(p);

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

        header(p);

        p.sendMessage(successLine("Dépôt effectué."));
        p.sendMessage("");
        p.sendMessage(detail("Montant : §a+" + SafeGUI.money(amount) + "€"));
        p.sendMessage(detail("Banque : §6" + SafeGUI.money(newBank) + "€"));

        footer(p);

        success(
                p,
                "§a+" + SafeGUI.money(amount) + "€",
                "§fDépôt effectué"
        );

        BankGUI.open(p);
    }

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
                    "Fonds bancaires insuffisants."
            );

            header(p);

            p.sendMessage(detail("Banque disponible : §6" + SafeGUI.money(bank) + "€"));
            p.sendMessage(detail("Montant demandé : §e" + SafeGUI.money(amount) + "€"));

            footer(p);

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

        header(p);

        p.sendMessage(successLine("Retrait effectué."));
        p.sendMessage("");
        p.sendMessage(detail("Montant : §a+" + SafeGUI.money(amount) + "€"));
        p.sendMessage(detail("Banque : §6" + SafeGUI.money(newBank) + "€"));

        footer(p);

        success(
                p,
                "§a+" + SafeGUI.money(amount) + "€",
                "§fRetrait effectué"
        );

        BankGUI.open(p);
    }

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

        header(p);

        p.sendMessage(successLine("Montant enregistré."));
        p.sendMessage("§8• §7Destinataire : §e" + target.getName());
        p.sendMessage("§8• §7Montant : §e" + SafeGUI.money(amount) + "€");
        p.sendMessage("§e➜ §fOuverture du menu de confirmation.");
        p.sendMessage("§8• §7Aucun argent n'a encore été envoyé.");

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                0.75f,
                1.25f
        );

        Bukkit.getScheduler().runTaskLater(
                fr.moodcraft.bridge.Main.getInstance(),
                () -> {

                    if (!p.isOnline()) {
                        return;
                    }

                    TransferConfirmGUI.open(p);
                },
                2L
        );
    }

    private void error(
            Player p,
            String msg
    ) {

        header(p);

        p.sendMessage(errorLine("Transaction refusée."));
        p.sendMessage("");
        p.sendMessage(detail(msg));

        footer(p);

        fail(p);
    }

    private static void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque ✦ §8-----");
        p.sendMessage("");
    }

    private static void footer(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8-----------------------------");
        p.sendMessage("");
    }

    private static String info(String text) {
        return "§e➜ §f" + text;
    }

    private static String detail(String text) {
        return "§8• §7" + text;
    }

    private static String successLine(String text) {
        return "§a✔ §f" + text;
    }

    private static String errorLine(String text) {
        return "§c✖ §f" + text;
    }

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
