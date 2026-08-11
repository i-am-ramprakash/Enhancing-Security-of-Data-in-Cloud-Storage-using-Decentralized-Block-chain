<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>File Access Requests — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <%@ include file="../jspf/flash.jspf" %>
        
        <div style="margin-bottom:1.25rem;">
            <h1 style="margin:0;">📩 File Access Requests</h1>
            <p class="muted" style="margin:0.2rem 0 0 0;">Review and approve/deny permission requests from Data Users seeking access to your encrypted files.</p>
        </div>

        <div class="card" style="padding:0;">
            <div class="table-wrap" style="border:0;">
                <table>
                    <thead>
                        <tr>
                            <th>Requested File</th>
                            <th>Requester Email</th>
                            <th>Status</th>
                            <th>Approval Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.requests}" var="item">
                            <tr>
                                <td><strong><c:out value="${item.filename()}"/></strong></td>
                                <td><c:out value="${item.requesterEmail()}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.status() == 'APPROVED'}">
                                            <span class="badge badge-approved">✓ APPROVED</span>
                                        </c:when>
                                        <c:when test="${item.status() == 'DENIED'}">
                                            <span class="badge badge-rejected">✕ DENIED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-pending">⏳ PENDING</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <form class="inline" action="${pageContext.request.contextPath}/Approve" method="post" style="display:inline;margin:0;">
                                        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="rid" value="${item.id()}">
                                        <button type="submit" name="decision" value="approve" style="width:auto;margin:0 0.2rem;padding:0.35rem 0.75rem;font-size:0.82rem;">✓ Approve</button>
                                        <button type="submit" class="danger" name="decision" value="deny" style="width:auto;margin:0 0.2rem;padding:0.35rem 0.75rem;font-size:0.82rem;">✕ Deny</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.requests}">
                            <tr>
                                <td colspan="4" class="muted" style="text-align:center;padding:2.5rem;">
                                    No pending or historical access requests found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</body>
</html>
