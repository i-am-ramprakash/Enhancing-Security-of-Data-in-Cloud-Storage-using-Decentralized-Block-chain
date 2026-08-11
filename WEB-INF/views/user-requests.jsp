<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>My Requests & Downloads — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <%@ include file="../jspf/flash.jspf" %>
        
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1.25rem;flex-wrap:wrap;gap:1rem;">
            <div>
                <h1 style="margin:0;">📋 My Access Requests & Downloads</h1>
                <p class="muted" style="margin:0.2rem 0 0 0;">Track permission requests submitted to Data Owners. Approved files can be downloaded and decrypted automatically.</p>
            </div>
            <a class="button" href="${pageContext.request.contextPath}/user/search" style="margin:0;width:auto;">🔍 Search Shared Files</a>
        </div>

        <div class="card" style="padding:0;">
            <div class="table-wrap" style="border:0;">
                <table>
                    <thead>
                        <tr>
                            <th>Requested File</th>
                            <th>File Owner</th>
                            <th>Permission Status</th>
                            <th>Download Payload</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.requests}" var="item">
                            <tr>
                                <td><strong><c:out value="${item.filename()}"/></strong></td>
                                <td><c:out value="${item.ownerEmail()}"/></td>
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
                                    <c:if test="${item.status() == 'APPROVED'}">
                                        <a class="button" href="${pageContext.request.contextPath}/ViewData?fid=${item.fileId()}" style="width:auto;margin:0;padding:0.35rem 0.75rem;font-size:0.82rem;">⬇️ Download Decrypted File</a>
                                    </c:if>
                                    <c:if test="${item.status() != 'APPROVED'}">
                                        <span class="muted" style="font-size:0.85rem;">Awaiting Owner Grant</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.requests}">
                            <tr>
                                <td colspan="4" class="muted" style="text-align:center;padding:2.5rem;">
                                    No file access requests submitted yet. Click <strong>Search Shared Files</strong> to request access.
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
