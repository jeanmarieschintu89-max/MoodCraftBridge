package fr.moodcraft.contracts.gestionnaire;

import fr.moodcraft.contracts.contrat.Contrat;

import org.bukkit.Bukkit;

import org.bukkit.Material;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;

import fr.moodcraft.contracts.Main;

public class GestionnaireCompletionContrat {

    //
    // 💰 VAULT
    //

    private static Economy economy;

    static {

        RegisteredServiceProvider<Economy> rsp =

                Bukkit.getServicesManager()
                        .getRegistration(
                                Economy.class
                        );

        if (rsp != null) {

            economy =
                    rsp.getProvider();
        }
    }

    // =========================
    // ✅ COMPLETER
    // =========================

    public static boolean completer(

            Player joueur,

            Contrat contrat
    ) {

        //
        // ❌ NULL
        //

        if (contrat == null) {

            return false;
        }

        Material material =
                contrat.getRessource();

        int quantite =
                contrat.getQuantite();

        double recompense =
                contrat.getRecompense();

        //
        // 📦 VERIFICATION STOCK
        //

        int total = 0;

        for (ItemStack item :
                joueur.getInventory()
                        .getContents()) {

            if (item == null)
                continue;

            if (item.getType()
                    != material)
                continue;

            total += item.getAmount();
        }

        //
        // ❌ STOCK INSUFFISANT
        //

        if (total < quantite) {

            joueur.sendMessage("");

            joueur.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            joueur.sendMessage(
                    "§c✦ §fLivraison refusée"
            );

            joueur.sendMessage("");

            joueur.sendMessage(
                    "§7Le réseau logistique MoodCraft"
            );

            joueur.sendMessage(
                    "§7détecte des ressources insuffisantes."
            );

            joueur.sendMessage("");

            joueur.sendMessage(
                    "§7Ressource demandée: §f"
                            + material.name()
            );

            joueur.sendMessage(
                    "§7Quantité requise: §e"
                            + quantite
            );

            joueur.sendMessage("");

            joueur.sendMessage(
                    "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            );

            joueur.sendMessage("");

            joueur.playSound(

                    joueur.getLocation(),

                    Sound.ENTITY_VILLAGER_NO,

                    1f,

                    0.9f
            );

            return false;
        }

        //
        // 📦 RETRAIT ITEMS
        //

        int restant =
                quantite;

        for (ItemStack item :
                joueur.getInventory()
                        .getContents()) {

            if (item == null)
                continue;

            if (item.getType()
                    != material)
                continue;

            int stack =
                    item.getAmount();

            if (stack <= restant) {

                restant -= stack;

                item.setAmount(0);

            } else {

                item.setAmount(
                        stack - restant
                );

                restant = 0;
            }

            if (restant <= 0)
                break;
        }

        //
        // 💰 PAIEMENT
        //

        if (economy != null) {

            economy.depositPlayer(
                    joueur,
                    recompense
            );
        }

        //
        // 📊 STATUT
        //

        contrat.setStatut(
                Contrat.Statut.TERMINE
        );

        //
        // ❌ SUPPRESSION
        //

        GestionnaireContrats.supprimer(
                contrat.getId()
        );

        //
        // ✨ SUCCÈS
        //

        joueur.sendMessage("");

        joueur.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        joueur.sendMessage(
                "§6✦ §fLivraison validée"
        );

        joueur.sendMessage("");

        joueur.sendMessage(
                "§7Le réseau économique MoodCraft"
        );

        joueur.sendMessage(
                "§7confirme la réception des ressources."
        );

        joueur.sendMessage("");

        joueur.sendMessage(
                "§7Ressource livrée: §f"
                        + material.name()
        );

        joueur.sendMessage(
                "§7Quantité transférée: §e"
                        + quantite
        );

        joueur.sendMessage("");

        joueur.sendMessage(
                "§7Paiement reçu: §a+"
                        + recompense
                        + "€"
        );

        joueur.sendMessage("");

        joueur.sendMessage(
                "§a✔ Fonds transférés automatiquement"
        );

        joueur.sendMessage("");

        joueur.sendMessage(
                "§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        joueur.sendMessage("");

        joueur.playSound(

                joueur.getLocation(),

                Sound.ENTITY_PLAYER_LEVELUP,

                1f,

                1.05f
        );

        //
        // 🌍 BROADCAST
        //

        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(
                "§6✦ §fLe réseau contrats annonce :"
        );

        Bukkit.broadcastMessage(
                "§e"
                        + joueur.getName()

                        + " §7a livré §e"

                        + quantite

                        + "x "

                        + material.name()

                        + " §7contre §a"

                        + recompense

                        + "€"
        );

        Bukkit.broadcastMessage("");

        return true;
    }
}