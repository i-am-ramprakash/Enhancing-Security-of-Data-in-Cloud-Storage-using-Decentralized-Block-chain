package com.servlets;

import com.security.FileCrypto;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/ViewData")
public class ViewData extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            var stored = ServletSupport.REPOSITORY.authorizedFile(request.getParameter("fid"),
                    ServletSupport.accountId(request));
            if (stored.isEmpty()) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            var file = stored.get();
            byte[] plaintext = FileCrypto.decrypt(file.encryptedFile(), FileCrypto.masterKeyFromEnvironment());
            ServletSupport.REPOSITORY.recordDownload(file.id(), ServletSupport.accountId(request));
            String filename = file.filename().replace("\"", "_").replace("\\", "_");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLengthLong(plaintext.length);
            response.getOutputStream().write(plaintext);
        } catch (SecurityException e) {
            request.getServletContext().log("File integrity check failed", e);
            response.sendError(HttpServletResponse.SC_CONFLICT, "Stored file failed integrity validation");
        } catch (Exception e) {
            request.getServletContext().log("Download failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
