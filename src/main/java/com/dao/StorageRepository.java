package com.dao;

import com.security.FileCrypto;
import com.security.PasswordHasher;
import com.security.Role;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** All persistence operations use bound parameters and close JDBC resources. */
public final class StorageRepository {
    public record Account(long id, String name, String email, int age, String gender, Role role) { }
    public record FileSummary(String id, String ownerEmail, String filename, String contentType,
                              String description, Instant createdAt) { }
    public record StoredFile(String id, long ownerId, String filename, String contentType,
                             FileCrypto.EncryptedFile encryptedFile) { }
    public record AccessRequest(long id, String fileId, String filename, String ownerEmail,
                                String requesterEmail, String status, Instant createdAt) { }
    public record AuditEntry(long id, String previousHash, String entryHash, String entityType,
                             String entityId, String eventType, String payloadHash,
                             Instant createdAt, boolean valid) { }

    public Optional<Account> authenticate(String email, String password, Role role) throws SQLException {
        String sql = "SELECT id,name,email,age,gender,password_hash,role FROM accounts WHERE email=? AND role=?";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !PasswordHasher.verify(password, rs.getString("password_hash"))) {
                    return Optional.empty();
                }
                return Optional.of(account(rs));
            }
        }
    }

    public Account register(String name, String email, int age, String gender,
                            String passwordHash, Role role) throws SQLException {
        String sql = "INSERT INTO accounts(name,email,age,gender,password_hash,role) VALUES(?,?,?,?,?,?)";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, age);
            ps.setString(4, gender);
            ps.setString(5, passwordHash);
            ps.setString(6, role.name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Account id was not generated");
                return new Account(keys.getLong(1), name, email, age, gender, role);
            }
        }
    }

    public List<Account> accounts(Role role) throws SQLException {
        String sql = "SELECT id,name,email,age,gender,role FROM accounts WHERE role=? ORDER BY name,email";
        List<Account> result = new ArrayList<>();
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(account(rs));
            }
        }
        return result;
    }

    public String saveFile(long ownerId, String filename, String contentType, String description,
                           FileCrypto.EncryptedFile encrypted) throws SQLException {
        String id = UUID.randomUUID().toString();
        try (Connection con = DBConnection.connect()) {
            con.setAutoCommit(false);
            try {
                String sql = "INSERT INTO files(id,owner_id,filename,content_type,description,ciphertext,file_nonce,"
                        + "wrapped_key,key_nonce,ciphertext_sha256) VALUES(?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.setLong(2, ownerId);
                    ps.setString(3, filename);
                    ps.setString(4, contentType);
                    ps.setString(5, description);
                    ps.setBytes(6, encrypted.ciphertext());
                    ps.setBytes(7, encrypted.nonce());
                    ps.setBytes(8, encrypted.wrappedKey());
                    ps.setBytes(9, encrypted.keyNonce());
                    ps.setBytes(10, encrypted.ciphertextSha256());
                    ps.executeUpdate();
                }
                appendAudit(con, "FILE", id, "UPLOAD", HexFormat.of().formatHex(encrypted.ciphertextSha256()));
                con.commit();
                return id;
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                throw new SQLException("Could not store file", e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<FileSummary> ownerFiles(long ownerId) throws SQLException {
        String sql = "SELECT f.id,a.email,f.filename,f.content_type,f.description,f.created_at "
                + "FROM files f JOIN accounts a ON a.id=f.owner_id WHERE f.owner_id=? ORDER BY f.created_at DESC";
        return fileSummaries(sql, ownerId, null);
    }

    public List<FileSummary> searchFiles(String term) throws SQLException {
        String sql = "SELECT f.id,a.email,f.filename,f.content_type,f.description,f.created_at "
                + "FROM files f JOIN accounts a ON a.id=f.owner_id "
                + "WHERE LOWER(f.filename) LIKE ? OR LOWER(f.description) LIKE ? ORDER BY f.created_at DESC LIMIT 100";
        return fileSummaries(sql, null, "%" + term.toLowerCase(java.util.Locale.ROOT) + "%");
    }

    private List<FileSummary> fileSummaries(String sql, Long id, String term) throws SQLException {
        List<FileSummary> result = new ArrayList<>();
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (id != null) ps.setLong(1, id);
            if (term != null) { ps.setString(1, term); ps.setString(2, term); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new FileSummary(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getTimestamp(6).toInstant()));
                }
            }
        }
        return result;
    }

    public Optional<StoredFile> authorizedFile(String fileId, long accountId) throws SQLException {
        String sql = "SELECT f.id,f.owner_id,f.filename,f.content_type,f.ciphertext,f.file_nonce,f.wrapped_key,f.key_nonce,f.ciphertext_sha256 "
                + "FROM files f WHERE f.id=? AND (f.owner_id=? OR EXISTS "
                + "(SELECT 1 FROM access_requests r WHERE r.file_id=f.id AND r.requester_id=? AND r.status='APPROVED'))";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fileId);
            ps.setLong(2, accountId);
            ps.setLong(3, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                byte[] ciphertext = rs.getBytes("ciphertext");
                byte[] expected = rs.getBytes("ciphertext_sha256");
                if (!MessageDigest.isEqual(expected, sha256(ciphertext))) {
                    throw new SecurityException("Stored ciphertext hash does not match");
                }
                return Optional.of(new StoredFile(rs.getString("id"), rs.getLong("owner_id"),
                        rs.getString("filename"), rs.getString("content_type"),
                        new FileCrypto.EncryptedFile(ciphertext, rs.getBytes("file_nonce"),
                                rs.getBytes("wrapped_key"), rs.getBytes("key_nonce"), expected)));
            }
        }
    }

    public boolean deleteFile(String fileId, long ownerId) throws SQLException {
        try (Connection con = DBConnection.connect()) {
            con.setAutoCommit(false);
            try {
                byte[] hash;
                try (PreparedStatement find = con.prepareStatement(
                        "SELECT ciphertext_sha256 FROM files WHERE id=? AND owner_id=? FOR UPDATE")) {
                    find.setString(1, fileId); find.setLong(2, ownerId);
                    try (ResultSet rs = find.executeQuery()) {
                        if (!rs.next()) { con.rollback(); return false; }
                        hash = rs.getBytes(1);
                    }
                }
                try (PreparedStatement delete = con.prepareStatement("DELETE FROM files WHERE id=? AND owner_id=?")) {
                    delete.setString(1, fileId); delete.setLong(2, ownerId); delete.executeUpdate();
                }
                appendAudit(con, "FILE", fileId, "DELETE", HexFormat.of().formatHex(hash));
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                throw new SQLException(e);
            } finally { con.setAutoCommit(true); }
        }
    }

    public boolean requestAccess(String fileId, long requesterId) throws SQLException {
        String sql = "INSERT INTO access_requests(file_id,requester_id,status) "
                + "SELECT f.id,?,'PENDING' FROM files f WHERE f.id=? AND f.owner_id<>? "
                + "ON DUPLICATE KEY UPDATE status=IF(status='DENIED','PENDING',status),updated_at=CURRENT_TIMESTAMP";
        try (Connection con = DBConnection.connect()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, requesterId); ps.setString(2, fileId); ps.setLong(3, requesterId);
                boolean changed = ps.executeUpdate() > 0;
                if (changed) appendAudit(con, "ACCESS_REQUEST", fileId, "REQUEST",
                        sha256Hex(Long.toString(requesterId)));
                con.commit();
                return changed;
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                throw new SQLException(e);
            } finally { con.setAutoCommit(true); }
        }
    }

    public List<AccessRequest> incomingRequests(long ownerId) throws SQLException {
        String sql = "SELECT r.id,r.file_id,f.filename,o.email,u.email,r.status,r.created_at FROM access_requests r "
                + "JOIN files f ON f.id=r.file_id JOIN accounts o ON o.id=f.owner_id "
                + "JOIN accounts u ON u.id=r.requester_id WHERE f.owner_id=? ORDER BY r.created_at DESC";
        return requests(sql, ownerId);
    }

    public List<AccessRequest> outgoingRequests(long requesterId) throws SQLException {
        String sql = "SELECT r.id,r.file_id,f.filename,o.email,u.email,r.status,r.created_at FROM access_requests r "
                + "JOIN files f ON f.id=r.file_id JOIN accounts o ON o.id=f.owner_id "
                + "JOIN accounts u ON u.id=r.requester_id WHERE r.requester_id=? ORDER BY r.created_at DESC";
        return requests(sql, requesterId);
    }

    private List<AccessRequest> requests(String sql, long accountId) throws SQLException {
        List<AccessRequest> result = new ArrayList<>();
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(new AccessRequest(rs.getLong(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getTimestamp(7).toInstant()));
            }
        }
        return result;
    }

    public boolean decideRequest(long requestId, long ownerId, boolean approve) throws SQLException {
        try (Connection con = DBConnection.connect()) {
            con.setAutoCommit(false);
            try {
                String fileId = null;
                try (PreparedStatement find = con.prepareStatement("SELECT r.file_id FROM access_requests r "
                        + "JOIN files f ON f.id=r.file_id WHERE r.id=? AND f.owner_id=? FOR UPDATE")) {
                    find.setLong(1, requestId); find.setLong(2, ownerId);
                    try (ResultSet rs = find.executeQuery()) { if (rs.next()) fileId = rs.getString(1); }
                }
                if (fileId == null) { con.rollback(); return false; }
                String status = approve ? "APPROVED" : "DENIED";
                try (PreparedStatement update = con.prepareStatement(
                        "UPDATE access_requests SET status=?,decided_at=CURRENT_TIMESTAMP WHERE id=?")) {
                    update.setString(1, status); update.setLong(2, requestId); update.executeUpdate();
                }
                appendAudit(con, "ACCESS_REQUEST", Long.toString(requestId), status, sha256Hex(fileId));
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                throw new SQLException(e);
            } finally { con.setAutoCommit(true); }
        }
    }

    public List<AuditEntry> auditEntries() throws SQLException {
        List<AuditEntry> result = new ArrayList<>();
        String expectedPrevious = "0".repeat(64);
        boolean chainValid = true;
        String sql = "SELECT id,previous_hash,entry_hash,entity_type,entity_id,event_type,payload_hash,created_at "
                + "FROM audit_chain ORDER BY id";
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Instant created = rs.getTimestamp(8).toInstant();
                String calculated = auditHash(rs.getString(2), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), created, FileCrypto.auditKeyFromEnvironment());
                boolean valid = chainValid && expectedPrevious.equals(rs.getString(2)) && calculated.equals(rs.getString(3));
                result.add(new AuditEntry(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), created, valid));
                chainValid = valid;
                expectedPrevious = rs.getString(3);
            }
        }
        String head;
        try (Connection con = DBConnection.connect(); PreparedStatement ps = con.prepareStatement(
                "SELECT current_hash FROM audit_head WHERE id=1"); ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("Audit head is missing");
            head = rs.getString(1);
        }
        if (!expectedPrevious.equals(head)) {
            if (result.isEmpty()) throw new SecurityException("Audit chain was truncated");
            AuditEntry last = result.get(result.size() - 1);
            result.set(result.size() - 1, new AuditEntry(last.id(), last.previousHash(), last.entryHash(),
                    last.entityType(), last.entityId(), last.eventType(), last.payloadHash(), last.createdAt(), false));
        }
        return result;
    }

    public void recordDownload(String fileId, long accountId) throws SQLException {
        try (Connection con = DBConnection.connect()) {
            con.setAutoCommit(false);
            try {
                appendAudit(con, "FILE", fileId, "DOWNLOAD", sha256Hex(Long.toString(accountId)));
                con.commit();
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                throw new SQLException(e);
            } finally { con.setAutoCommit(true); }
        }
    }

    private static void appendAudit(Connection con, String entityType, String entityId,
                                    String eventType, String payloadHash) throws SQLException {
        String previous;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT current_hash FROM audit_head WHERE id=1 FOR UPDATE");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("Audit head is missing");
            previous = rs.getString(1);
        }
        // MySQL TIMESTAMP(6) stores microseconds; hash exactly the value that is persisted.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        String hash = auditHash(previous, entityType, entityId, eventType, payloadHash, now,
                FileCrypto.auditKeyFromEnvironment());
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO audit_chain(previous_hash,entry_hash,"
                + "entity_type,entity_id,event_type,payload_hash,created_at) VALUES(?,?,?,?,?,?,?)")) {
            ps.setString(1, previous); ps.setString(2, hash); ps.setString(3, entityType);
            ps.setString(4, entityId); ps.setString(5, eventType); ps.setString(6, payloadHash);
            ps.setTimestamp(7, Timestamp.from(now)); ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE audit_head SET current_hash=? WHERE id=1")) {
            ps.setString(1, hash); ps.executeUpdate();
        }
    }

    public static String auditHash(String previous, String entityType, String entityId,
                                   String eventType, String payloadHash, Instant createdAt, SecretKey auditKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(auditKey.getEncoded(), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(String.join("|", previous, entityType, entityId,
                    eventType, payloadHash, createdAt.toString()).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Audit HMAC failed", e);
        }
    }

    private static Account account(ResultSet rs) throws SQLException {
        return new Account(rs.getLong("id"), rs.getString("name"), rs.getString("email"),
                rs.getInt("age"), rs.getString("gender"), Role.parse(rs.getString("role")));
    }

    private static byte[] sha256(byte[] value) {
        try { return MessageDigest.getInstance("SHA-256").digest(value); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    private static String sha256Hex(String value) {
        return HexFormat.of().formatHex(sha256(value.getBytes(StandardCharsets.UTF_8)));
    }
}
