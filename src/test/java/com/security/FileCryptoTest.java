package com.security;

import org.junit.jupiter.api.Test;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class FileCryptoTest {
    private final SecretKeySpec master = new SecretKeySpec(new byte[32], "AES");

    @Test void roundTripUsesFreshRandomness() {
        byte[] source = "confidential content".getBytes(StandardCharsets.UTF_8);
        var first = FileCrypto.encrypt(source, master);
        var second = FileCrypto.encrypt(source, master);
        assertArrayEquals(source, FileCrypto.decrypt(first, master));
        assertFalse(java.util.Arrays.equals(first.ciphertext(), second.ciphertext()));
        assertFalse(java.util.Arrays.equals(first.nonce(), second.nonce()));
    }

    @Test void rejectsTampering() {
        var encrypted = FileCrypto.encrypt("data".getBytes(StandardCharsets.UTF_8), master);
        encrypted.ciphertext()[0] ^= 1;
        assertThrows(SecurityException.class, () -> FileCrypto.decrypt(encrypted, master));
    }
}
