package com.servlets;

import com.security.FileCrypto;
import com.security.Input;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;

@MultipartConfig(maxFileSize = 16_000_000, maxRequestSize = 17_000_000, fileSizeThreshold = 1_000_000)
@WebServlet("/FileUpload")
public class FileUpload extends HttpServlet {
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Part part = request.getPart("file");
            if (part == null || part.getSize() == 0) throw new IllegalArgumentException("Choose a non-empty file");
            String submitted = part.getSubmittedFileName();
            String filename = Input.safeFilename(request.getParameter("filename") == null
                    || request.getParameter("filename").isBlank() ? submitted : request.getParameter("filename"));
            String description = Input.text(request.getParameter("content"), 2_000);
            String contentType = part.getContentType() == null ? "application/octet-stream"
                    : Input.text(part.getContentType(), 150);
            byte[] plaintext;
            try (InputStream input = part.getInputStream()) {
                plaintext = input.readAllBytes();
            }
            var encrypted = FileCrypto.encrypt(plaintext, FileCrypto.masterKeyFromEnvironment());
            ServletSupport.REPOSITORY.saveFile(ServletSupport.accountId(request), filename, contentType, description, encrypted);
            ServletSupport.flash(request, "success", "File encrypted and uploaded successfully.");
        } catch (IllegalArgumentException e) {
            ServletSupport.flash(request, "error", e.getMessage());
        } catch (Exception e) {
            request.getServletContext().log("Upload failed", e);
            ServletSupport.flash(request, "error", "Upload failed.");
        }
        ServletSupport.redirect(request, response, "/owner/upload");
    }
}
