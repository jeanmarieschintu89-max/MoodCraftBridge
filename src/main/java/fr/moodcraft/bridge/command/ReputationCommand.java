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

            p.sendMessage("");
            p.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            p.sendMessage("§6✦ §fRéputation MoodCraft");
            p.sendMessage("");

            p.sendMessage("§7Points: §e" + rep);
            p.sendMessage(
                    "§7Rang: "
                            + ReputationManager.getRank(rep)
            );

            p.sendMessage("");
            p.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            p.sendMessage("");

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

            p.sendMessage("");
            p.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            p.sendMessage(
                    "§6✦ §fHistorique réputation §8(Page "
                            + page
                            + ")"
            );
            p.sendMessage("");

            if (list.isEmpty()) {

                p.sendMessage("§7Aucune donnée.");

            } else {

                list.forEach(
                        l -> p.sendMessage(" " + l)
                );
            }

            p.sendMessage("");
            p.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            p.sendMessage("");

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

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage(
                    "§6✦ §fRéputation de §e"
                            + safeName(target)
            );
            sender.sendMessage("");

            sender.sendMessage("§7Points: §e" + rep);

            sender.sendMessage(
                    "§7Rang: "
                            + ReputationManager.getRank(rep)
            );

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("");

            return true;
        }

        //
        // 🏆 CLASSEMENT
        //

        if (args[0].equalsIgnoreCase("classement")) {

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("§6🏆 §fClassement réputation");
            sender.sendMessage("");

            int i = 1;

            Map<String, Integer> top =
                    ReputationManager.getTop(10);

            if (top.isEmpty()) {

                sender.sendMessage("§7Aucune réputation enregistrée.");

            } else {

                for (Map.Entry<String, Integer> entry :
                        top.entrySet()) {

                    UUID uuid =
                            UUID.fromString(entry.getKey());

                    String name =
                            safeName(
                                    Bukkit.getOfflinePlayer(uuid)
                            );

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

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("");

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

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("§c✦ §fPermission refusée");
            sender.sendMessage("");
            sender.sendMessage("§7Tu n'as pas accès à cette commande.");
            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("");

            return true;
        }

        if (args.length < 2) {

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("§6✦ §fCommande réputation");
            sender.sendMessage("");

            sender.sendMessage(
                    "§7Usage:"
            );

            sender.sendMessage(
                    "§e/reputation admin <ajouter|retirer|definir|reset|historique> <joueur> [valeur/page]"
            );

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("");

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

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage(
                    "§6✦ §fHistorique de §e"
                            + safeName(target)
                            + " §8(Page "
                            + page
                            + ")"
            );

            sender.sendMessage("");

            if (list.isEmpty()) {

                sender.sendMessage("§7Aucune donnée.");

            } else {

                list.forEach(
                        l -> sender.sendMessage(" " + l)
                );
            }

            sender.sendMessage("");
            sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            sender.sendMessage("");

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

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("§a✦ §fRéputation ajoutée");
                sender.sendMessage("");

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage(
                        "§7Variation: §a+"
                                + value
                );

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("");
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

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("§c✦ §fRéputation retirée");
                sender.sendMessage("");

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage(
                        "§7Variation: §c-"
                                + value
                );

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("");
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

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("§6✦ §fRéputation définie");
                sender.sendMessage("");

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage(
                        "§7Nouvelle valeur: §e"
                                + value
                );

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("");
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

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("§c✦ §fRéputation réinitialisée");
                sender.sendMessage("");

                sender.sendMessage(
                        "§7Joueur: §e"
                                + safeName(target)
                );

                sender.sendMessage("");
                sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("");
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
    // 🧠 SAFE NAME
    //

    private String safeName(OfflinePlayer p) {

        return p.getName() != null
                ? p.getName()
                : "Inconnu";
    }
}