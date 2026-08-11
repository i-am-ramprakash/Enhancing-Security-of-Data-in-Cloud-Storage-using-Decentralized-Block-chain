<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Dashboard — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <%@ include file="../jspf/flash.jspf" %>
        
        <section class="portal-hero-banner">
            <div>
                <div class="security-badge">
                    <span>🛡️ Workspace Authenticated &amp; Encrypted</span>
                </div>
                <h1>Welcome back, <c:out value="${sessionScope.name}"/></h1>
                <p>Signed in as <strong><c:out value="${sessionScope.role}"/></strong> (<c:out value="${sessionScope.email}"/>). Your workspace is protected with zero-trust authenticated encryption and tamper-evident audit logging.</p>
                <div class="actions">
                    <c:choose>
                        <c:when test="${sessionScope.role == 'OWNER' || sessionScope.role == 'DATA_OWNER'}">
                            <a class="btn-owner" href="${pageContext.request.contextPath}/owner/upload">☁️ Upload New File &rarr;</a>
                            <a class="btn-card-secondary" href="${pageContext.request.contextPath}/owner/files">📁 Manage My Files</a>
                            <a class="btn-card-secondary" href="${pageContext.request.contextPath}/owner/requests">📩 View Access Requests</a>
                        </c:when>
                        <c:when test="${sessionScope.role == 'USER' || sessionScope.role == 'DATA_USER'}">
                            <a class="btn-card-primary amber-theme" href="${pageContext.request.contextPath}/user/search">🔑 Search Shared Files &rarr;</a>
                            <a class="btn-card-secondary" href="${pageContext.request.contextPath}/user/requests">📋 My Requests &amp; Downloads</a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn-card-primary blue-theme" href="${pageContext.request.contextPath}/admin/audit">🛡️ View Integrity Audit Ledger &rarr;</a>
                            <a class="btn-card-secondary" href="${pageContext.request.contextPath}/admin/accounts">👥 User &amp; Owner Accounts</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="dashboard-visual-container">
                <c:choose>
                    <c:when test="${sessionScope.role == 'OWNER' || sessionScope.role == 'DATA_OWNER'}">
                        <img src="${pageContext.request.contextPath}/images/owner_dashboard_hero_3d.png" alt="Data Owner 3D Workspace" class="dashboard-hero-3d-img">
                        <div class="dashboard-overlay-card">
                            <div class="hero-overlay-icon" style="background:var(--purple-soft);color:var(--purple);">🔒</div>
                            <div>
                                <div class="hero-overlay-title">AES-256-GCM Envelope Encryption</div>
                                <div class="hero-overlay-desc">Owner-sovereign key management</div>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${sessionScope.role == 'USER' || sessionScope.role == 'DATA_USER'}">
                        <img src="${pageContext.request.contextPath}/images/user_dashboard_hero_3d.png" alt="Data User 3D Portal" class="dashboard-hero-3d-img">
                        <div class="dashboard-overlay-card">
                            <div class="hero-overlay-icon" style="background:var(--amber-soft);color:var(--amber);">🔑</div>
                            <div>
                                <div class="hero-overlay-title">Cryptographic Access Grants</div>
                                <div class="hero-overlay-desc">Approved download payload access</div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/images/admin_dashboard_hero_3d.png" alt="System Administrator 3D Audit" class="dashboard-hero-3d-img">
                        <div class="dashboard-overlay-card">
                            <div class="hero-overlay-icon" style="background:var(--blue-soft);color:var(--blue);">🛡️</div>
                            <div>
                                <div class="hero-overlay-title">Immutable Audit Chain</div>
                                <div class="hero-overlay-desc">HMAC-SHA-256 integrity verified</div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <section class="stat-grid">
            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Encryption Status</span>
                    <div class="metric-icon-box icon-purple" style="width:36px;height:36px;font-size:1.1rem;">🔒</div>
                </div>
                <div class="stat-value">AES-256-GCM</div>
                <p class="stat-desc">Zero-trust envelope key wrapping</p>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Session Protection</span>
                    <div class="metric-icon-box icon-amber" style="width:36px;height:36px;font-size:1.1rem;">🛡️</div>
                </div>
                <div class="stat-value">Active</div>
                <p class="stat-desc">PBKDF2 salted authentication &amp; CSRF defense</p>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Audit Ledger</span>
                    <div class="metric-icon-box icon-green" style="width:36px;height:36px;font-size:1.1rem;">⛓️</div>
                </div>
                <div class="stat-value">Verified</div>
                <p class="stat-desc">HMAC-SHA-256 tamper-evident hash chain</p>
            </div>
        </section>
    </main>
</body>
</html>
