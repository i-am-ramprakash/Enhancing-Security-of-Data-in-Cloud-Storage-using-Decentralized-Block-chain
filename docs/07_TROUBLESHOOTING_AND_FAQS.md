# 07 — Troubleshooting & FAQs

## 1. Frequently Encountered Issues & Solutions

---

### Issue 1: `java.sql.SQLException: No suitable driver`
#### **Symptom:**
Tomcat log shows `java.lang.RuntimeException: Failed to get driver instance... Caused by: java.sql.SQLException: No suitable driver` when attempting to log in or query the database.

#### **Root Cause:**
In Tomcat container environments, JDBC drivers inside `WEB-INF/lib` are not automatically registered with `DriverManager` unless HikariCP is given the explicit driver class name.

#### **Solution:**
Ensure `DBConnection.java` has explicit driver registration:
```java
HikariConfig config = new HikariConfig();
config.setDriverClassName("com.mysql.cj.jdbc.Driver");
config.setJdbcUrl("jdbc:mysql://localhost:3306/secure_cloud?serverTimezone=UTC");
```

---

### Issue 2: `IllegalStateException: APP_MASTER_KEY must be a Base64-encoded 256-bit key`
#### **Symptom:**
Application throws `IllegalStateException` on startup or file upload.

#### **Root Cause:**
`APP_MASTER_KEY` or `APP_AUDIT_KEY` environment variable is either missing or decodes to a size other than 32 bytes (256 bits).

#### **Solution:**
Generate valid 32-byte Base64 keys using PowerShell:
```powershell
$b = New-Object byte[] 32; (New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($b); [Convert]::ToBase64String($b)
```
Add both keys into Tomcat's `bin/setenv.bat` (Windows) or `bin/setenv.sh` (Linux/macOS):
```bat
set "APP_MASTER_KEY=YOUR_32_BYTE_BASE64_KEY_1"
set "APP_AUDIT_KEY=YOUR_32_BYTE_BASE64_KEY_2"
```

---

### Issue 3: MySQL Workbench 8.0 Crashes Immediately Upon Connection
#### **Symptom:**
Opening MySQL Workbench 8.0 and clicking the MySQL 9.1 connection instantly closes Workbench.

#### **Root Cause:**
MySQL Workbench 8.0 has a known compatibility bug with MySQL Server 9.1 (`authentication_mysql_native_password` plugin removal).

#### **Solution:**
Import `schema.sql` and run queries directly via **PowerShell / CMD** using `mysql.exe`:
```powershell
Get-Content schema.sql | & "C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -p
```
*(Or use DBeaver Community edition, which is fully compatible with MySQL 9.1).*

---

### Issue 4: PowerShell Error `The '<' operator is reserved for future use`
#### **Symptom:**
Running `mysql -u root -p < schema.sql` in PowerShell throws `ParserError: RedirectionNotSupported`.

#### **Root Cause:**
PowerShell does not support `<` input redirection.

#### **Solution:**
Use `Get-Content` piped into `mysql.exe`:
```powershell
Get-Content schema.sql | & "C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -p
```

---

### Issue 5: VS Code Displays 190 "Problems" / `The import javax.servlet cannot be resolved`
#### **Symptom:**
VS Code shows red squiggles under `import javax.servlet.*`, even though `mvn clean compile` builds with `BUILD SUCCESS`.

#### **Root Cause:**
VS Code's Java extension language server cache has lost synchronization with Maven dependencies in `pom.xml`.

#### **Solution:**
1. Press `Ctrl + Shift + P` in VS Code.
2. Type `Java: Clean Java Language Server Workspace` and select **Restart and Clean**.
3. Or right-click `pom.xml` -> **Java: Update Project Configuration**.

---

### Issue 6: `HTTP 403 Forbidden: Invalid CSRF token`
#### **Symptom:**
Form submission returns HTTP 403.

#### **Root Cause:**
The `POST` request did not contain the `csrfToken` parameter or the session expired.

#### **Solution:**
Ensure all HTML `<form method="POST">` elements contain:
```html
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
```
