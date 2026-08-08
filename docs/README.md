# Secure Cloud Storage — Comprehensive Documentation

Welcome to the official documentation suite for **Secure Cloud Storage**, an enterprise-grade Java 17 Servlet & JSP web application designed for encrypted, owner-approved cloud file sharing and tamper-evident audit logging.

---

## 📚 Documentation Index

| Module | Document | Description |
|---|---|---|
| **00** | [Project Executive Overview](00_PROJECT_EXECUTIVE_OVERVIEW.md) | **Deep overview**: What is this project, why do we need it, purpose, real-world applications, and how it is used |
| **01** | [Architecture Overview](01_ARCHITECTURE_OVERVIEW.md) | System design, technology stack, v1 legacy comparison, threat model, and RBAC hierarchy |
| **02** | [Security & Cryptography](02_SECURITY_AND_CRYPTOGRAPHY.md) | PBKDF2 password hashing, AES-256-GCM envelope encryption, key management, CSRF tokens, rate limiting |
| **03** | [Database & Audit Chain](03_DATABASE_AND_AUDIT_CHAIN.md) | MySQL schema tables, HikariCP connection pool, and append-only HMAC-SHA-256 tamper-evident ledger |
| **04** | [API & Servlets](04_API_AND_SERVLETS.md) | Complete Servlet mapping (`com.servlets.*`), routing rules, `SecurityFilter` authorization logic |
| **05** | [User Workflows & Roles](05_USER_WORKFLOWS_AND_ROLES.md) | Step-by-step user journeys and screen flows for `ADMIN`, `OWNER`, and `USER` roles |
| **06** | [Testing & Verification](06_TESTING_AND_VERIFICATION.md) | Unit test suite breakdown, running Maven tests, and security verification |
| **07** | [Troubleshooting & FAQs](07_TROUBLESHOOTING_AND_FAQS.md) | Known issues (JDBC driver loading, environment variables, MySQL 9.1 setup) and fixes |
| **08** | [Setup & Deployment Guide](08_SETUP_AND_DEPLOYMENT_GUIDE.md) | Complete setup guide for development and production deployment on Apache Tomcat 9/10 |
| **09** | [Tools & Technology Stack](09_TOOLS_AND_TECHNOLOGY_STACK.md) | **Detailed breakdown of all tools & tech**: Java 17/21, Maven, Servlets/JSP, MySQL, HikariCP, JCE Crypto, Tomcat, JUnit 5 with code/command examples |

---

## 🔑 Key Technology Stack

- **Core Runtime:** Java 17+ / Java 21
- **Build & Dependency Management:** Apache Maven 3.9+
- **Web Container:** Java Servlet 4.0 / JSP (Apache Tomcat 9 or 10)
- **Database:** MySQL 8.4+ / MySQL 9.1+ (Engine: InnoDB)
- **Database Connection Pool:** HikariCP 6.3.3
- **Database Driver:** `com.mysql:mysql-connector-j:9.7.0`
- **Security & Cryptography:** Java Cryptography Extension (JCE), AES-256-GCM, PBKDF2-HMAC-SHA-256, HMAC-SHA-256 Audit Ledger
- **Testing Framework:** JUnit 5 (Jupiter 5.14.0)

---

## ⚡ Quick Start

For quick local setup and deployment instructions, refer directly to [08_SETUP_AND_DEPLOYMENT_GUIDE.md](08_SETUP_AND_DEPLOYMENT_GUIDE.md).
