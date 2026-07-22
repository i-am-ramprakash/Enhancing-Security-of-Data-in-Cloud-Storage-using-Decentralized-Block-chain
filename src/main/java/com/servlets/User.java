package com.servlets;

import com.security.Role;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/User")
public class User extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin.login(request, response, Role.USER, "/user/home", "/DataUserLogin.jsp");
    }
}
