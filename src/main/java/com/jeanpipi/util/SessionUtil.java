package com.jeanpipi.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SessionUtil {
    public static final String USER_ID = "usuarioId";
    public static final String USER_NAME = "usuarioNombre";
    public static final String USER_ROLE = "usuarioRol";

    private SessionUtil() {
    }

    public static boolean autenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(USER_ID) != null;
    }

    public static boolean esAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object role = session.getAttribute(USER_ROLE);
        return role != null && "ADMIN".equalsIgnoreCase(role.toString());
    }

    public static Integer usuarioId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(USER_ID);
        return value instanceof Integer ? (Integer) value : null;
    }
}
