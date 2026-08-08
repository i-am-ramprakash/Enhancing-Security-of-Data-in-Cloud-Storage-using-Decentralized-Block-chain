<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Administrator Sign In — Secure Cloud Storage</title>
    <link rel="stylesheet" href="app.css">
</head>
<body>
    <header>
        <strong><a href="index.html" style="color:white;text-decoration:none;">Secure Cloud Storage</a></strong>
        <nav>
            <a href="index.html">Back to Home</a>
        </nav>
    </header>
    <main>
        <section class="card narrow">
            <div class="auth-banner-header" style="background-image: url('images/security_banner.png');">
                <h2 class="auth-banner-title">🛡️ Admin Sign In</h2>
            </div>

            <p class="muted" style="margin-top: -0.5rem; margin-bottom: 1.25rem;">Sign in to access system admin audit, account directories, and tamper integrity ledger.</p>

            <c:if test="${not empty sessionScope.flash}">
                <div class="flash error"><c:out value="${sessionScope.flash}"/></div>
                <c:remove var="flash" scope="session"/>
                <c:remove var="flashType" scope="session"/>
            </c:if>

            <form action="Admin" method="post">
                <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                
                <label>Administrator Email
                    <input type="email" name="email" maxlength="254" placeholder="admin@example.com" autocomplete="username" required>
                </label>
                
                <label>Password
                    <input type="password" name="password" maxlength="128" placeholder="••••••••••••" autocomplete="current-password" required>
                </label>
                
                <button type="submit">Sign In as Administrator</button>
            </form>
            
            <p style="text-align: center; margin-top: 1.5rem; margin-bottom: 0;">
                <a href="index.html">← Back to Portal Home</a>
            </p>
        </section>
    </main>
</body>
</html>
