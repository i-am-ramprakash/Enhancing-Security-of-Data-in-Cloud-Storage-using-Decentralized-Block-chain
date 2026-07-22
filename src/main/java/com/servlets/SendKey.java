package com.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/** Compatibility endpoint: approvals now grant access server-side; keys are never disclosed. */
@WebServlet("/SendKey")
public class SendKey extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletSupport.flash(request, "success", "No key transmission is required; approved users can download securely.");
        ServletSupport.redirect(request, response, "/owner/requests");
    }
}
