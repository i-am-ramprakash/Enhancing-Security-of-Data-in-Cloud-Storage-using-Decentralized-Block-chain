package com.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/Approve")
public class Approve extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            long requestId = Long.parseLong(request.getParameter("rid"));
            boolean approve = "approve".equals(request.getParameter("decision"));
            boolean changed = ServletSupport.REPOSITORY.decideRequest(requestId, ServletSupport.accountId(request), approve);
            ServletSupport.flash(request, changed ? "success" : "error",
                    changed ? "Request updated." : "Request not found.");
        } catch (Exception e) {
            request.getServletContext().log("Approval failed", e);
            ServletSupport.flash(request, "error", "Could not update request.");
        }
        ServletSupport.redirect(request, response, "/owner/requests");
    }
}
