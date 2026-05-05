package fr.moodcraft.bridge.bank;

public class BankAPI {

    public static double get(String uuid) {
        return BankStorage.get(uuid);
    }

    public static void add(String uuid, double amount) {
        BankStorage.add(uuid, amount);
    }

    public static void remove(String uuid, double amount) {
        BankStorage.remove(uuid, amount);
    }

    public static void set(String uuid, double amount) {
        BankStorage.set(uuid, amount);
    }
}