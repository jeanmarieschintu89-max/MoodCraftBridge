private void sell(Player p, String id, Material mat) {

    int amount = count(p, mat);

    if (amount <= 0) {

        p.sendMessage(
                "§cTu n'as aucun "
                        + mat.name().toLowerCase()
        );

        return;
    }

    double unit =
            MarketEngine.getPrice(id);

    double gross =
            unit * amount;

    double taxRate = 0.20;

    double tax =
            gross * taxRate;

    double total =
            gross - tax;

    //
    // 💰 ARGENT
    //

    VaultHook.getEconomy()
            .depositPlayer(p, total);

    //
    // 📦 REMOVE ITEMS
    //

    remove(p, mat, amount);

    //
    // 📉 IMPACT BOURSE
    //

    MarketEngine.recordSell(id, amount);

    //
    // ✨ MESSAGE
    //

    p.sendMessage("");
    p.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    p.sendMessage("§6✦ §fVente effectuée");
    p.sendMessage("");

    p.sendMessage(
            "§7Item: §e"
                    + amount
                    + "x "
                    + id
    );

    p.sendMessage(
            "§7Brut: §f"
                    + String.format("%.2f", gross)
                    + "€"
    );

    p.sendMessage(
            "§cTaxe (20%): §f-"
                    + String.format("%.2f", tax)
                    + "€"
    );

    p.sendMessage(
            "§aNet reçu: §f"
                    + String.format("%.2f", total)
                    + "€"
    );

    p.sendMessage("");
    p.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

    p.sendTitle(
            "§a+"
                    + String.format("%.2f", total)
                    + "€",

            "§cTaxe: -"
                    + String.format("%.2f", tax)
                    + "€",

            5,
            25,
            8
    );
}