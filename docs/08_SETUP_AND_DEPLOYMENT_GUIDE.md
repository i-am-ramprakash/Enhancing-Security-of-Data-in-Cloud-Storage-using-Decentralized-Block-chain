# 08 — Setup & Deployment Guide

## 1. Prerequisites & Environment Requirements

Before setting up the project, ensure your development or deployment environment meets the following specifications:

| Technology | Minimum Version | Recommended Version |
|---|---|---|
| **Java Development Kit (JDK)** | Java 17 | JDK 21 LTS |
| **Apache Maven** | Maven 3.9.0 | Maven 3.9+ |
| **Database Server** | MySQL 8.4 | MySQL 8.4+ / 9.1+ |
| **Web Container** | Apache Tomcat 9.0 | Tomcat 9.0.86+ or Tomcat 10 |
| **Operating System** | Windows 10/11, Ubuntu 22.04 LTS, macOS | Windows / Linux Server |

---

## 2. Step 1: Database Initialization

1. Open your terminal or PowerShell and navigate to the project directory:
   ```bash
   cd Enhancing-Security-of-Data-in-Cloud-Storage-using-Decentralized-Block-chain
   ```

2. Import `schema.sql` into MySQL:
   - **PowerShell (Windows):**
     ```powershell
     Get-Content schema.sql | & "C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -p
     ```
   - **Linux / macOS / Bash:**
     ```bash
     mysql -u root -p < schema.sql
     ```

3. (Optional) Create a dedicated, restricted database user:
   ```sql
   CREATE USER IF NOT EXISTS 'secure_cloud'@'localhost' IDENTIFIED BY 'MyDatabasePassword123!';
   GRANT SELECT, INSERT, UPDATE, DELETE ON secure_cloud.* TO 'secure_cloud'@'localhost';
   FLUSH PRIVILEGES;
   ```

---

## 3. Step 2: Generate Cryptographic Keys & Admin Password

1. **Compile Java classes:**
   ```bash
   mvn clean compile
   ```

2. **Generate Master Key (`APP_MASTER_KEY`) and Audit Key (`APP_AUDIT_KEY`):**
   Run the following PowerShell script to generate two Base64-encoded 256-bit random keys:
   ```powershell
   # Master Key:
   $b1 = New-Object byte[] 32; (New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($b1); [Convert]::ToBase64String($b1)

   # Audit Key:
   $b2 = New-Object byte[] 32; (New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($b2); [Convert]::ToBase64String($b2)
   ```

3. **Generate Admin User Password Hash:**
   Run the interactive `PasswordTool`:
   ```bash
   java -cp target/classes com.security.PasswordTool
   ```
   - Enter your administrator password (e.g. `AdminPass123!`).
   - Copy the generated hash starting with `pbkdf2-sha256$310000$...`.

4. **Insert Administrator Into MySQL:**
   Run this query in MySQL CLI or DBeaver:
   ```sql
   USE secure_cloud;
   INSERT INTO accounts(name, email, age, gender, password_hash, role)
   VALUES ('Administrator', 'admin@example.com', 25, 'Prefer not to say', 'YOUR_PBKDF2_HASH_HERE', 'ADMIN');
   ```

---

## 4. Step 3: Configure Tomcat Environment Variables

Go to your Tomcat `bin/` directory and create/edit **`setenv.bat`** (Windows) or **`setenv.sh`** (Linux):

### For Windows (`bin/setenv.bat`):
```bat
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "CATALINA_HOME=D:\rama\applications\apache-tomcat-9.0.120-windows-x64\apache-tomcat-9.0.120"
set "DB_URL=jdbc:mysql://localhost:3306/secure_cloud?serverTimezone=UTC"
set "DB_USER=root"
set "DB_PASSWORD=YOUR_MYSQL_ROOT_PASSWORD"
set "APP_MASTER_KEY=YOUR_MASTER_KEY_BASE64"
set "APP_AUDIT_KEY=YOUR_AUDIT_KEY_BASE64"
```

### For Linux/macOS (`bin/setenv.sh`):
```bash
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
export CATALINA_HOME="/opt/tomcat"
export DB_URL="jdbc:mysql://localhost:3306/secure_cloud?serverTimezone=UTC"
export DB_USER="secure_cloud"
export DB_PASSWORD="YOUR_MYSQL_ROOT_PASSWORD"
export APP_MASTER_KEY="YOUR_MASTER_KEY_BASE64"
export APP_AUDIT_KEY="YOUR_AUDIT_KEY_BASE64"
```
Ensure permissions are set: `chmod +x bin/setenv.sh`.

---

## 5. Step 4: Build & Deploy Web Application

1. **Build the WAR package:**
   ```bash
   mvn clean verify
   ```
2. **Deploy WAR file to Tomcat `webapps/`:**
   - **PowerShell:**
     ```powershell
     Copy-Item target\secure-cloud-storage.war D:\rama\applications\apache-tomcat-9.0.120-windows-x64\apache-tomcat-9.0.120\webapps\ -Force
     ```
   - **Linux:**
     ```bash
     cp target/secure-cloud-storage.war /opt/tomcat/webapps/
     ```

---

## 6. Step 5: Launch Application & Verify

1. **Start Apache Tomcat:**
   - **Windows:** `.\bin\startup.bat`
   - **Linux:** `./bin/startup.sh`

2. **Open Web Browser:**
   Go to: `http://localhost:8080/secure-cloud-storage/`

3. **Sign In:**
   - **Email:** `admin@example.com`
   - **Password:** `AdminPass123!`

---

## 7. Production Hardening Checklist

For production deployment:
- [ ] **HTTPS / TLS:** Terminate TLS on port 443 with a valid Certificate Authority (CA) SSL certificate.
- [ ] **Secret Manager:** Store `APP_MASTER_KEY` and `APP_AUDIT_KEY` in AWS Secrets Manager, HashiCorp Vault, or Azure Key Vault rather than plaintext files.
- [ ] **Audit Head Anchoring:** Anchor periodic audit head values (`audit_head.current_hash`) into an external immutable ledger or cloud block storage to prevent complete database rollback attacks.
