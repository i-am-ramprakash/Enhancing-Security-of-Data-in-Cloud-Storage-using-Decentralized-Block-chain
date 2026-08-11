<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>My Encrypted Files — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <%@ include file="../jspf/flash.jspf" %>
        
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1.25rem;flex-wrap:wrap;gap:1rem;">
            <div>
                <h1 style="margin:0;">📁 My Encrypted Files</h1>
                <p class="muted" style="margin:0.2rem 0 0 0;">Manage your uploaded files protected with AES-256-GCM envelope encryption.</p>
            </div>
            <a class="button" href="${pageContext.request.contextPath}/owner/upload" style="margin:0;width:auto;">📤 Upload New File</a>
        </div>

        <div class="card" style="padding:0;">
            <div class="table-wrap" style="border:0;">
                <table>
                    <thead>
                        <tr>
                            <th>Filename</th>
                            <th>Description</th>
                            <th>Upload Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.files}" var="file">
                            <tr>
                                <td><strong><c:out value="${file.filename()}"/></strong></td>
                                <td><c:out value="${file.description()}"/></td>
                                <td><c:out value="${file.createdAt()}"/></td>
                                <td>
                                    <a class="button" href="${pageContext.request.contextPath}/ViewData?fid=${file.id()}" style="width:auto;margin:0 0.2rem;padding:0.35rem 0.75rem;font-size:0.82rem;">⬇️ Download</a>
                                    <form class="inline" action="${pageContext.request.contextPath}/DeleteFile" method="post" style="display:inline;margin:0;">
                                        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="fid" value="${file.id()}">
                                        <button class="danger" type="submit" style="width:auto;margin:0 0.2rem;padding:0.35rem 0.75rem;font-size:0.82rem;">🗑️ Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.files}">
                            <tr>
                                <td colspan="4" class="muted" style="text-align:center;padding:2.5rem;">
                                    No encrypted files uploaded yet. Click <strong>Upload New File</strong> to get started.
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
