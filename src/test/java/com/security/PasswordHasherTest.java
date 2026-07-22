package com.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test void roundTripAndSaltUniqueness() {
        String first = PasswordHasher.hash("correct horse battery staple");
        String second = PasswordHasher.hash("correct horse battery staple");
        assertNotEquals(first, second);
        assertTrue(PasswordHasher.verify("correct horse battery staple", first));
        assertFalse(PasswordHasher.verify("wrong password", first));
    }

    @Test void rejectsWeakPasswordsAndMalformedHashes() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash("short"));
        assertFalse(PasswordHasher.verify("anything at all", "not-a-hash"));
    }
}
