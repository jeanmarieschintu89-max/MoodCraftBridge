package fr.moodcraft.bridge.contract;

import org.bukkit.Material;

import java.util.UUID;

public class Contract {

    //
    // 🆔 ID
    //

    private final String id;

    //
    // 👤 CRÉATEUR
    //

    private final UUID owner;

    //
    // 🤝 TRAVAILLEUR
    //

    private UUID worker;

    //
    // 📦 ITEM
    //

    private final Material item;

    //
    // 🔢 QUANTITÉ
    //

    private final int amount;

    //
    // 💰 RÉCOMPENSE
    //

    private final double reward;

    //
    // 📊 STATUS
    //

    private Status status;

    //
    // 🕒 DATE
    //

    private final long createdAt;

    // =========================
    // 🚀 CONSTRUCTOR
    // =========================

    public Contract(
            String id,
            UUID owner,
            Material item,
            int amount,
            double reward
    ) {

        this.id = id;

        this.owner = owner;

        this.item = item;

        this.amount = amount;

        this.reward = reward;

        this.status = Status.OPEN;

        this.createdAt =
                System.currentTimeMillis();
    }

    // =========================
    // 📊 STATUS
    // =========================

    public enum Status {

        OPEN,

        IN_PROGRESS,

        COMPLETED,

        CANCELLED
    }

    // =========================
    // 🆔 GET ID
    // =========================

    public String getId() {

        return id;
    }

    // =========================
    // 👤 GET OWNER
    // =========================

    public UUID getOwner() {

        return owner;
    }

    // =========================
    // 🤝 GET WORKER
    // =========================

    public UUID getWorker() {

        return worker;
    }

    // =========================
    // 🤝 SET WORKER
    // =========================

    public void setWorker(UUID worker) {

        this.worker = worker;
    }

    // =========================
    // 📦 GET ITEM
    // =========================

    public Material getItem() {

        return item;
    }

    // =========================
    // 🔢 GET AMOUNT
    // =========================

    public int getAmount() {

        return amount;
    }

    // =========================
    // 💰 GET REWARD
    // =========================

    public double getReward() {

        return reward;
    }

    // =========================
    // 📊 GET STATUS
    // =========================

    public Status getStatus() {

        return status;
    }

    // =========================
    // 📊 SET STATUS
    // =========================

    public void setStatus(Status status) {

        this.status = status;
    }

    // =========================
    // 🕒 GET CREATED
    // =========================

    public long getCreatedAt() {

        return createdAt;
    }
}