package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.ReputationManager;
import fr.moodcraft.bridge.manager.ReputationHistoryManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReputationCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 8;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // 🔥 Alias /toprep
        if (label.equalsIgnoreCase("toprep")) {
            args = new String[]{"classement"};
        }

        //
        // 👤 /reputation
        //

        if (args.length == 0) {

            if (!(sender instanceof Player p)) return true;

            int rep =
                    ReputationManager.get(
                            p.getUniqueId().toString()
                    );

            sendHeader(
                    p,
                    "§fVotre réputation"
            );

            p.sendMessage("§7Points: §e" + rep);
            p.sendMessage(
                    "§7Rang: "
                            + ReputationManager.getRank(rep)
            );

            sendFooter(p);

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.1f
            );

            return true;
        }

        //
        // 📜 HISTORIQUE
        //

        if (args[0].equalsIgnoreCase("historique")) {

            if (!(sender instanceof Player p)) return true;

            int page = 1;

            if (args.length >= 2
                    && isNumber(args[1])) {

                page = Integer.parseInt(args[1]);
            }

            List<String> list =
                    ReputationHistoryManager.getPage(
                            p.getUniqueId(),
                            page,
                            PAGE_SIZE
                    );

            sendHeader(
                    p,
                    "§fHistorique réputation §8(Page " + page + ")"
            );

            if (list.isEmpty()) {

                p.sendMessage("§7Aucune donnée.");

            } else {

                list.forEach(
                        l -> p.sendMessage(" " + l)
                );
            }

            sendFooter(p);

            return true;
        }

        //
        // 👤 /reputation <joueur>
        //

        if (args.length == 1 &&
                !args[0].equalsIgnoreCase("classement") &&
                !args[0].equalsIgnoreCase("admin")) {

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(args[0]);

            int rep =
                    ReputationManager.get(
                            target.getUniqueId().toString()
                    );

            sendHeader(
                    sender,
                    "§fRéputation de §e" + safeName(target)
            );

            sender.sendMessage("§7Points: §e" + rep);

            sender.sendMessage(
                    "§7Rang: "
                            + ReputationManager.getRank(rep)
            );

            sendFooter(sender);

            return true;
        }

        //
        // 🏆 CLASSEMENT
        //

        if (args[0].equalsIgnoreCase("classement")) {

            sendHeader(
                    sender,
                    "§fClassement réputation"
            );

            int i = 1;

            Map<String, Integer> top =
                    ReputationManager.getTop(10);

            if (top.isEmpty()) {

                sender.sendMessage("§7Aucune réputation enregistrée.");

            } else {

                for (Map.Entry<String, Integer> entry :
                        top.entrySet()) {

                    String name =
                            getNameFromId(entry.getKey());

                    int rep =
                            entry.getValue();

                    String rank =
                            ReputationManager.getRank(rep);

                    sender.sendMessage(
                            "§e#"
                                    + i
                                    + " §f"
                                    + name
                                    + " §8» §e"
                                    + rep
                                    + " §6✦ §7Rang: "
                                    + rank
                    );

                    i++;
                }
            }

            sendFooter(sender);

            return true;
        }

        //
        // 🔒 ADMIN
        //

        if (!args[0].equalsIgnoreCase("admin")) {

            sender.sendMessage("§cSous-commande inconnue.");

            return true;
        }

        if (!sender.hasPermission("moodcraft.admin")) {

            sendHeader(
                    sender,
                    "§cPermission refusée"
            );

            sender.sendMessage("§7Tu n'as pas accès à cette commande.");

            sendFooter(sender);

            return true;
        }

        if (args.length < 2) {

            sendHeader(
                    sender,
                    "§fCommande réputation"
            );

            sender.sendMessage("§7Usage:");
            sender.sendMessage(
                    "§e/reputation admin <ajouter|retirer|definir|reset|historique> <joueur> [valeur/page]"
            );

            sendFooter(sender);

            return true;
        }

        String action =
                args[1].toLowerCase();

        //
        // 📜 HISTO ADMIN
        //

        if (action.equals("historique")) {

            if (args.length < 3) {

                sender.sendMessage(
                        "§cUsage: /reputation admin historique <joueur> [page]"
                );

                return true;
            }

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(args[2]);

            int page = 1;

            if (args.length >= 4
                    && isNumber(args[3])) {

                page = Integer.parseInt(args[3]);
            }

            List<String> list =
                    ReputationHistoryManager.getPage(
                            target.getUniqueId(),
                            page,
                            PAGE_SIZE
                    );

            sendHeader(
                    sender,
                    "§fHistorique de §e"
                            + safeName(target)
                            + " §8(Page "
                            + page
                            + ")"
            );

            if (list.isEmpty()) {

                sender.sendMessage("§7Aucune donnée.");

            } else {

                list.forEach(
                        l -> sender.sendMessage(" " + l)
                );
            }

            sendFooter(sender);

            return true;
        }

        //
        // ACTIONS ADMIN
        //

        if (args.length < 3) {

            sender.sendMessage(
                    "§cUsage: /reputation admin <action> <joueur> [valeur]"
            );

            return true;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[2]);

        String id =
                target.getUniqueId().toString();

        switch (action) {

            //
            // ➕ AJOUT
            //

            case "ajouter" -> {

                int value =
                        parse(sender, args, 3);

                if (value <= 0) return true;

                if (target.isOnline()) {

                    ReputationManager.addRepStyled(
                            target.getPlayer(),
                            value,
                            "Ajout administrateur"
                    );

                } else {

                    ReputationManager.add(id, value);

                    ReputationHistoryManager.add(
                            target.getUniqueId(),
                            value,
                            "Ajout administrateur"
                    );
                }

                sendHeader(
                        sender,
                        "§aRéputation ajoutée"
                );

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage(
                        "§7Variation: §a+"
                                + value
                );

                sendFooter(sender);
            }

            //
            // ➖ RETRAIT
            //

            case "retirer" -> {

                int value =
                        parse(sender, args, 3);

                if (value <= 0) return true;

                if (target.isOnline()) {

                    ReputationManager.addRepStyled(
                            target.getPlayer(),
                            -value,
                            "Retrait administrateur"
                    );

                } else {

                    ReputationManager.add(id, -value);

                    ReputationHistoryManager.add(
                            target.getUniqueId(),
                            -value,
                            "Retrait administrateur"
                    );
                }

                sendHeader(
                        sender,
                        "§cRéputation retirée"
                );

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage(
                        "§7Variation: §c-"
                                + value
                );

                sendFooter(sender);
            }

            //
            // ✏ DEFINIR
            //

            case "definir" -> {

                int value =
                        parse(sender, args, 3);

                if (value < -1000) value = -1000;
                if (value > 1000) value = 1000;

                ReputationManager.set(id, value);

                ReputationHistoryManager.add(
                        target.getUniqueId(),
                        value,
                        "Définition administrateur"
                );

                sendHeader(
                        sender,
                        "§fRéputation définie"
                );

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage(
                        "§7Nouvelle valeur: §e"
                                + value
                );

                sendFooter(sender);
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

                sendHeader(
                        sender,
                        "§cRéputation réinitialisée"
                );

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sendFooter(sender);
            }

            //
            // ❌ UNKNOWN
            //

            default ->
                    sender.sendMessage("§cAction inconnue.");
        }

        return true;
    }

    //
    // 🎨 HEADER MOODCRAFT
    //

    private void sendHeader(CommandSender sender, String subtitle) {

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ §fRéputation MoodCraft §6✦ §8-----");
        sender.sendMessage("§6✦ " + subtitle + " §6✦");
        sender.sendMessage("");
    }

    //
    // 🎨 FOOTER MOODCRAFT
    //

    private void sendFooter(CommandSender sender) {

        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    //
    // 🔢 PARSE
    //

    private int parse(CommandSender sender, String[] args, int index) {

        try {

            return Integer.parseInt(args[index]);

        } catch (Exception e) {

            sender.sendMessage("§cValeur invalide.");

            return -1;
        }
    }

    //
    // 🔢 NUMBER
    //

    private boolean isNumber(String s) {

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

    private String getNameFromId(String id) {

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

    private String safeName(OfflinePlayer p) {

        return p.getName() != null
                ? p.getName()
                : "Inconnu";
    }
}