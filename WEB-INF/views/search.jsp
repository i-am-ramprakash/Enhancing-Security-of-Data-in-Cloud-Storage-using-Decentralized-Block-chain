<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Search Shared Files — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <%@ include file="../jspf/flash.jspf" %>
        <section class="card">
            <div class="auth-banner-header" style="background-image: url('${pageContext.request.contextPath}/images/security_banner.png');">
                <h2 class="auth-banner-title">🔍 Search Shared Files</h2>
            </div>
            <p class="muted" style="margin-top: -0.5rem; margin-bottom: 1.25rem;">Search shared file descriptions and submit permission requests to file owners.</p>
            
            <form method="get" action="${pageContext.request.contextPath}/user/search">
                <label>Filename or Keyword Query
                    <input type="text" name="q" maxlength="200" placeholder="Type keywords e.g. report, design, financial..." value="<c:out value='${requestScope.query}'/>" required>
                </label>
                <button type="submit" style="width: auto;">🔍 Search Repository</button>
            </form>
        </section>
        
        <c:if test="${not empty requestScope.query}">
            <div class="card">
                <h2>Search Results for "<c:out value="${requestScope.query}"/>"</h2>
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Filename</th>
                                <th>File Owner</th>
                                <th>Description</th>
                                <th>Access Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${requestScope.files}" var="file">
                                <tr>
                                    <td><strong><c:out value="${file.filename()}"/></strong></td>
                                    <td><c:out value="${file.ownerEmail()}"/></td>
                                    <td><c:out value="${file.description()}"/></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/SendRequest" method="post" class="inline">
                                            <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="fid" value="${file.id()}">
                                            <button type="submit" style="width: auto; margin-top: 0;">🔑 Request Access</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty requestScope.files}">
                                <tr>
                                    <td colspan="4" class="muted" style="text-align: center; padding: 2rem;">No matching encrypted files found in repository.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
    </main>
</body>
</html>
