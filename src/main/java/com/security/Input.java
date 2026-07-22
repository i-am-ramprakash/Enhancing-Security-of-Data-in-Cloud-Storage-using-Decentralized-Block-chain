package com.security;

import java.util.Locale;
import java.util.regex.Pattern;

public final class Input {
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$", Pattern.CASE_INSENSITIVE);

    private Input() { }

    public static String email(String value) {
        String result = text(value, 254).toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(result).matches()) throw new IllegalArgumentException("A valid email is required");
        return result;
    }

    public static String text(String value, int max) {
        if (value == null) throw new IllegalArgumentException("Required value is missing");
        String result = value.strip();
        if (result.isEmpty() || result.length() > max) throw new IllegalArgumentException("Invalid value length");
        return result;
    }

    public static int age(String value) {
        try {
            int age = Integer.parseInt(value);
            if (age < 13 || age > 120) throw new IllegalArgumentException("Age must be between 13 and 120");
            return age;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be a number");
        }
    }

    public static String safeFilename(String value) {
        String name = text(value, 255).replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1).replaceAll("[\\r\\n\\u0000]", "_");
        if (name.equals(".") || name.equals("..") || name.isBlank()) throw new IllegalArgumentException("Invalid filename");
        return name;
    }
}
