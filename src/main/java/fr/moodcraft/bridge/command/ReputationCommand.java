package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.ReputationManager;
import fr.moodcraft.bridge.manager.ReputationHistoryManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReputationCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 8;

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        //
        // 🏆 ALIAS /toprep
        //

        if (label.equalsIgnoreCase("toprep")) {

            args =
                    new String[]{"classement"};
        }

        //
        // 👤 /rep
        //

        if (args.length == 0) {

            if (!(sender instanceof Player p)) {
                return true;
            }

            int rep =
                    ReputationManager.get(
                            p.getUniqueId().toString()
                    );

            header(
                    p,
                    "Réputation"
            );

            p.sendMessage("§fVotre réputation sur §aMood§6Craft§f.");
            p.sendMessage("");
            p.sendMessage("§7Points: §e" + rep);
            p.sendMessage("§7Rang: " + ReputationManager.getRank(rep));
            p.sendMessage("");
            p.sendMessage("§8• §7Votre réputation montre");
            p.sendMessage("§8• §7votre place dans la communauté.");

            footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.1f
            );

            return true;
        }

        //
        // 📜 /rep historique
        //

        if (args[0].equalsIgnoreCase("historique")) {

            if (!(sender instanceof Player p)) {
                return true;
            }

            int page =
                    1;

            if (args.length >= 2
                    && isNumber(args[1])) {

                page =
                        Integer.parseInt(args[1]);
            }

            List<String> list =
                    ReputationHistoryManager.getPage(
                            p.getUniqueId(),
                            page,
                            PAGE_SIZE
                    );

            header(
                    p,
                    "Historique Réputation"
            );

            p.sendMessage("§7Page: §e" + page);
            p.sendMessage("");

            if (list.isEmpty()) {

                p.sendMessage("§7Aucune action enregistrée.");

            } else {

                list.forEach(
                        line -> p.sendMessage("§8• §7" + line)
                );
            }

            footer(p);

            return true;
        }

        //
        // 👤 /rep <joueur>
        //

        if (args.length == 1
                && !args[0].equalsIgnoreCase("classement")
                && !args[0].equalsIgnoreCase("admin")) {

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(args[0]);

            int rep =
                    ReputationManager.get(
                            target.getUniqueId().toString()
                    );

            header(
                    sender,
                    "Réputation Joueur"
            );

            sender.sendMessage("§7Joueur: §e" + safeName(target));
            sender.sendMessage("§7Points: §e" + rep);
            sender.sendMessage("§7Rang: " + ReputationManager.getRank(rep));
            sender.sendMessage("");
            sender.sendMessage("§8• §7La réputation aide à reconnaître");
            sender.sendMessage("§8• §7les joueurs actifs et fiables.");

            footer(sender);

            return true;
        }

        //
        // 🏆 /toprep
        //

        if (args[0].equalsIgnoreCase("classement")) {

            header(
                    sender,
                    "Classement Réputation"
            );

            Map<String, Integer> top =
                    ReputationManager.getTop(10);

            if (top.isEmpty()) {

                sender.sendMessage("§7Aucune réputation enregistrée.");

            } else {

                int i =
                        1;

                for (Map.Entry<String, Integer> entry :
                        top.entrySet()) {

                    String name =
                            getNameFromId(
                                    entry.getKey()
                            );

                    int rep =
                            entry.getValue();

                    String rank =
                            ReputationManager.getRank(rep);

                    sender.sendMessage(
                            "§e#"
                                    + i
                                    + " §f"
                                    + shortText(name, 14)
                                    + " §8» §e"
                                    + rep
                                    + " §6✦ "
                                    + rank
                    );

                    i++;
                }
            }

            footer(sender);

            return true;
        }

        //
        // 🔒 ADMIN CHECK
        //

        if (!args[0].equalsIgnoreCase("admin")) {

            error(
                    sender,
                    "Commande inconnue."
            );

            return true;
        }

        if (!sender.hasPermission("moodcraft.admin")) {

            header(
                    sender,
                    "Réputation"
            );

            sender.sendMessage("§c✘ §fAccès refusé.");
            sender.sendMessage("");
            sender.sendMessage("§7Cette commande est réservée");
            sender.sendMessage("§7à l'administration.");

            footer(sender);

            return true;
        }

        if (args.length < 2) {

            sendAdminHelp(sender);

            return true;
        }

        String action =
                args[1].toLowerCase();

        //
        // 📜 /rep admin historique <joueur>
        //

        if (action.equals("historique")) {

            if (args.length < 3) {

                usage(
                        sender,
                        "/reputation admin historique <joueur> [page]"
                );

                return true;
            }

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(args[2]);

            int page =
                    1;

            if (args.length >= 4
                    && isNumber(args[3])) {

                page =
                        Integer.parseInt(args[3]);
            }

            List<String> list =
                    ReputationHistoryManager.getPage(
                            target.getUniqueId(),
                            page,
                            PAGE_SIZE
                    );

            header(
                    sender,
                    "Historique Réputation"
            );

            sender.sendMessage("§7Joueur: §e" + safeName(target));
            sender.sendMessage("§7Page: §e" + page);
            sender.sendMessage("");

            if (list.isEmpty()) {

                sender.sendMessage("§7Aucune action enregistrée.");

            } else {

                list.forEach(
                        line -> sender.sendMessage("§8• §7" + line)
                );
            }

            footer(sender);

            return true;
        }

        //
        // ADMIN ACTIONS
        //

        if (args.length < 3) {

            usage(
                    sender,
                    "/reputation admin <action> <joueur> [valeur]"
            );

            return true;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[2]);

        String id =
                target.getUniqueId().toString();

        switch (action) {

            //
            // ➕ AJOUTER
            //

            case "ajouter" -> {

                int value =
                        parse(sender, args, 3);

                if (value <= 0) {
                    return true;
                }

                if (target.isOnline()) {

                    ReputationManager.addRepStyled(
                            target.getPlayer(),
                            value,
                            "Ajout administrateur"
                    );

                } else {

                    ReputationManager.add(
                            id,
                            value
                    );

                    ReputationHistoryManager.add(
                            target.getUniqueId(),
                            value,
                            "Ajout administrateur"
                    );
                }

                header(
                        sender,
                        "Réputation"
                );

                sender.sendMessage("§a✔ §fRéputation ajoutée.");
                sender.sendMessage("");
                sender.sendMessage("§7Joueur: §e" + safeName(target));
                sender.sendMessage("§7Points: §a+" + value);

                footer(sender);
            }

            //
            // ➖ RETIRER
            //

            case "retirer" -> {

                int value =
                        parse(sender, args, 3);

                if (value <= 0) {
                    return true;
                }

                if (target.isOnline()) {

                    ReputationManager.addRepStyled(
                            target.getPlayer(),
                            -value,
                            "Retrait administrateur"
                    );

                } else {

                    ReputationManager.add(
                            id,
                            -value
                    );

                    ReputationHistoryManager.add(
                            target.getUniqueId(),
                            -value,
                            "Retrait administrateur"
                    );
                }

                header(
                        sender,
                        "Réputation"
                );

                sender.sendMessage("§c✘ §fRéputation retirée.");
                sender.sendMessage("");
                sender.sendMessage("§7Joueur: §e" + safeName(target));
                sender.sendMessage("§7Points: §c-" + value);

                footer(sender);
            }

            //
            // ✏ DEFINIR
            //

            case "definir" -> {

                int value =
                        parse(sender, args, 3);

                if (value < -1000) {
                    value = -1000;
                }

                if (value > 1000) {
                    value = 1000;
                }

                ReputationManager.set(
                        id,
                        value
                );

                ReputationHistoryManager.add(
                        target.getUniqueId(),
                        value,
                        "Définition administrateur"
                );

                header(
                        sender,
                        "Réputation"
                );

                sender.sendMessage("§a✔ §fRéputation définie.");
                sender.sendMessage("");
                sender.sendMessage("§7Joueur: §e" + safeName(target));
                sender.sendMessage("§7Nouveau total: §e" + value);
                sender.sendMessage("§7Rang: " + ReputationManager.getRank(value));

                footer(sender);
            }

            //
            // 🔄 RESET
            //

            case "reset" -> {

                ReputationManager.reset(id);

                ReputationHistoryManager.add(
                        target.getUniqueId(),
                        0,
                        "Reset administrateur"
                );

                header(
                        sender,
                        "Réputation"
                );

                sender.sendMessage("§c✘ §fRéputation réinitialisée.");
                sender.sendMessage("");
                sender.sendMessage("§7Joueur: §e" + safeName(target));

                footer(sender);
            }

            //
            // ❌ UNKNOWN
            //

            default -> error(
                    sender,
                    "Action inconnue."
            );
        }

        return true;
    }

    //
    // 📘 HELP ADMIN
    //

    private void sendAdminHelp(
            CommandSender sender
    ) {

        header(
                sender,
                "Commande Réputation"
        );

        sender.sendMessage("§fActions disponibles.");
        sender.sendMessage("");
        sender.sendMessage("§8• §e/reputation admin ajouter <joueur> <points>");
        sender.sendMessage("§8• §e/reputation admin retirer <joueur> <points>");
        sender.sendMessage("§8• §e/reputation admin definir <joueur> <points>");
        sender.sendMessage("§8• §e/reputation admin reset <joueur>");
        sender.sendMessage("§8• §e/reputation admin historique <joueur>");

        footer(sender);
    }

    //
    // 🎨 HEADER
    //

    private void header(
            CommandSender sender,
            String title
    ) {

        sender.sendMessage("");
        sender.sendMessage(
                "§8----- §6✦ §aMood§6Craft §f"
                        + cleanTitle(title)
                        + " §6✦ §8-----"
        );
        sender.sendMessage("");
    }

    //
    // 🎨 FOOTER
    //

    private void footer(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    //
    // ❌ ERROR
    //

    private void error(
            CommandSender sender,
            String message
    ) {

        header(
                sender,
                "Réputation"
        );

        sender.sendMessage("§c✘ §fAction refusée.");
        sender.sendMessage("");
        sender.sendMessage("§7" + message);

        footer(sender);
    }

    //
    // 📘 USAGE
    //

    private void usage(
            CommandSender sender,
            String usage
    ) {

        header(
                sender,
                "Réputation"
        );

        sender.sendMessage("§fCommande incorrecte.");
        sender.sendMessage("");
        sender.sendMessage("§7Utilisation: §e" + usage);

        footer(sender);
    }

    //
    // 🔢 PARSE
    //

    private int parse(
            CommandSender sender,
            String[] args,
            int index
    ) {

        try {

            return Integer.parseInt(
                    args[index]
            );

        } catch (Exception e) {

            error(
                    sender,
                    "Valeur invalide."
            );

            return -1;
        }
    }

    //
    // 🔢 NUMBER
    //

    private boolean isNumber(
            String s
    ) {

        try {

            Integer.parseInt(s);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    //
    // 🧠 NAME FROM ID
    //

    private String getNameFromId(
            String id
    ) {

        try {

            UUID uuid =
                    UUID.fromString(id);

            return safeName(
                    Bukkit.getOfflinePlayer(uuid)
            );

        } catch (Exception e) {

            return "Inconnu";
        }
    }

    //
    // 🧠 SAFE NAME
    //

    private String safeName(
            OfflinePlayer p
    ) {

        return p.getName() != null
                ? p.getName()
                : "Inconnu";
    }

    //
    // ✂ SHORT TEXT
    //

    private String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(
                0,
                Math.max(1, max - 3)
        ) + "...";
    }

    //
    // 🧼 TITLE CLEANER
    //

    private String cleanTitle(
            String title
    ) {

        if (title == null || title.isBlank()) {
            return "Réputation";
        }

        return title
                .replace("§f", "")
                .replace("§6", "")
                .replace("§a", "")
                .replace("§c", "")
                .replace("✦", "")
                .trim();
    }
}