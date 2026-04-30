package com.example.agenticdev.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class TextUtils {
    private static final Pattern NON_IDENTIFIER = Pattern.compile("[^a-zA-Z0-9_]+_");

    private TextUtils() {
    }

    public static boolean containsAny(String text, String... words) {
        if (text == null) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        for (String word : words) {
            if (lower.contains(word.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    public static String shortText(String text, int max) {
        if (text == null) {
            return "";
        }
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= max) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, max - 3)) + "...";
    }

    public static List<String> distinct(List<String> values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                set.add(value.trim());
            }
        }
        return new ArrayList<>(set);
    }

    public static String toJavaClassName(String raw, String fallback) {
        String base = raw == null || raw.isBlank() ? fallback : raw;
        String[] parts = base.replaceAll("[^a-zA-Z0-9]+", " ").trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        String result = builder.toString();
        if (result.isBlank()) {
            return fallback;
        }
        if (!Character.isJavaIdentifierStart(result.charAt(0))) {
            return fallback + result;
        }
        return result;
    }

    public static String lowerFirst(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    public static int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int cjkCount = 0;
        int asciiCount = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                cjkCount++;
            } else if (!Character.isWhitespace(c)) {
                asciiCount++;
            }
        }
        return cjkCount + Math.max(1, asciiCount / 4);
    }
}
