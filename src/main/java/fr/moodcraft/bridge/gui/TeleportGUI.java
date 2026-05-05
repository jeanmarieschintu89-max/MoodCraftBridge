package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.manager.GUIManager;
import fr.moodcraft.bridge.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TeleportGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§bTeleport");

        SafeGUI.safeSet(inv, 10,
                SafeGUI.item(Material.OAK_LOG, "§aRessources",
                        "§8────────────",
                        "§7Récolte minerais",
                        "§7et ressources",
                        "",
                        "§8Clique pour accéder"));

        SafeGUI.safeSet(inv, 11,
                SafeGUI.item(Material.EMERALD, "§6AdminShop",
                        "§8────────────",
                        "§7Boutique d'items",
                        "",
                        "§aAcheter",
                        "",
                        "§8Clique pour accéder"));

        SafeGUI.safeSet(inv, 12,
                SafeGUI.item(Material.NETHER_STAR, "§dMini-jeux",
                        "§8────────────",
                        "§7Activités fun",
                        "",
                        "§aRécompenses à gagner",
                        "",
                        "§8Clique pour jouer"));

        SafeGUI.safeSet(inv, 13,
                SafeGUI.item(Material.ENDER_EYE, "§5Exploration",
                        "§8────────────",
                        "§7Téléportation aléatoire",
                        "",
                        "§7Découverte libre",
                        "",
                        "§8Clique pour explorer"));

        SafeGUI.safeSet(inv, 14,
                SafeGUI.item(Material.COMPASS, "§eSpawn",
                        "§8────────────",
                        "§7Retour à l'accueil",
                        "",
                        "§8Clique pour revenir"));

        SafeGUI.safeSet(inv, 15,
                SafeGUI.item(Material.BRICKS, "§aVille",
                        "§8────────────",
                        "§7Accès à ta ville",
                        "",
                        "§7Gestion et territoire",
                        "",
                        "§8Clique pour ouvrir"));

        SafeGUI.safeSet(inv, 22,
                SafeGUI.item(Material.BARRIER, "§cRetour",
                        "§8────────────",
                        "§7Retour au menu",
                        "",
                        "§8Clique pour revenir"));

        GUIManager.open(p, "teleport", inv);
    }
}