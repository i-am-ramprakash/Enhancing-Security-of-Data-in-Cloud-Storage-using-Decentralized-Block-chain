package com.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public final class Csrf {
    public static final String SESSION_KEY = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() { }

    public static String ensure(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SESSION_KEY, token);
        }
        return token;
    }

    public static boolean valid(HttpServletRequest request) {
        String expected = (String) request.getSession(true).getAttribute(SESSION_KEY);
        String supplied = request.getParameter("csrf");
        return expected != null && supplied != null
                && java.security.MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
