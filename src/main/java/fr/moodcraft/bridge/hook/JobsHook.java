package fr.moodcraft.bridge.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JobsHook {

    private static boolean enabled = false;

    private static Method getPlayerManager;
    private static Method getJobsPlayer;
    private static Method getJobProgression;
    private static Method getJob;
    private static Method getName;
    private static Method getLevel;

    // =========================
    // 🔧 INIT (appel au démarrage)
    // =========================
    public static void init() {

        if (Bukkit.getPluginManager().getPlugin("Jobs") == null) return;

        try {
            Class<?> jobsClass = Class.forName("com.gamingmesh.jobs.Jobs");

            getPlayerManager = jobsClass.getMethod("getPlayerManager");
            Object manager = getPlayerManager.invoke(null);

            getJobsPlayer = manager.getClass().getMethod("getJobsPlayer", Player.class);

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
            Bukkit.getLogger().warning("[JobsHook] Impossible de hook Jobs");
        }
    }

    // =========================
    // 📜 LORE
    // =========================
    public static List<String> getJobsLore(Player p) {

        List<String> lore = new ArrayList<>();

        if (!enabled) {
            lore.add("§7Aucun");
            return lore;
        }

        try {

            Object manager = getPlayerManager.invoke(null);
            Object jobsPlayer = getJobsPlayer.invoke(manager, p);

            if (jobsPlayer == null) {
                lore.add("§7Aucun");
                return lore;
            }

            List<?> jobs = (List<?>) getJobProgression.invoke(jobsPlayer);

            if (jobs.isEmpty()) {
                lore.add("§7Aucun");
                return lore;
            }

            int count = 0;

            for (Object prog : jobs) {

                if (count >= 2) {
                    lore.add("§7+ autres...");
                    break;
                }

                Object job = getJob.invoke(prog);
                String name = (String) getName.invoke(job);
                int level = (int) getLevel.invoke(prog);

                lore.add("§a- " + name + " §7(Lv." + level + ")");
                count++;
            }

        } catch (Exception e) {
            lore.clear();
            lore.add("§7Erreur");
        }

        return lore;
    }
}