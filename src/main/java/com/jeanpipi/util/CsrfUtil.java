package com.jeanpipi.util;

// Token CSRF de la sesion.
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String KEY = "csrfToken";

    private CsrfUtil() {}

    public static String token(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object existing = session.getAttribute(KEY);
        if (existing instanceof String value && !value.isBlank()) {
            return value;
        }
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(KEY, value);
        return value;
    }

    public static boolean valid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object value = session.getAttribute(KEY);
        String header = request.getHeader("X-CSRF-Token");
        return value instanceof String expected && header != null && constantEquals(expected, header);
    }

    private static boolean constantEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
