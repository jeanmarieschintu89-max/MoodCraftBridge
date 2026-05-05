package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.IbanManager;
import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IbanPayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length < 2) {
            p.sendMessage("§cUsage: /ibanpay <iban> <montant>");
            return true;
        }

        String iban = args[0].replace(" ", "").toUpperCase();

        double amount;
        try {
            amount = Double.parseDouble(args[1].replace(",", "."));
        } catch (Exception e) {
            p.sendMessage("§cMontant invalide");
            return true;
        }

        if (amount <= 0) {
            p.sendMessage("§cMontant invalide");
            return true;
        }

        // 🔍 trouver destinataire
        UUID targetUUID = IbanManager.getOwner(iban);

        if (targetUUID == null) {
            p.sendMessage("§cIBAN introuvable");
            return true;
        }

        if (targetUUID.equals(p.getUniqueId())) {
            p.sendMessage("§cTu ne peux pas te payer toi-même");
            return true;
        }

        Player target = Bukkit.getPlayer(targetUUID);

        // 💰 vérif argent
        double balance = VaultHook.getBalance(p);

        if (balance < amount) {
            p.sendMessage("§cFonds insuffisants");
            return true;
        }

        // 💸 TRANSACTION
        VaultHook.remove(p, amount);

        if (target != null && target.isOnline()) {
            VaultHook.add(target, amount);
        } else {
            // si offline → à toi de voir (bankStorage, pending, etc.)
            // pour l'instant on crédite quand même via Vault (si offline supporté)
            VaultHook.add(targetUUID, amount); // ⚠️ adapte si ton VaultHook supporte UUID
        }

        // =========================
        // ✨ MESSAGES STYLÉS
        // =========================

        // 👤 envoyeur
        p.sendMessage("§8§m-----------------------------");
        p.sendMessage("§6✦ §fVirement effectué");
        p.sendMessage("§7Vers IBAN: §e" + iban);
        p.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
        p.sendMessage("§8§m-----------------------------");

        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.2f);

        // 🎯 receveur
        if (target != null && target.isOnline()) {
            target.sendMessage("§8§m-----------------------------");
            target.sendMessage("§a✦ §fVirement reçu");
            target.sendMessage("§7De: §e" + p.getName());
            target.sendMessage("§7Montant: §a+" + SafeGUI.money(amount) + "€");
            target.sendMessage("§8§m-----------------------------");

            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }

        // 📜 LOG console
        Bukkit.getConsoleSender().sendMessage("§b[IBAN] §f" + p.getName() +
                " -> " + iban + " : " + amount + "€");

        return true;
    }
}