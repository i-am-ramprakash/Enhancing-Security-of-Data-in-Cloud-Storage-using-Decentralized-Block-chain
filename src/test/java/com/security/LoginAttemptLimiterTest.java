package com.security;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptLimiterTest {
    @Test void blocksAfterFiveFailuresAndClearsAfterSuccess() {
        String key = UUID.randomUUID().toString();
        assertTrue(LoginAttemptLimiter.allowed(key));
        for (int i = 0; i < 5; i++) LoginAttemptLimiter.failed(key);
        assertFalse(LoginAttemptLimiter.allowed(key));
        LoginAttemptLimiter.succeeded(key);
        assertTrue(LoginAttemptLimiter.allowed(key));
    }
}
