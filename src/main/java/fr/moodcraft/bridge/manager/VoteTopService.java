package fr.moodcraft.bridge.manager;

import fr.moodcraft.bridge.Main;

import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VoteTopService {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final String DEFAULT_URL = "https://moodcraft.fr/vote";
    private static final long CACHE_MS = 5 * 60_000L;

    private static List<VoteEntry> cachedTop = Collections.emptyList();
    private static long cachedAt = 0L;
    private static boolean fetching = false;

    private VoteTopService() {}

    public static List<VoteEntry> top(int limit) {
        long now = System.currentTimeMillis();

        if (now - cachedAt > CACHE_MS && !fetching) {
            refreshAsync();
        }

        return trim(cachedTop, limit);
    }

    public static void refreshAsync() {
        if (fetching) return;

        fetching = true;

        CompletableFuture
                .supplyAsync(VoteTopService::fetchTop)
                .whenComplete((result, throwable) -> Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    fetching = false;

                    if (throwable != null) {
                        Main.getInstance().getLogger().warning("[VoteTop] Lecture impossible: " + throwable.getMessage());
                        return;
                    }

                    if (result == null || result.isEmpty()) {
                        Main.getInstance().getLogger().warning("[VoteTop] Aucun vote trouvé sur la page.");
                        return;
                    }

                    cachedTop = Collections.unmodifiableList(result);
                    cachedAt = System.currentTimeMillis();
                    VotePanelManager.refreshFromCache();
                }));
    }

    public static void forceRefresh() {
        cachedAt = 0L;
        refreshAsync();
    }

    private static List<VoteEntry> fetchTop() {
        String url = Main.getInstance().getConfig().getString("vote-top.url", DEFAULT_URL);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "MoodCraftBridge/1.0")
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                Main.getInstance().getLogger().warning("[VoteTop] HTTP " + response.statusCode() + " sur " + url);
                return Collections.emptyList();
            }

            return parseHtml(response.body());

        } catch (Exception e) {
            Main.getInstance().getLogger().warning("[VoteTop] Erreur HTTP: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static List<VoteEntry> parseHtml(String html) {
        List<VoteEntry> entries = new ArrayList<>();

        String cleanHtml = html
                .replace("\r", "")
                .replace("\n", " ")
                .replace("\t", " ");

        Pattern rowPattern = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher rowMatcher = rowPattern.matcher(cleanHtml);

        while (rowMatcher.find()) {
            String row = rowMatcher.group(1);

            List<String> cells = extractCells(row);
            if (cells.size() < 3) continue;

            Integer rank = parseRank(cells.get(0));
            if (rank == null || rank < 1) continue;

            String name = normalize(cells.get(1));
            Integer votes = parseVotes(cells.get(2));

            if (name.isBlank() || votes == null) continue;
            if (name.equalsIgnoreCase("nom")) continue;

            entries.add(new VoteEntry(rank, name, votes));
        }

        entries.sort((a, b) -> Integer.compare(a.rank(), b.rank()));

        return entries;
    }

    private static List<String> extractCells(String row) {
        List<String> cells = new ArrayList<>();

        Pattern cellPattern = Pattern.compile("<t[dh][^>]*>(.*?)</t[dh]>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = cellPattern.matcher(row);

        while (matcher.find()) {
            cells.add(stripHtml(matcher.group(1)));
        }

        return cells;
    }

    private static String stripHtml(String text) {
        return text
                .replaceAll("<[^>]+>", "")
                .replace("&nbsp;", " ")
                .replace("&#039;", "'")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .trim();
    }

    private static String normalize(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("\\s+", " ");
    }

    private static Integer parseRank(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return null;

        try {
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer parseVotes(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return null;

        try {
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<VoteEntry> trim(List<VoteEntry> source, int limit) {
        if (source.size() <= limit) return source;
        return new ArrayList<>(source.subList(0, limit));
    }

    public static String panelVotes(int votes) {
        return votes + " votes";
    }

    public static String shorten(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(1, max - 1)) + ".";
    }

    public static String sourceUrl() {
        return Main.getInstance().getConfig().getString("vote-top.url", DEFAULT_URL);
    }

    public static record VoteEntry(int rank, String name, int votes) {}
}