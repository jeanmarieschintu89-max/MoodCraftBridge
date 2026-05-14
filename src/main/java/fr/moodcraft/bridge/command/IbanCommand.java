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
                    "§c✖ §fCommande joueur uniquement."
            );

            return true;
        }

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

    private void sendIban(
            Player p,
            String name,
            String iban,
            boolean staffView
    ) {

        header(p);

        p.sendMessage(
                staffView
                        ? "§e➜ §fConsultation bancaire staff."
                        : "§e➜ §fTon identité bancaire."
        );

        p.sendMessage("");

        p.sendMessage(detail("Titulaire : §e" + name));
        p.sendMessage(detail("IBAN : §b" + iban));

        p.sendMessage("");

        p.sendMessage(detail("Utilisable pour les virements"));
        p.sendMessage(detail("À partager uniquement si nécessaire"));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                1f,
                1.2f
        );
    }

    private void sendUsage(
            Player p
    ) {

        header(p);

        p.sendMessage("§e➜ §fCommande bancaire.");
        p.sendMessage("");
        p.sendMessage(detail("/iban §7voir ton IBAN"));

        if (p.hasPermission("econ.admin")
                || p.hasPermission("moodcraft.admin")) {

            p.sendMessage(detail("/iban <joueur> §7voir l'IBAN d'un joueur"));
        }

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                1f,
                0.8f
        );
    }

    private void sendError(
            Player p,
            String title,
            String detail
    ) {

        header(p);

        p.sendMessage("§c✖ §f" + title);
        p.sendMessage("");
        p.sendMessage(detail(detail));

        footer(p);

        p.playSound(
                p.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                1f,
                0.85f
        );
    }

    private void header(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ §aMood§6Craft §fBanque ✦ §8-----");
        p.sendMessage("");
    }

    private void footer(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8-----------------------------");
        p.sendMessage("");
    }

    private String detail(String text) {
        return "§8• §7" + text;
    }
}
