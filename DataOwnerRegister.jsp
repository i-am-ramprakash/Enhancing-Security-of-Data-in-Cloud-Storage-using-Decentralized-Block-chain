<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Register Data Owner — Secure Cloud Storage</title>
    <link rel="stylesheet" href="app.css">
</head>
<body>
    <header>
        <strong><a href="index.html" style="color:white;text-decoration:none;">Secure Cloud Storage</a></strong>
        <nav>
            <a href="DataOwnerLogin.jsp">Owner Sign In</a>
            <a href="DataUserLogin.jsp">User Sign In</a>
        </nav>
    </header>
    <main>
        <section class="card narrow">
            <div class="auth-banner-header" style="background-image: url('images/security_banner.png');">
                <h2 class="auth-banner-title">👤 Register Data Owner</h2>
            </div>

            <p class="muted" style="margin-top: -0.5rem; margin-bottom: 1.25rem;">Create a Data Owner account to upload encrypted files and manage access permissions.</p>

            <c:if test="${not empty sessionScope.flash}">
                <div class="flash error"><c:out value="${sessionScope.flash}"/></div>
                <c:remove var="flash" scope="session"/>
                <c:remove var="flashType" scope="session"/>
            </c:if>

            <form action="OwnerReg" method="post">
                <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                
                <label>Full Name
                    <input type="text" name="name" maxlength="100" placeholder="e.g. Jane Doe" required>
                </label>
                
                <label>Email Address
                    <input type="email" name="email" maxlength="254" placeholder="name@company.com" autocomplete="username" required>
                </label>
                
                <label>Age
                    <input type="number" name="age" min="13" max="120" placeholder="e.g. 28" required>
                </label>
                
                <label>Gender
                    <select name="gender" required>
                        <option value="" disabled selected>Select gender</option>
                        <option value="Woman">Woman</option>
                        <option value="Man">Man</option>
                        <option value="Non-binary">Non-binary</option>
                        <option value="Prefer not to say">Prefer not to say</option>
                    </select>
                </label>
                
                <label>Password (12+ characters)
                    <input type="password" name="password" minlength="12" maxlength="128" placeholder="••••••••••••" autocomplete="new-password" required>
                </label>
                
                <button type="submit">Create Owner Account</button>
            </form>
            
            <p style="text-align: center; margin-top: 1.5rem; margin-bottom: 0;">
                Already have an account? <a href="DataOwnerLogin.jsp"><strong>Sign in here</strong></a>
            </p>
        </section>
    </main>
</body>
</html>
