package com.security;

public enum Role {
    USER, OWNER, ADMIN;

    public static Role parse(String value) {
        return Role.valueOf(value.toUpperCase(java.util.Locale.ROOT));
    }
}
