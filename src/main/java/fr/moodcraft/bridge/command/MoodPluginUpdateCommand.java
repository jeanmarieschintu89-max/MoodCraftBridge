package fr.moodcraft.bridge.command;

import fr.moodcraft.bridge.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class MoodPluginUpdateCommand implements CommandExecutor {

    private static final String DEFAULT_OWNER = "jeanmarieschintu89-max";
    private static final String DEFAULT_REPO = "MoodCraftBridge";
    private static final String DEFAULT_WORKFLOW = "moodcraft-minestrator.yml";
    private static final String DEFAULT_BRANCH = "main";
    private static final String DEFAULT_ACTION = "1 - Build et déploie Minestrator";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("moodcraft.admin")) {
            sender.sendMessage("§8----- §d§l✦ MoodCraft Plugins ✦ §8-----");
            sender.sendMessage("§c■ §fAccès réservé à l'administration MoodCraft.");
            sender.sendMessage("§8-----------------------------");
            return true;
        }

        String token = token();
        if (token == null || token.isBlank()) {
            sender.sendMessage("§8----- §d§l✦ MoodCraft Plugins ✦ §8-----");
            sender.sendMessage("§c■ §fToken GitHub manquant.");
            sender.sendMessage("§d➜ §fAjoute §eplugin-update.github-token §fdans la config du Bridge.");
            sender.sendMessage("§7Le token doit avoir accès aux Actions du dépôt MoodCraftBridge.");
            sender.sendMessage("§8-----------------------------");
            return true;
        }

        String reason = args.length == 0 ? "Mise à jour MoodCraft" : String.join(" ", args);
        sender.sendMessage("§8----- §d§l✦ MoodCraft Plugins ✦ §8-----");
        sender.sendMessage("§a▶ §fLancement du workflow GitHub...");
        sender.sendMessage("§e★ §fAction : §e" + DEFAULT_ACTION);
        sender.sendMessage("§d➜ §fMessage : §7" + reason);
        sender.sendMessage("§8-----------------------------");

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> trigger(sender, token, reason));
        return true;
    }

    private void trigger(CommandSender sender, String token, String reason) {
        String owner = config("plugin-update.owner", DEFAULT_OWNER);
        String repo = config("plugin-update.repo", DEFAULT_REPO);
        String workflow = config("plugin-update.workflow", DEFAULT_WORKFLOW);
        String branch = config("plugin-update.branch", DEFAULT_BRANCH);
        String action = config("plugin-update.action", DEFAULT_ACTION);
        String delay = config("plugin-update.delay-seconds", "10");

        String body = "{"
                + "\"ref\":\"" + json(branch) + "\","
                + "\"inputs\":{"
                + "\"action\":\"" + json(action) + "\","
                + "\"delay_seconds\":\"" + json(delay) + "\","
                + "\"reason\":\"" + json(reason) + "\""
                + "}"
                + "}";

        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/actions/workflows/" + workflow + "/dispatches";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + token)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> report(sender, response.statusCode(), response.body()));
        } catch (IOException exception) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> error(sender, "Connexion GitHub impossible : " + exception.getMessage()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> error(sender, "Requête GitHub interrompue."));
        }
    }

    private void report(CommandSender sender, int status, String body) {
        if (status == 204) {
            Bukkit.broadcastMessage("§8----- §d§l✦ MoodCraft Plugins ✦ §8-----");
            Bukkit.broadcastMessage("§a▶ §fMise à jour des plugins lancée depuis le serveur.");
            Bukkit.broadcastMessage("§d➜ §fGitHub construit, déploie sur Minestrator puis redémarre.");
            Bukkit.broadcastMessage("§7Patiente quelques minutes avant de relancer une action.");
            Bukkit.broadcastMessage("§8-----------------------------");
            return;
        }
        error(sender, "GitHub a répondu : HTTP " + status + " " + body);
    }

    private void error(CommandSender sender, String message) {
        sender.sendMessage("§8----- §d§l✦ MoodCraft Plugins ✦ §8-----");
        sender.sendMessage("§c■ §f" + message);
        sender.sendMessage("§d➜ §fVérifie le token, le workflow et les permissions GitHub Actions.");
        sender.sendMessage("§8-----------------------------");
    }

    private String token() {
        String fromConfig = Main.getInstance().getConfig().getString("plugin-update.github-token", "");
        if (fromConfig != null && !fromConfig.isBlank()) return fromConfig.trim();
        String fromEnv = System.getenv("GITHUB_PAT");
        return fromEnv == null ? "" : fromEnv.trim();
    }

    private String config(String path, String fallback) {
        String value = Main.getInstance().getConfig().getString(path, fallback);
        return value == null || value.isBlank() ? fallback : value;
    }

    private String json(String value) {
        return value == null ? "" : value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}
