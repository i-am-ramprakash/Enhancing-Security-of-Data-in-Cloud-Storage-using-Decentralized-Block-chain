package com.servlets;

import com.security.Role;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/UserReg")
public class UserReg extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OwnerReg.register(request, response, Role.USER, "/DataUserLogin.jsp", "/DataUserRegister.jsp");
    }
}
