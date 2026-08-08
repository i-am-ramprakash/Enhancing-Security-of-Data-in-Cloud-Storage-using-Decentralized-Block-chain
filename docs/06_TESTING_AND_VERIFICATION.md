# 06 — Testing & Verification

## 1. Testing Framework Overview

The project incorporates an automated test suite powered by **JUnit 5 (Jupiter 5.14.0)** located under `src/test/java/`. The unit tests validate core security primitives, encryption integrity, rate limiting, and input sanitization without requiring an active database or web server.

---

## 2. Test Classes Breakdown

| Test Class | Package | Tested Target Component | Key Verification Assertions |
|---|---|---|---|
| `AuditChainTest` | `com.dao` | Cryptographic Audit Ledger | Verifies entry hash generation, previous hash chaining consistency, and tamper detection when payload is mutated. |
| `FileCryptoTest` | `com.security` | AES-256-GCM Envelope Encryption | Tests plaintext encryption, successful decryption, authentication tag validation, and failure on corrupted ciphertext. |
| `InputTest` | `com.security` | Input Sanitization | Validates XSS HTML entity encoding and strict filename validation against path traversal attacks. |
| `LoginAttemptLimiterTest` | `com.security` | Rate Limiter | Verifies attempt tracking, lockout threshold enforcement after 5 failed attempts, and reset upon successful login. |
| `PasswordHasherTest` | `com.security` | PBKDF2 Password Engine | Verifies hash format (`pbkdf2-sha256$...`), correct password matching, and rejection of invalid passwords. |

---

## 3. Running Unit Tests via Maven

Execute the following commands in PowerShell or terminal at the project root:

### A. Run Full Test Suite
```bash
mvn clean test
```
*Expected Output:*
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.dao.AuditChainTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.security.FileCryptoTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.security.InputTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.security.LoginAttemptLimiterTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.security.PasswordHasherTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### B. Compile Test Classes Only
```bash
mvn test-compile
```

---

## 4. Manual Security Verification Checklist

| Security Control | Manual Verification Step | Expected Result |
|---|---|---|
| **CSRF Protection** | Send `POST /FileUpload` without `csrfToken` parameter using Postman | Server returns **HTTP 403 Forbidden ("Invalid CSRF token")** |
| **RBAC Enforcement** | Access `/DataOwnerHome.jsp` while logged in as a `USER` | Server redirects or returns **HTTP 403 Forbidden** |
| **Session Security** | Access `/UploadFile.jsp` with no active HTTP session | Server redirects to `/index.html` |
| **SQL Injection Defense** | Input `' OR '1'='1` in login forms | Login fails gracefully without SQL exception |
| **Security Headers** | Inspect HTTP response headers in browser Developer Tools (F12) | Headers present: `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Content-Security-Policy` |
| **Audit Ledger** | Navigate to `/BlocksData.jsp` as `ADMIN` | Audit chain status displays green/valid status across all historical block entries |
