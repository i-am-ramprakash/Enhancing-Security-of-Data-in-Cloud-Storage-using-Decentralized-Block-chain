# Secure Cloud Storage

A Java 17 Servlet/JSP application for encrypted, owner-approved file sharing. Version 2 replaces the original prototype's plaintext passwords, SQL concatenation, URL keys, AES-ECB, and unverifiable proof-of-work records.

## Security model

- Passwords use PBKDF2-HMAC-SHA-256 with per-account random salts and 310,000 iterations.
- Every file gets a random 256-bit AES data key and 96-bit nonce. AES-GCM provides confidentiality and integrity.
- Data keys are wrapped by a 256-bit deployment master key; raw keys are never stored or sent to users.
- Downloads require an authenticated owner, administrator, or an approved request belonging to the current user.
- Mutations are POST-only and require a session-bound CSRF token.
- SQL uses bound parameters, pooled connections, transactions, foreign keys, and ownership predicates.
- Uploads, downloads, requests, and access decisions enter an HMAC-SHA-256 append-only audit chain.

The audit chain detects edits, reordering, and truncation while its independent HMAC key and audit head remain trustworthy. Detecting a complete database rollback requires anchoring periodic audit heads in an external immutable system. This design is deliberately not described as decentralized blockchain; that would require independent nodes and consensus outside this application.

## Requirements and setup

Use JDK 17+, Maven 3.9+, MySQL 8.4+, a Servlet 4 container such as Tomcat 9, and HTTPS outside localhost.

1. Import `schema.sql`. The insecure legacy dump and compiled artifacts have been removed. Create the least-privilege user shown in `schema.sql`.
2. Configure `DB_URL` (optional), `DB_USER` (optional), `DB_PASSWORD` (required), `DB_POOL_SIZE` (optional), `APP_MASTER_KEY` (required Base64 encoding of 32 random bytes), and a separate `APP_AUDIT_KEY` with the same format in Tomcat's environment. Generate each key independently with `openssl rand -base64 32`, store them in a secret manager, and back them up.
3. Build classes and generate the first administrator's password hash interactively:

   ```text
   mvn clean compile
   java -cp target/classes com.security.PasswordTool
   ```

   Insert the resulting hash using the commented administrator template in `schema.sql`.
4. Run `mvn clean verify`, then deploy `target/secure-cloud-storage.war`.

The new schema is intentionally incompatible with the insecure legacy dump. Legacy AES-ECB files require an isolated one-time migration followed by immediate AES-GCM re-encryption; never place legacy data in the live schema.
