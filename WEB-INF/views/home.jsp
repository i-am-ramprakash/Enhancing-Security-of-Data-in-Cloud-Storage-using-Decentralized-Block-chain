<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Dashboard</title><link rel="stylesheet" href="${pageContext.request.contextPath}/app.css"></head><body>
<%@ include file="../jspf/header.jspf" %>
<main><%@ include file="../jspf/flash.jspf" %><section class="card"><h1>Welcome, <c:out value="${sessionScope.name}"/></h1><p>You are signed in as <strong><c:out value="${sessionScope.role}"/></strong>. All file operations are authorized on the server and recorded in the integrity audit.</p></section></main>
</body></html>
