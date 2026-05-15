package fr.moodcraft.bridge.gui;

public final class GuiTitle {

    private GuiTitle() {}

    public static String of(String title) {
        if (title == null || title.isBlank()) {
            title = "MoodCraft";
        }

        return "§6✦ §8§l" + title + " §6✦";
    }

    public static String moodCraft() {
        return "§6✦ §8§lMenu §aMood§6Craft §6✦";
    }

    public static String clean(String title) {
        if (title == null) {
            return "";
        }

        return title
                .replaceAll("§.", "")
                .replace("✦", "")
                .trim()
                .toLowerCase();
    }
}
