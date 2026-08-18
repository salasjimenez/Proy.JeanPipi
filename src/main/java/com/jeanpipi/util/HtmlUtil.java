package com.jeanpipi.util;

// Sanitizacion del contenido editorial.
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlUtil {
    private static final Safelist EDITOR = Safelist.relaxed()
            .addTags("figure", "figcaption", "blockquote", "h2", "h3", "h4", "span")
            .addAttributes("a", "target", "rel")
            .addAttributes("img", "loading", "alt", "title")
            .addAttributes("span", "class")
            .preserveRelativeLinks(false);

    private HtmlUtil() {}

    public static String sanitize(String html) {
        return Jsoup.clean(html == null ? "" : html, EDITOR);
    }

    public static String plainText(String html) {
        return Jsoup.parse(html == null ? "" : html).text();
    }

    public static int readingMinutes(String html) {
        String text = plainText(html).trim();
        if (text.isBlank()) {
            return 1;
        }
        int words = text.split("\\s+").length;
        return Math.max(1, (int) Math.ceil(words / 220.0));
    }
}
