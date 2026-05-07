package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;

import fr.moodcraft.bridge.contract.Contract;

import org.bukkit.Material;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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

    //
    // 🧠 SLOT MAP
    //

    private static final Map<Integer, Contract>
            slotMap = new HashMap<>();

    //
    // 💾 FILE
    //

    private static File file;

    private static FileConfiguration config;

    // =========================
    // 🚀 INIT
    // =========================

    public static void init() {

        file = new File(

                Main.getInstance()
                        .getDataFolder(),

                "contracts.yml"
        );

        if (!file.exists()) {

            try {

                file.getParentFile().mkdirs();

                file.createNewFile();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        config =
                YamlConfiguration.loadConfiguration(file);

        load();
    }

    // =========================
    // 📥 LOAD
    // =========================

    public static void load() {

        contracts.clear();

        for (String id :

                config.getKeys(false)) {

            try {

                UUID owner =
                        UUID.fromString(

                                config.getString(
                                        id + ".owner"
                                )
                        );

                Material material =
                        Material.valueOf(

                                config.getString(
                                        id + ".item"
                                )
                        );

                int amount =
                        config.getInt(
                                id + ".amount"
                        );

                double reward =
                        config.getDouble(
                                id + ".reward"
                        );

                Contract contract =
                        new Contract(

                                id,

                                owner,

                                material,

                                amount,

                                reward
                        );

                //
                // 📊 STATUS
                //

                String status =
                        config.getString(
                                id + ".status",
                                "OPEN"
                        );

                contract.setStatus(

                        Contract.Status.valueOf(
                                status
                        )
                );

                //
                // 🤝 WORKER
                //

                if (config.contains(
                        id + ".worker"
                )) {

                    contract.setWorker(

                            UUID.fromString(

                                    config.getString(
                                            id + ".worker"
                                    )
                            )
                    );
                }

                contracts.put(
                        id,
                        contract
                );

            } catch (Exception e) {

                Bukkit.getLogger().warning(
                        "[MoodCraft] Contrat invalide: "
                                + id
                );
            }
        }
    }

    // =========================
    // 💾 SAVE
    // =========================

    public static void save() {

        for (String key :
                config.getKeys(false)) {

            config.set(key, null);
        }

        for (Contract contract :

                contracts.values()) {

            String id =
                    contract.getId();

            config.set(
                    id + ".owner",
                    contract.getOwner().toString()
            );

            config.set(
                    id + ".item",
                    contract.getItem().name()
            );

            config.set(
                    id + ".amount",
                    contract.getAmount()
            );

            config.set(
                    id + ".reward",
                    contract.getReward()
            );

            config.set(
                    id + ".status",
                    contract.getStatus().name()
            );

            if (contract.getWorker() != null) {

                config.set(
                        id + ".worker",
                        contract.getWorker().toString()
                );
            }
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // =========================
    // ➕ CREATE
    // =========================

    public static Contract create(

            UUID owner,

            Material item,

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

        save();

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

        save();
    }

    // =========================
    // 📦 SET SLOT
    // =========================

    public static void setSlot(
            int slot,
            Contract contract
    ) {

        slotMap.put(
                slot,
                contract
        );
    }

    // =========================
    // 🔍 GET SLOT
    // =========================

    public static Contract getBySlot(
            int slot
    ) {

        return slotMap.get(slot);
    }

    // =========================
    // 🧹 CLEAR SLOTS
    // =========================

    public static void clearSlots() {

        slotMap.clear();
    }
}