package com.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/DeleteFile")
public class DeleteFile extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            boolean deleted = ServletSupport.REPOSITORY.deleteFile(request.getParameter("fid"), ServletSupport.accountId(request));
            ServletSupport.flash(request, deleted ? "success" : "error", deleted ? "File deleted." : "File not found.");
        } catch (Exception e) {
            request.getServletContext().log("Delete failed", e);
            ServletSupport.flash(request, "error", "Delete failed.");
        }
        ServletSupport.redirect(request, response, "/owner/files");
    }
}
