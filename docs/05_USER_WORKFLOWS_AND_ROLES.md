# 05 — User Workflows & Roles

## 1. User Roles Overview

The system categorizes users into three distinct roles, each tailored with specific interface workflows:

```mermaid
graph LR
    System[Secure Cloud Storage System]
    System --> ADMIN[Administrator]
    System --> OWNER[Data Owner]
    System --> USER[Data User]

    ADMIN -->|Audit & Monitoring| AdminFeatures["View Accounts | Verify Cryptographic Audit Chain"]
    OWNER -->|Data Governance| OwnerFeatures["Upload Encrypted Files | Manage Access Requests | Grant Permissions"]
    USER -->|Data Access| UserFeatures["Search Shared Files | Submit Access Requests | Download Approved Files"]
```

---

## 2. Data Owner Journey & Workflows

Data Owners are content creators who upload files into the cloud storage system and maintain full authority over access permissions.

```mermaid
sequenceDiagram
    autonumber
    actor Owner as Data Owner
    participant Web as Web Interface
    participant Servlet as FileUpload / Approve Servlet
    participant Crypto as FileCrypto Engine
    participant DB as MySQL Database

    Owner->>Web: 1. Register & Log In (/DataOwnerLogin.jsp)
    Owner->>Web: 2. Select File & Click Upload (/UploadFile.jsp)
    Web->>Servlet: 3. POST /FileUpload (file, description)
    Servlet->>Crypto: 4. Encrypt File Payload (AES-256-GCM)
    Crypto-->>Servlet: 5. Return Encrypted Ciphertext + Wrapped Key
    Servlet->>DB: 6. Insert Into `files` Table + Append Audit Log
    
    Note over Owner, DB: Access Request Handling
    Owner->>Web: 7. View Received Requests (/Request.jsp)
    Owner->>Web: 8. Click "Approve" or "Deny"
    Web->>Servlet: 9. POST /Approve (requestId, status)
    Servlet->>DB: 10. Update `access_requests` to APPROVED + Audit Log
```

### Key Owner Screens:
- **Registration:** `DataOwnerRegister.jsp` -> `/OwnerReg`
- **Dashboard:** `DataOwnerHome.jsp`
- **File Upload:** `UploadFile.jsp` -> `/FileUpload`
- **View Owned Files:** `ViewOwnFiles.jsp`
- **Pending Access Requests:** `Request.jsp` -> `/Approve`

---

## 3. Data User Journey & Workflows

Data Users search the cloud repository, submit access requests for files, and download/decrypt files once approved by the file owner.

```mermaid
sequenceDiagram
    autonumber
    actor User as Data User
    participant Web as Web Interface
    participant Servlet as SendRequest / ViewData Servlet
    participant Crypto as FileCrypto Engine
    participant DB as MySQL Database

    User->>Web: 1. Register & Log In (/DataUserLogin.jsp)
    User->>Web: 2. Search Available Files (/SearchFile.jsp)
    User->>Web: 3. Click "Send Request"
    Web->>Servlet: 4. POST /SendRequest (fileId)
    Servlet->>DB: 5. Insert Into `access_requests` (PENDING)
    
    Note over User, DB: Downloading Approved File
    User->>Web: 6. Check Request Status (/Response.jsp)
    User->>Web: 7. Click "Download File" (If APPROVED)
    Web->>Servlet: 8. GET /ViewData?fileId=...
    Servlet->>DB: 9. Verify APPROVED status in `access_requests`
    Servlet->>Crypto: 10. Decrypt File Data (FileCrypto.decrypt)
    Crypto-->>Servlet: 11. Plaintext Bytes
    Servlet-->>User: 12. Downloaded File (HTTP Stream)
```

### Key User Screens:
- **Registration:** `DataUserRegister.jsp` -> `/UserReg`
- **Dashboard:** `DataUserHome.jsp`
- **Search Files:** `SearchFile.jsp` / `SearchResult.jsp`
- **Submit Request:** `/SendRequest`
- **View Approved Responses:** `Response.jsp` -> `/ViewData`

---

## 4. Administrator Journey & Workflows

Administrators oversee system operations, view all registered Data Owners and Data Users, and audit the cryptographic tamper-evident ledger.

### Key Administrator Screens:
- **Admin Sign In:** `Admin.jsp` -> `/Admin`
- **Admin Dashboard:** `Adminhome.jsp`
- **View Data Owners:** `DataOwnerInfo.jsp`
- **View Data Users:** `DataUserInfo.jsp`
- **Integrity Audit:** `BlocksData.jsp` -> Audits the HMAC-SHA-256 ledger (`audit_chain`)
