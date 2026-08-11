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
    private static final Set<String> PUBLIC_PAGES_LOWER = Set.of(
            "", "/", "/index.html", "/admin.jsp", "/dataownerlogin.jsp", "/dataownerregister.jsp",
            "/datauserlogin.jsp", "/datauserregister.jsp", "/admin", "/owner", "/ownerreg", "/user", "/userreg");
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
        response.setHeader("Content-Security-Policy", "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; form-action 'self'; frame-ancestors 'none'; base-uri 'none'");
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

        if (PUBLIC_PAGES_LOWER.contains(path.toLowerCase())) {
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
        String lower = path.toLowerCase();
        if (lower.startsWith("/admin/") || lower.equals("/adminhome.jsp") || lower.equals("/dataownerinfo.jsp")
                || lower.equals("/datauserinfo.jsp") || lower.equals("/blocksdata.jsp")
                || lower.startsWith("/trackdata")) return role == Role.ADMIN;
        if (lower.startsWith("/owner/") || OWNER_ACTIONS.stream().anyMatch(a -> a.equalsIgnoreCase(path))
                || lower.equals("/dataownerhome.jsp") || lower.equals("/uploadfile.jsp") || lower.equals("/viewownfiles.jsp")
                || lower.equals("/request.jsp") || lower.equals("/sendkey.jsp")) return role == Role.OWNER;
        if (lower.startsWith("/user/") || USER_ACTIONS.stream().anyMatch(a -> a.equalsIgnoreCase(path))
                || lower.equals("/datauserhome.jsp") || lower.equals("/searchfile.jsp") || lower.equals("/searchresult.jsp")
                || lower.equals("/response.jsp")) return role == Role.USER;
        if (lower.equals("/viewdata") || lower.equals("/viewdata1")) return role == Role.USER || role == Role.OWNER;
        if (lower.equals("/logout")) return true;
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
