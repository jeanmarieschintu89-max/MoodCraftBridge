package fr.moodcraft.bridge.gui;

import fr.moodcraft.bridge.bank.BankStorage;
import fr.moodcraft.bridge.util.SafeGUI;
import fr.moodcraft.bridge.util.VaultHook;
import fr.moodcraft.bridge.manager.GUIManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class BankGUI {

    public static void open(Player p) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        36,
                        "§6✦ §0Banque MoodCraft"
                );

        double bank =
                BankStorage.get(
                        p.getUniqueId().toString()
                );

        double cash = 0;

        try {

            cash =
                    VaultHook.getBalance(p);

        } catch (Exception ignored) {}

        //
        // 🖤 FILL
        //

        SafeGUI.fill(
                inv,
                Material.BLACK_STAINED_GLASS_PANE,
                " "
        );

        //
        // 💰 INFORMATIONS
        //

        SafeGUI.safeSet(inv, 4,

                SafeGUI.item(
                        Material.GOLD_BLOCK,

                        "§6✦ §fCompte bancaire",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Liquide:",
                        "§a" + SafeGUI.money(cash) + "€",
                        "",
                        "§7Banque:",
                        "§6" + SafeGUI.money(bank) + "€",
                        "",
                        "§8MoodCraft Financial System"
                )
        );

        //
        // 📥 DÉPÔT
        //

        SafeGUI.safeSet(inv, 10,

                SafeGUI.item(
                        Material.EMERALD,

                        "§a✦ §fDéposer",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Transférer ton argent",
                        "§7liquide vers la banque.",
                        "",
                        "§e▶ Cliquer pour déposer"
                )
        );

        //
        // 📤 RETRAIT
        //

        SafeGUI.safeSet(inv, 12,

                SafeGUI.item(
                        Material.REDSTONE,

                        "§c✦ §fRetirer",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Retirer de l'argent",
                        "§7depuis ton compte.",
                        "",
                        "§e▶ Cliquer pour retirer"
                )
        );

        //
        // 💸 VIREMENT
        //

        SafeGUI.safeSet(inv, 14,

                SafeGUI.item(
                        Material.WRITABLE_BOOK,

                        "§e✦ §fVirement bancaire",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Envoyer de l'argent",
                        "§7à un autre joueur.",
                        "",
                        "§7Compatible IBAN MoodCraft",
                        "",
                        "§e▶ Cliquer pour transférer"
                )
        );

        //
        // 🏦 IBAN
        //

        SafeGUI.safeSet(inv, 16,

                SafeGUI.item(
                        Material.NAME_TAG,

                        "§b✦ §fMon IBAN",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Consulter ton identifiant",
                        "§7bancaire personnel.",
                        "",
                        "§e▶ Cliquer pour afficher"
                )
        );

        //
        // 📜 HISTORIQUE
        //

        SafeGUI.safeSet(inv, 20,

                SafeGUI.item(
                        Material.KNOWLEDGE_BOOK,

                        "§d✦ §fHistorique bancaire",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Voir tes:",
                        "§8• Dépôts",
                        "§8• Retraits",
                        "§8• Virements",
                        "§8• Achats",
                        "§8• Ventes",
                        "",
                        "§e▶ Cliquer pour consulter"
                )
        );

        //
        // 📈 ACTIVITÉ ÉCO
        //

        SafeGUI.safeSet(inv, 24,

                SafeGUI.item(
                        Material.COMPARATOR,

                        "§6✦ §fActivité économique",

                        "§8━━━━━━━━━━━━━━━━",
                        "",
                        "§7Suivre ton activité",
                        "§7sur le marché MoodCraft.",
                        "",
                        "§e▶ Statistiques économiques"
                )
        );

        //
        // 🔙 RETOUR
        //

        SafeGUI.safeSet(inv, 31,

                SafeGUI.item(
                        Material.BARRIER,

                        "§c✦ §fRetour",

                        "§7Retour au menu principal"
                )
        );

        //
        // 🚀 OPEN
        //

        GUIManager.open(
                p,
                "bank_main",
                inv
        );
    }
}