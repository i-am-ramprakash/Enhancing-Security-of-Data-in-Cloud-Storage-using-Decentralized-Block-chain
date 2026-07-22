package com.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InputTest {
    @Test void normalizesEmailsAndFilenames() {
        assertEquals("user@example.com", Input.email(" User@Example.COM "));
        assertEquals("report.pdf", Input.safeFilename("C:\\fakepath\\report.pdf"));
    }

    @Test void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> Input.email("not-an-email"));
        assertThrows(IllegalArgumentException.class, () -> Input.age("5"));
        assertThrows(IllegalArgumentException.class, () -> Input.safeFilename(".."));
    }
}
