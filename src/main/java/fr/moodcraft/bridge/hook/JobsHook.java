package fr.moodcraft.bridge.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JobsHook {

    private static boolean enabled = false;

    private static Method getJobsPlayer;
    private static Method getJobProgression;
    private static Method getJob;
    private static Method getName;
    private static Method getLevel;

    private static Object playerManager; // 🔥 cache

    // =========================
    // 🔧 INIT
    // =========================
    public static void init() {

        Plugin jobs = Bukkit.getPluginManager().getPlugin("Jobs");

        if (jobs == null || !jobs.isEnabled()) return;

        try {
            Class<?> jobsClass = Class.forName("com.gamingmesh.jobs.Jobs");

            Method getPlayerManager = jobsClass.getMethod("getPlayerManager");
            playerManager = getPlayerManager.invoke(null);

            getJobsPlayer = playerManager.getClass().getMethod("getJobsPlayer", Player.class);

            Class<?> jobsPlayerClass = Class.forName("com.gamingmesh.jobs.container.JobsPlayer");
            getJobProgression = jobsPlayerClass.getMethod("getJobProgression");

            Class<?> progressionClass = Class.forName("com.gamingmesh.jobs.container.JobProgression");
            getJob = progressionClass.getMethod("getJob");
            getLevel = progressionClass.getMethod("getLevel");

            Class<?> jobClass = Class.forName("com.gamingmesh.jobs.container.Job");
            getName = jobClass.getMethod("getName");

            enabled = true;

            Bukkit.getLogger().info("[JobsHook] Hook activé");

        } catch (Exception e) {
            Bukkit.getLogger().warning("[JobsHook] Hook échoué: " + e.getMessage());
        }
    }

    // =========================
    // 📜 LORE
    // =========================
    public static List<String> getJobsLore(Player p) {

        List<String> lore = new ArrayList<>();

        if (!enabled || playerManager == null) {
            lore.add("§7Aucun métier");
            return lore;
        }

        try {

            Object jobsPlayer = getJobsPlayer.invoke(playerManager, p);

            if (jobsPlayer == null) {
                lore.add("§7Aucun métier");
                return lore;
            }

            List<?> jobs = (List<?>) getJobProgression.invoke(jobsPlayer);

            if (jobs == null || jobs.isEmpty()) {
                lore.add("§7Aucun métier");
                return lore;
            }

            int count = 0;

            for (Object prog : jobs) {

                if (count >= 2) {
                    lore.add("§7+ autres...");
                    break;
                }

                Object job = getJob.invoke(prog);

                if (job == null) continue;

                String name = (String) getName.invoke(job);
                int level = (int) getLevel.invoke(prog);

                lore.add("§a- " + name + " §7(Lv." + level + ")");
                count++;
            }

        } catch (Exception e) {
            lore.clear();
            lore.add("§7Erreur Jobs");
        }

        return lore;
    }
}