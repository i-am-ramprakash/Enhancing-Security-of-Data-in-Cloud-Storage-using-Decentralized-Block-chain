package com.servlets;

import com.dao.StorageRepository;
import com.security.Csrf;
import com.security.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

final class ServletSupport {
    static final StorageRepository REPOSITORY = new StorageRepository();

    private ServletSupport() { }

    static void signIn(HttpServletRequest request, StorageRepository.Account account) {
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(30 * 60);
        session.setAttribute("accountId", account.id());
        session.setAttribute("email", account.email());
        session.setAttribute("name", account.name());
        session.setAttribute("role", account.role().name());
        Csrf.ensure(request);
    }

    static long accountId(HttpServletRequest request) {
        return ((Number) request.getSession(false).getAttribute("accountId")).longValue();
    }

    static Role role(HttpServletRequest request) {
        return Role.parse(request.getSession(false).getAttribute("role").toString());
    }

    static void flash(HttpServletRequest request, String type, String message) {
        request.getSession().setAttribute("flashType", type);
        request.getSession().setAttribute("flash", message);
    }

    static void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    static void preparePage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            request.setAttribute("flash", session.getAttribute("flash"));
            request.setAttribute("flashType", session.getAttribute("flashType"));
            session.removeAttribute("flash");
            session.removeAttribute("flashType");
        }
    }
}
