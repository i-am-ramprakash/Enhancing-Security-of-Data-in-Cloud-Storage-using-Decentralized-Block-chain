package com.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/SendRequest")
public class SendRequest extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            boolean created = ServletSupport.REPOSITORY.requestAccess(request.getParameter("fid"), ServletSupport.accountId(request));
            ServletSupport.flash(request, created ? "success" : "error",
                    created ? "Access request submitted." : "The file is unavailable.");
        } catch (Exception e) {
            request.getServletContext().log("Access request failed", e);
            ServletSupport.flash(request, "error", "Could not submit request.");
        }
        ServletSupport.redirect(request, response, "/user/search");
    }
}
