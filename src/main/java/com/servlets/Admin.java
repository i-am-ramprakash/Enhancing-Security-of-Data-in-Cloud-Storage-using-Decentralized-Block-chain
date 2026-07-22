package com.servlets;

import com.security.Input;
import com.security.LoginAttemptLimiter;
import com.security.Role;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/Admin")
public class Admin extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        login(request, response, Role.ADMIN, "/admin/home", "/Admin.jsp");
    }

    static void login(HttpServletRequest request, HttpServletResponse response, Role role,
                      String success, String failure) throws IOException {
        try {
            String email = Input.email(request.getParameter("email"));
            String password = request.getParameter("password");
            String attemptKey = request.getRemoteAddr() + "|" + email;
            if (!LoginAttemptLimiter.allowed(attemptKey)) {
                ServletSupport.flash(request, "error", "Too many failed attempts. Try again later.");
                ServletSupport.redirect(request, response, failure);
                return;
            }
            var account = ServletSupport.REPOSITORY.authenticate(email, password, role);
            if (account.isPresent()) {
                LoginAttemptLimiter.succeeded(attemptKey);
                ServletSupport.signIn(request, account.get());
                ServletSupport.redirect(request, response, success);
            } else {
                LoginAttemptLimiter.failed(attemptKey);
                ServletSupport.flash(request, "error", "Invalid credentials");
                ServletSupport.redirect(request, response, failure);
            }
        } catch (Exception e) {
            getServletContext(request).log("Login failed", e);
            ServletSupport.flash(request, "error", "Unable to sign in");
            ServletSupport.redirect(request, response, failure);
        }
    }

    private static javax.servlet.ServletContext getServletContext(HttpServletRequest request) {
        return request.getServletContext();
    }
}
