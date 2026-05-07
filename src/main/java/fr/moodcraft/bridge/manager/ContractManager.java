package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.contract.Contract;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContractManager {

    //
    // 📦 CONTRATS
    //

    private static final Map<String, Contract>
            contracts = new HashMap<>();

    // =========================
    // ➕ CREATE
    // =========================

    public static Contract create(

            UUID owner,

            org.bukkit.Material item,

            int amount,

            double reward
    ) {

        String id =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Contract contract =
                new Contract(

                        id,

                        owner,

                        item,

                        amount,

                        reward
                );

        contracts.put(
                id,
                contract
        );

        return contract;
    }

    // =========================
    // 🔍 GET
    // =========================

    public static Contract get(String id) {

        return contracts.get(id);
    }

    // =========================
    // 📜 GET ALL
    // =========================

    public static Collection<Contract> getAll() {

        return contracts.values();
    }

    // =========================
    // ❌ REMOVE
    // =========================

    public static void remove(String id) {

        contracts.remove(id);
    }
}