<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Upload</title><link rel="stylesheet" href="${pageContext.request.contextPath}/app.css"></head><body>
<%@ include file="../jspf/header.jspf" %><main><%@ include file="../jspf/flash.jspf" %><section class="card narrow"><h1>Encrypt and upload</h1>
<form action="${pageContext.request.contextPath}/FileUpload" method="post" enctype="multipart/form-data"><input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
<label>Display filename <input name="filename" maxlength="255" placeholder="Defaults to selected filename"></label>
<label>Description <textarea name="content" maxlength="2000" required></textarea></label>
<label>File <input type="file" name="file" required></label><p class="muted">Maximum size: 16 MB. Files are always downloaded as attachments.</p>
<button type="submit">Upload securely</button></form></section></main></body></html>
