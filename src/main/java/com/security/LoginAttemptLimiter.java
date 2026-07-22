package com.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Per-node login throttling. Put a shared edge rate limiter in front of multi-node deployments. */
public final class LoginAttemptLimiter {
    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final ConcurrentHashMap<String, Attempt> ATTEMPTS = new ConcurrentHashMap<>();

    private record Attempt(int failures, Instant firstFailure) { }
    private LoginAttemptLimiter() { }

    public static boolean allowed(String key) {
        Attempt attempt = ATTEMPTS.get(key);
        if (attempt == null) return true;
        if (attempt.firstFailure().plus(WINDOW).isBefore(Instant.now())) {
            ATTEMPTS.remove(key, attempt);
            return true;
        }
        return attempt.failures() < MAX_FAILURES;
    }

    public static void failed(String key) {
        Instant now = Instant.now();
        ATTEMPTS.compute(key, (ignored, old) -> old == null || old.firstFailure().plus(WINDOW).isBefore(now)
                ? new Attempt(1, now) : new Attempt(old.failures() + 1, old.firstFailure()));
        if (ATTEMPTS.size() > 10_000) ATTEMPTS.entrySet().removeIf(e -> e.getValue().firstFailure().plus(WINDOW).isBefore(now));
    }

    public static void succeeded(String key) { ATTEMPTS.remove(key); }
}
