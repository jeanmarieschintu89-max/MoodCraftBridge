package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.bank.IbanManager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class IbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {

            sender.sendMessage(
                    "§cCommande joueur uniquement."
            );

            return true;
        }

        //
        // 👤 /iban
        //

        if (args.length == 0) {

            String iban =
                    IbanManager.get(
                            p.getUniqueId()
                    );

            if (iban == null) {

                sendError(
                        p,
                        "Aucun IBAN défini.",
                        "Ouvre la banque pour créer ton identité bancaire."
                );

                return true;
            }

            sendIban(
                    p,
                    p.getName(),
                    iban,
                    false
            );

            return true;
        }

        //
        // 👑 /iban <joueur>
        //

        if (args.length == 1) {

            if (!p.hasPermission("econ.admin")
                    && !p.hasPermission("moodcraft.admin")) {

                sendError(
                        p,
                        "Accès refusé.",
                        "Cette consultation est réservée au staff."
                );

                return true;
            }

            Player target =
                    Bukkit.getPlayerExact(
                            args[0]
                    );

            if (target == null) {

                sendError(
                        p,
                        "Joueur introuvable.",
                        "Le joueur doit être connecté."
                );

                return true;
            }

            String iban =
                    IbanManager.get(
                            target.getUniqueId()
                    );

            if (iban == null) {

                sendError(
                        p,
                        "Aucun IBAN trouvé.",
                        "Ce joueur n'a pas encore d'identité bancaire."
                );

                return true;
            }

            sendIban(
                    p,
                    target.getName(),
                    iban,
                    true
            );

            return true;
        }

        sendUsage(p);

        return true;
    }

    //
    // 🏦 IBAN DISPLAY
    //

    private void sendIban(
            Player p,
            String name,
            String iban,
            boolean staffView
    ) {

        header(p);

        p.sendMessage(
                staffView
                        ? "§fConsultation bancaire staff."
                        : "§fTon identité bancaire."
        );

        p.sendMessage("");

        p.sendMessage(
                "§7Titulaire: §e" + name
        );

        p.sendMessage(
                "§7IBAN: §b" + iban
        );

        p.sendMessage("");

        p.sendMessage(
                "§8• §7Utilisable pour les virements"
        );

        p.sendMessage(
                "§8• §7À partager uniquement si nécessaire"
        );

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                1f,
                1.2f
        );
    }

    //
    // 📘 USAGE
    //

    private void sendUsage(
            Player p
    ) {

        header(p);

        p.sendMessage("§fCommande bancaire.");
        p.sendMessage("");
        p.sendMessage("§7Utilisation:");
        p.sendMessage("§8• §e/iban §7voir ton IBAN");

        if (p.hasPermission("econ.admin")
                || p.hasPermission("moodcraft.admin")) {

            p.sendMessage("§8• §e/iban <joueur> §7voir l'IBAN d'un joueur");
        }

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                1f,
                0.8f
        );
    }

    //
    // ❌ ERROR
    //

    private void sendError(
            Player p,
            String title,
            String detail
    ) {

        header(p);

        p.sendMessage("§c✘ §f" + title);
        p.sendMessage("");
        p.sendMessage("§7" + detail);

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    //
    // 🎨 HEADER
    //

    private void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque §6✦ §8-----");
        p.sendMessage("");
    }

    //
    // 🎨 FOOTER
    //

    private void footer(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8-----------------------------");
        p.sendMessage("");
    }
}
