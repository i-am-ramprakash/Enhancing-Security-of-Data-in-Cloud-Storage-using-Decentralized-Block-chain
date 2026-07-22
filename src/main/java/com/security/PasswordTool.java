package com.security;

import java.io.Console;

/** Creates an admin-compatible password hash without exposing the password in process arguments. */
public final class PasswordTool {
    private PasswordTool() { }

    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) throw new IllegalStateException("Run PasswordTool from an interactive terminal");
        char[] first = console.readPassword("Password (12+ characters): ");
        char[] second = console.readPassword("Confirm password: ");
        try {
            if (!java.security.MessageDigest.isEqual(new String(first).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    new String(second).getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            console.writer().println(PasswordHasher.hash(new String(first)));
        } finally {
            java.util.Arrays.fill(first, '\0');
            java.util.Arrays.fill(second, '\0');
        }
    }
}
