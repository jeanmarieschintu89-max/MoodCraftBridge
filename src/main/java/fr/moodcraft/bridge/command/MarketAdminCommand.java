package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.manager.PriceUpdater;
import fr.moodcraft.bridge.manager.ShopIndex;
import fr.moodcraft.bridge.market.MarketEngine;
import fr.moodcraft.bridge.market.MarketState;
import fr.moodcraft.bridge.market.MarketStorage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.NumberFormat;
import java.util.Locale;

public class MarketAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("moodcraft.admin")) {
            error(sender, "Accès réservé à l'administration économie.");
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "help", "aide" -> help(sender);
            case "status", "etat", "état" -> status(sender);
            case "info" -> info(sender, args);
            case "setprice", "setprix" -> setPrice(sender, args);
            case "resetitem" -> resetItem(sender, args);
            case "resetall" -> resetAll(sender);
            case "sync" -> sync(sender);
            case "tick" -> tick(sender);
            case "save" -> save(sender);
            case "reload" -> reload(sender);
            default -> help(sender);
        }

        return true;
    }

    private void status(CommandSender sender) {
        header(sender, "Admin Marché");
        sender.sendMessage("§e➜ §7Items prix : §e" + MarketState.price.size());
        sender.sendMessage("§e➜ §7Items base : §e" + MarketState.base.size());
        sender.sendMessage("§e➜ §7Items stock : §e" + MarketState.stock.size());
        footer(sender);
    }

    private void info(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/marketadmin info <item>");
            return;
        }

        String item = args[1].toUpperCase(Locale.ROOT);

        header(sender, "Admin Marché");
        sender.sendMessage("§e➜ §7Item : §e" + item);
        sender.sendMessage("§e➜ §7Prix : §e" + money(MarketState.getPrice(item)));
        sender.sendMessage("§e➜ §7Base : §e" + money(MarketState.base.getOrDefault(item, 0.0)));
        sender.sendMessage("§e➜ §7Stock : §e" + MarketState.stock.getOrDefault(item, 0.0));
        sender.sendMessage("§e➜ §7Achats : §e" + MarketState.buy.getOrDefault(item, 0.0));
        sender.sendMessage("§e➜ §7Ventes : §e" + MarketState.sell.getOrDefault(item, 0.0));
        sender.sendMessage("§e➜ §7Tendance : §f" + MarketState.trend.getOrDefault(item, "§7▬ Stable"));
        footer(sender);
    }

    private void setPrice(CommandSender sender, String[] args) {
        if (args.length < 3) {
            usage(sender, "/marketadmin setprice <item> <prix>");
            return;
        }

        Double price = parse(args[2]);
        if (price == null || price < 0) {
            error(sender, "Prix invalide.");
            return;
        }

        String item = args[1].toUpperCase(Locale.ROOT);
        MarketState.setPrice(item, price);
        MarketStorage.save();
        PriceUpdater.updateItem(item);
        success(sender, "Prix forcé : §e" + item + " §8= §e" + money(price));
    }

    private void resetItem(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/marketadmin resetitem <item>");
            return;
        }

        String item = args[1].toUpperCase(Locale.ROOT);
        double base = MarketState.base.getOrDefault(item, -1.0);

        if (base < 0) {
            error(sender, "Item introuvable dans les prix de base.");
            return;
        }

        MarketState.setPrice(item, base);
        MarketState.stock.put(item, 0.0);
        MarketState.buy.put(item, 0.0);
        MarketState.sell.put(item, 0.0);
        MarketStorage.save();
        PriceUpdater.updateItem(item);
        success(sender, "Item reset : §e" + item);
    }

    private void resetAll(CommandSender sender) {
        for (String item : MarketState.base.keySet()) {
            MarketState.setPrice(item, MarketState.base.get(item));
            MarketState.stock.put(item, 0.0);
            MarketState.buy.put(item, 0.0);
            MarketState.sell.put(item, 0.0);
            PriceUpdater.updateItem(item);
        }

        MarketStorage.save();
        success(sender, "Marché entièrement réinitialisé.");
    }

    private void sync(CommandSender sender) {
        ShopIndex.rebuild();

        for (String item : MarketState.price.keySet()) {
            PriceUpdater.updateItem(item);
        }

        success(sender, "Shops resynchronisés avec le marché.");
    }

    private void tick(CommandSender sender) {
        MarketEngine.tick();
        MarketStorage.save();
        success(sender, "Tick marché exécuté manuellement.");
    }

    private void save(CommandSender sender) {
        MarketStorage.save();
        success(sender, "Marché sauvegardé.");
    }

    private void reload(CommandSender sender) {
        MarketStorage.init();
        success(sender, "Marché rechargé depuis le stockage.");
    }

    private Double parse(String input) {
        try {
            return Double.parseDouble(input.replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    private String money(double value) {
        return NumberFormat.getInstance(Locale.FRANCE).format(value) + "€";
    }

    private void help(CommandSender sender) {
        header(sender, "Admin Marché");
        sender.sendMessage("§e➜ §7/marketadmin status");
        sender.sendMessage("§e➜ §7/marketadmin info <item>");
        sender.sendMessage("§e➜ §7/marketadmin setprice <item> <prix>");
        sender.sendMessage("§e➜ §7/marketadmin resetitem <item>");
        sender.sendMessage("§e➜ §7/marketadmin resetall");
        sender.sendMessage("§e➜ §7/marketadmin sync");
        sender.sendMessage("§e➜ §7/marketadmin tick");
        sender.sendMessage("§e➜ §7/marketadmin save");
        sender.sendMessage("§e➜ §7/marketadmin reload");
        footer(sender);
    }

    private void usage(CommandSender sender, String usage) {
        header(sender, "Admin Marché");
        sender.sendMessage("§c✖ §fCommande incomplète.");
        sender.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(sender);
    }

    private void success(CommandSender sender, String message) {
        header(sender, "Admin Marché");
        sender.sendMessage("§a✔ §f" + message);
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Admin Marché");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
    }
}
