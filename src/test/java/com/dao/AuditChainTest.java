package com.dao;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import static org.junit.jupiter.api.Assertions.*;

class AuditChainTest {
    @Test void hashCommitsToEveryFieldAndPreviousEntry() {
        Instant time = Instant.parse("2026-01-01T00:00:00Z");
        var key = new SecretKeySpec(new byte[32], "AES");
        String previous = "0".repeat(64);
        String hash = StorageRepository.auditHash(previous, "FILE", "id", "UPLOAD", "payload", time, key);
        assertEquals(hash, StorageRepository.auditHash(previous, "FILE", "id", "UPLOAD", "payload", time, key));
        assertNotEquals(hash, StorageRepository.auditHash(previous, "FILE", "other", "UPLOAD", "payload", time, key));
        assertNotEquals(hash, StorageRepository.auditHash("1".repeat(64), "FILE", "id", "UPLOAD", "payload", time, key));
        byte[] differentKey = new byte[32];
        differentKey[0] = 1;
        assertNotEquals(hash, StorageRepository.auditHash(previous, "FILE", "id", "UPLOAD", "payload", time,
                new SecretKeySpec(differentKey, "AES")));
    }
}
