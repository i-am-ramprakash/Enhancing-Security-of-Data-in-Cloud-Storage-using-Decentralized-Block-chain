package com.security;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class SecurityFilter implements Filter {
    private static final Set<String> PUBLIC_PAGES = Set.of(
            "", "/", "/index.html", "/Admin.jsp", "/DataOwnerLogin.jsp", "/DataOwnerRegister.jsp",
            "/DataUserLogin.jsp", "/DataUserRegister.jsp", "/Admin", "/Owner", "/OwnerReg", "/User", "/UserReg");
    private static final Set<String> OWNER_ACTIONS = Set.of("/FileUpload", "/DeleteFile", "/Approve", "/SendKey");
    private static final Set<String> USER_ACTIONS = Set.of("/SendRequest");

    @Override
    public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) rawRequest;
        HttpServletResponse response = (HttpServletResponse) rawResponse;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "no-referrer");
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        response.setHeader("Content-Security-Policy", "default-src 'self'; img-src 'self' data:; style-src 'self'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'");
        response.setHeader("Cache-Control", "no-store");
        if (request.isSecure()) response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        if (isStatic(path)) {
            chain.doFilter(request, response);
            return;
        }

        Csrf.ensure(request);
        if ("POST".equalsIgnoreCase(request.getMethod()) && !Csrf.valid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        if (PUBLIC_PAGES.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("accountId") == null || session.getAttribute("role") == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
        Role role = Role.parse(session.getAttribute("role").toString());
        if (!authorized(path, role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }

    private static boolean authorized(String path, Role role) {
        if (path.startsWith("/admin/") || path.equals("/Adminhome.jsp") || path.equals("/DataOwnerInfo.jsp")
                || path.equals("/DataUserInfo.jsp") || path.equals("/BlocksData.jsp")
                || path.startsWith("/TrackData")) return role == Role.ADMIN;
        if (path.startsWith("/owner/") || OWNER_ACTIONS.contains(path) || path.equals("/DataOwnerHome.jsp")
                || path.equals("/UploadFile.jsp") || path.equals("/ViewOwnFiles.jsp")
                || path.equals("/Request.jsp") || path.equals("/SendKey.jsp")) return role == Role.OWNER;
        if (path.startsWith("/user/") || USER_ACTIONS.contains(path) || path.equals("/DataUserHome.jsp")
                || path.equals("/SearchFile.jsp") || path.equals("/SearchResult.jsp")
                || path.equals("/Response.jsp")) return role == Role.USER;
        if (path.equals("/ViewData") || path.equals("/ViewData1")) return role == Role.USER || role == Role.OWNER;
        if (path.equals("/Logout")) return true;
        return false;
    }

    private static boolean isStatic(String path) {
        return path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/fonts/") || path.startsWith("/uploads/")
                || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png")
                || path.endsWith(".jpg") || path.endsWith(".gif") || path.endsWith(".woff")
                || path.endsWith(".woff2") || path.endsWith(".ico");
    }
}
