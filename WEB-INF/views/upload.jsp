<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Encrypt & Upload File — Secure Cloud Storage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app.css">
</head>
<body>
    <%@ include file="../jspf/header.jspf" %>
    <main>
        <%@ include file="../jspf/flash.jspf" %>
        <section class="card narrow">
            <div class="auth-banner-header" style="background-image: url('${pageContext.request.contextPath}/images/security_banner.png');">
                <h2 class="auth-banner-title">🔐 Encrypt & Upload File</h2>
            </div>
            
            <p class="muted" style="margin-top: -0.5rem; margin-bottom: 1.25rem;">Uploaded files are automatically encrypted with AES-256-GCM envelope encryption before being stored in the cloud.</p>
            
            <form action="${pageContext.request.contextPath}/FileUpload" method="post" enctype="multipart/form-data">
                <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                
                <label>Display Filename (Optional)
                    <input type="text" name="filename" maxlength="255" placeholder="e.g. Confidential_Quarterly_Report.pdf">
                </label>
                
                <label>File Description
                    <textarea name="content" maxlength="2000" placeholder="Provide a brief summary of the file content for data users..." required></textarea>
                </label>
                
                <label>Select File Payload
                    <input type="file" name="file" required>
                </label>
                
                <p class="muted" style="margin-top: 0.5rem;">Maximum file size: 16 MB. Files are stored as encrypted blobs and downloaded via authenticated AES decryption streams.</p>
                
                <button type="submit">🔒 Encrypt & Upload File</button>
            </form>
        </section>
    </main>
</body>
</html>
