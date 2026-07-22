package com.servlets;

import com.security.Input;
import com.security.Role;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {
        "/owner/home", "/owner/upload", "/owner/files", "/owner/requests",
        "/user/home", "/user/search", "/user/requests",
        "/admin/home", "/admin/accounts", "/admin/audit"
})
public class PageServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            ServletSupport.preparePage(request);
            String path = request.getServletPath();
            String view;
            switch (path) {
                case "/owner/home", "/user/home", "/admin/home" -> view = "/WEB-INF/views/home.jsp";
                case "/owner/upload" -> view = "/WEB-INF/views/upload.jsp";
                case "/owner/files" -> {
                    request.setAttribute("files", ServletSupport.REPOSITORY.ownerFiles(ServletSupport.accountId(request)));
                    view = "/WEB-INF/views/owner-files.jsp";
                }
                case "/owner/requests" -> {
                    request.setAttribute("requests", ServletSupport.REPOSITORY.incomingRequests(ServletSupport.accountId(request)));
                    view = "/WEB-INF/views/owner-requests.jsp";
                }
                case "/user/search" -> {
                    String query = request.getParameter("q");
                    if (query != null && !query.isBlank()) {
                        query = Input.text(query, 200);
                        request.setAttribute("query", query);
                        request.setAttribute("files", ServletSupport.REPOSITORY.searchFiles(query));
                    }
                    view = "/WEB-INF/views/search.jsp";
                }
                case "/user/requests" -> {
                    request.setAttribute("requests", ServletSupport.REPOSITORY.outgoingRequests(ServletSupport.accountId(request)));
                    view = "/WEB-INF/views/user-requests.jsp";
                }
                case "/admin/accounts" -> {
                    String roleValue = request.getParameter("role");
                    Role role = roleValue == null ? Role.USER : Role.parse(roleValue);
                    if (role == Role.ADMIN) throw new IllegalArgumentException("Admin accounts are not listed");
                    request.setAttribute("listedRole", role.name());
                    request.setAttribute("accounts", ServletSupport.REPOSITORY.accounts(role));
                    view = "/WEB-INF/views/accounts.jsp";
                }
                case "/admin/audit" -> {
                    request.setAttribute("entries", ServletSupport.REPOSITORY.auditEntries());
                    view = "/WEB-INF/views/audit.jsp";
                }
                default -> { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            }
            request.getRequestDispatcher(view).forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            request.getServletContext().log("Page rendering failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
