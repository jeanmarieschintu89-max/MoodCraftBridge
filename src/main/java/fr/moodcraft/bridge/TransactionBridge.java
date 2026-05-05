package fr.moodcraft.bridge;

public class TransactionBridge {

    // =========================
    // 💰 LOG SIMPLE
    // =========================
    public static void log(String player, String type, double amount) {
        TransactionLogger.log(player, type, amount);
    }

    // =========================
    // 💸 LOG AVEC CIBLE
    // =========================
    public static void log(String player, String type, double amount, String target) {

        if (target != null) {
            type = type + " -> " + target;
        }

        TransactionLogger.log(player, type, amount);
    }

    // =========================
    // 💸 VIREMENT PROPRE
    // =========================
    public static void logTransfer(String from, String to, double amount) {

        TransactionLogger.log(from, "Virement vers " + to, amount);
        TransactionLogger.log(to, "Virement reçu de " + from, amount);
    }

    // =========================
    // 📦 CONTRAT
    // =========================
    public static void logContract(String player, String item, int amount, double price) {

        TransactionLogger.log(
                player,
                "Contrat " + item + " x" + amount,
                price
        );
    }
}