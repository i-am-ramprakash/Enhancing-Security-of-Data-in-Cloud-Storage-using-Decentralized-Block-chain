# 09 — Tools & Technology Stack

## 1. Overview

This document provides a deep, comprehensive breakdown of every tool, library, programming language, and framework used in **Secure Cloud Storage**. Each section explains **what the technology is**, **how it is used in this project**, and **hands-on code or command examples** of how it is performed.

---

## 2. Java 17 LTS / Java 21 Runtime

### What is it?
Java is a strongly-typed, object-oriented programming language and runtime platform. Java 17 (and Java 21) are Long-Term Support (LTS) releases introducing modern language features such as immutable **Records**, sealed classes, and enhanced security performance.

### How it is used in this project:
- Core business logic, cryptographic processing, and DAO database management.
- Uses **Java 17 Records** (`public record Account(...)`, `public record StoredFile(...)`) for clean data structures.

### Code Example:
```java
// Using Java 17 Record feature for immutable DTOs (com.dao.StorageRepository)
public record Account(long id, String name, String email, int age, String gender, Role role) { }

// Instantization:
Account acc = new Account(1L, "Alice", "alice@example.com", 28, "Female", Role.OWNER);
System.out.println(acc.email()); // Output: alice@example.com
```

---

## 3. Apache Maven 3.9+

### What is it?
Apache Maven is a build automation and software project management tool for Java. It uses a Project Object Model (`pom.xml`) to manage compilation, dependencies, testing, and packaging into web archives (`.war`).

### How it is used in this project:
- Manages project dependencies (`javax.servlet-api`, `HikariCP`, `mysql-connector-j`, `junit-jupiter`).
- Compiles Java source files and packages the final WAR file (`secure-cloud-storage.war`).

### Command Examples:
```bash
# 1. Compile Java source code
mvn clean compile

# 2. Run unit tests
mvn test

# 3. Build production WAR package
mvn clean verify

# 4. Resolve and download dependencies
mvn dependency:resolve
```

---

## 4. Java Servlets 4.0 & JSP (JavaServer Pages)

### What is it?
Java Servlets are server-side Java components that handle HTTP requests and responses. JSPs are template pages combining HTML with Java tag libraries (JSTL) for dynamic frontend rendering.

### How it is used in this project:
- **Servlets (`@WebServlet`)**: Handle backend logic for login, registration, file upload, file downloading, and request approvals.
- **Filters (`@WebFilter("/*")`)**: `SecurityFilter` intercepts every request to enforce CSRF validation, HTTP security headers, and Role-Based Access Control (RBAC).

### Code Example:
```java
// Servlet handling File Upload (com.servlets.FileUpload)
@WebServlet("/FileUpload")
@MultipartConfig
public class FileUpload extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part filePart = request.getPart("file");
        byte[] fileBytes = filePart.getInputStream().readAllBytes();
        
        // Encrypt file bytes using FileCrypto
        SecretKey masterKey = FileCrypto.masterKeyFromEnvironment();
        FileCrypto.EncryptedFile encrypted = FileCrypto.encrypt(fileBytes, masterKey);
        
        // Save to Database via DAO
        repository.saveFile(ownerId, filename, contentType, description, encrypted);
        response.sendRedirect("ViewOwnFiles.jsp");
    }
}
```

---

## 5. MySQL 8.4+ / MySQL 9.1+ & InnoDB Engine

### What is it?
MySQL is an open-source Relational Database Management System (RDBMS). The **InnoDB** storage engine supports ACID compliance, foreign key constraints, binary columns (`LONGBLOB`, `VARBINARY`), and transaction rollbacks.

### How it is used in this project:
- Persists user accounts, encrypted file ciphertexts, data key envelopes, access permissions, and audit logs across 5 tables (`accounts`, `files`, `access_requests`, `audit_chain`, `audit_head`).

### SQL Command Examples:
```sql
-- Import database schema (PowerShell)
Get-Content schema.sql | & "C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -p

-- View table status and accounts
USE secure_cloud;
SHOW TABLES;
SELECT id, name, email, role FROM accounts;
```

---

## 6. HikariCP Connection Pool (v6.3.3)

### What is it?
HikariCP is a high-performance, lightweight JDBC connection pool framework. Instead of creating and destroying a new database connection for every HTTP request (which causes high overhead), HikariCP maintains a pool of pre-established, reusable connections.

### How it is used in this project:
- Configured in `DBConnection.java` to serve JDBC connections efficiently to `StorageRepository`.

### Code Example:
```java
// HikariCP Initialization (com.dao.DBConnection)
HikariConfig config = new HikariConfig();
config.setDriverClassName("com.mysql.cj.jdbc.Driver");
config.setJdbcUrl("jdbc:mysql://localhost:3306/secure_cloud?serverTimezone=UTC");
config.setUsername("root");
config.setPassword("root");
config.setMaximumPoolSize(10);

HikariDataSource dataSource = new HikariDataSource(config);
Connection con = dataSource.getConnection(); // Obtains reusable connection from pool
```

---

## 7. Java Cryptography Extension (JCE): AES-256-GCM & PBKDF2

### What is it?
The Java Cryptography Extension (JCE) provides official Java APIs for encryption, key generation, and message hashing without requiring third-party libraries.

### How it is used in this project:
1. **`AES/GCM/NoPadding`**: Encrypts uploaded file data and wraps data keys (`FileCrypto.java`).
2. **`PBKDF2WithHmacSHA256`**: Hashes user passwords with 310,000 iterations and 16-byte random salts (`PasswordHasher.java`).
3. **`HmacSHA256`**: Computes append-only audit chain hashes (`APP_AUDIT_KEY`).

### Code Examples:
```java
// 1. Password Hashing (PBKDF2)
SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 310000, 256);
byte[] hash = factory.generateSecret(spec).getEncoded();

// 2. AES-256-GCM File Encryption
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce); // 128-bit Auth Tag, 96-bit Nonce
cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
byte[] ciphertext = cipher.doFinal(plaintextBytes);
```

---

## 8. Apache Tomcat 9.0 / 10 Web Server

### What is it?
Apache Tomcat is an open-source Java Servlet container that compiles JSPs and executes Java Servlets to serve HTTP requests.

### How it is used in this project:
- Hosts the deployed `secure-cloud-storage.war` application and executes `setenv.bat` / `setenv.sh` to inject secret environment variables (`APP_MASTER_KEY`, `APP_AUDIT_KEY`, `DB_PASSWORD`).

### Command Example:
```powershell
# Navigate to Tomcat bin directory and launch server
cd D:\rama\applications\apache-tomcat-9.0.120-windows-x64\apache-tomcat-9.0.120\bin
.\startup.bat
```

---

## 9. JUnit 5 (Jupiter 5.14.0)

### What is it?
JUnit 5 is the standard unit testing framework for Java apps. It allows developers to write automated test cases with assertions.

### How it is used in this project:
- Unit tests in `src/test/java/` automatically verify encryption, decryption, rate limiting, and password hashing primitives.

### Code Example:
```java
// Unit Test Assertion Example (com.security.PasswordHasherTest)
@Test
void testPasswordVerification() {
    String rawPassword = "AdminPass123!";
    String hash = PasswordHasher.hash(rawPassword);
    
    // Assert correct password matches
    assertTrue(PasswordHasher.verify("AdminPass123!", hash));
    
    // Assert wrong password fails
    assertFalse(PasswordHasher.verify("WrongPassword!", hash));
}
```
