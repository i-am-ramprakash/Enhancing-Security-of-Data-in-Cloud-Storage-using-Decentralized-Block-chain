<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Integrity Audit Ledger — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <div style="margin-bottom:1.25rem;">
            <h1 style="margin:0;">⛓️ Append-Only Cryptographic Audit Ledger</h1>
            <p class="muted" style="margin:0.2rem 0 0 0;">Each audit entry commits to the previous entry hash using HMAC-SHA-256 (`APP_AUDIT_KEY`). Any database modification breaks the chain and flags an alert.</p>
        </div>

        <div class="card" style="padding:0;">
            <div class="table-wrap" style="border:0;">
                <table>
                    <thead>
                        <tr>
                            <th>Block ID</th>
                            <th>Entity Reference</th>
                            <th>Event Action</th>
                            <th>Cryptographic Hash</th>
                            <th>Timestamp</th>
                            <th>Tamper Integrity</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.entries}" var="entry">
                            <tr>
                                <td><strong>#<c:out value="${entry.id()}"/></strong></td>
                                <td><c:out value="${entry.entityType()}"/>/<c:out value="${entry.entityId()}"/></td>
                                <td><span class="role-badge-pill" style="background:#eff6ff;color:#1e40af;border-color:#bfdbfe;"><c:out value="${entry.eventType()}"/></span></td>
                                <td><code><c:out value="${entry.entryHash()}"/></code></td>
                                <td><c:out value="${entry.createdAt()}"/></td>
                                <td>
                                    <span class="${entry.valid() ? 'valid' : 'invalid'}">
                                        <c:out value="${entry.valid() ? 'VALID' : 'INVALID'}"/>
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.entries}">
                            <tr>
                                <td colspan="6" class="muted" style="text-align:center;padding:2.5rem;">No audit entries logged yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</body>
</html>
