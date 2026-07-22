package com.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/** Application-owned, bounded JDBC connection pool. */
public final class DBConnection {
    private static volatile HikariDataSource dataSource;

    private DBConnection() { }

    public static Connection connect() throws SQLException {
        return dataSource().getConnection();
    }

    private static HikariDataSource dataSource() {
        HikariDataSource current = dataSource;
        if (current == null) {
            synchronized (DBConnection.class) {
                current = dataSource;
                if (current == null) {
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(env("DB_URL", "jdbc:mysql://localhost:3306/secure_cloud?serverTimezone=UTC"));
                    config.setUsername(env("DB_USER", "secure_cloud"));
                    config.setPassword(requiredEnv("DB_PASSWORD"));
                    config.setMaximumPoolSize(intEnv("DB_POOL_SIZE", 10));
                    config.setMinimumIdle(1);
                    config.setConnectionTimeout(10_000);
                    config.setValidationTimeout(3_000);
                    config.setPoolName("secure-cloud-db");
                    dataSource = current = new HikariDataSource(config);
                }
            }
        }
        return current;
    }

    public static void close() {
        HikariDataSource current = dataSource;
        if (current != null) {
            current.close();
            dataSource = null;
        }
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be configured");
        }
        return value;
    }

    private static int intEnv(String name, int fallback) {
        try {
            return Integer.parseInt(env(name, Integer.toString(fallback)));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
