package com.servlets;

import com.security.Input;
import com.security.PasswordHasher;
import com.security.Role;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/OwnerReg")
public class OwnerReg extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        register(request, response, Role.OWNER, "/DataOwnerLogin.jsp", "/DataOwnerRegister.jsp");
    }

    static void register(HttpServletRequest request, HttpServletResponse response, Role role,
                         String success, String failure) throws IOException {
        try {
            String name = Input.text(request.getParameter("name"), 100);
            String email = Input.email(request.getParameter("email"));
            int age = Input.age(request.getParameter("age"));
            String gender = Input.text(request.getParameter("gender"), 30);
            String passwordHash = PasswordHasher.hash(request.getParameter("password"));
            ServletSupport.REPOSITORY.register(name, email, age, gender, passwordHash, role);
            ServletSupport.flash(request, "success", "Registration successful. You can now sign in.");
            ServletSupport.redirect(request, response, success);
        } catch (SQLIntegrityConstraintViolationException e) {
            ServletSupport.flash(request, "error", "That email address is already registered.");
            ServletSupport.redirect(request, response, failure);
        } catch (IllegalArgumentException e) {
            ServletSupport.flash(request, "error", e.getMessage());
            ServletSupport.redirect(request, response, failure);
        } catch (Exception e) {
            request.getServletContext().log("Registration failed", e);
            ServletSupport.flash(request, "error", "Registration is temporarily unavailable.");
            ServletSupport.redirect(request, response, failure);
        }
    }
}
