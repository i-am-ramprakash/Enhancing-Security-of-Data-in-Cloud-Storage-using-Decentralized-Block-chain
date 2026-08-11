<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title><c:out value="${requestScope.listedRole}"/> Directory — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1.25rem;flex-wrap:wrap;gap:1rem;">
            <div>
                <h1 style="margin:0;">👥 System Accounts Directory</h1>
                <p class="muted" style="margin:0.2rem 0 0 0;">Registered platform accounts authenticated via PBKDF2-HMAC-SHA-256 salted hashes.</p>
            </div>
            <div class="actions" style="margin:0;">
                <a class="button ${requestScope.listedRole == 'OWNER' ? '' : 'secondary'}" href="${pageContext.request.contextPath}/admin/accounts?role=OWNER" style="margin:0;width:auto;">Data Owners</a>
                <a class="button ${requestScope.listedRole == 'USER' ? '' : 'secondary'}" href="${pageContext.request.contextPath}/admin/accounts?role=USER" style="margin:0;width:auto;">Data Users</a>
            </div>
        </div>

        <div class="card" style="padding:0;">
            <div class="table-wrap" style="border:0;">
                <table>
                    <thead>
                        <tr>
                            <th>Account Name</th>
                            <th>Email Address</th>
                            <th>Age</th>
                            <th>Gender</th>
                            <th>Assigned Role</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.accounts}" var="account">
                            <tr>
                                <td><strong><c:out value="${account.name()}"/></strong></td>
                                <td><c:out value="${account.email()}"/></td>
                                <td><c:out value="${account.age()}"/></td>
                                <td><c:out value="${account.gender()}"/></td>
                                <td><span class="role-badge-pill" style="background:#f1f5f9;color:#0f172a;border-color:#cbd5e1;"><c:out value="${account.role()}"/></span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.accounts}">
                            <tr>
                                <td colspan="5" class="muted" style="text-align:center;padding:2.5rem;">No <c:out value="${requestScope.listedRole}"/> accounts registered.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</body>
</html>
