package com.jeanpipi.util;

// Utilidades de autorizacion.
import com.jeanpipi.modelos.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static Usuario user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (Usuario) session.getAttribute("usuario");
    }

    public static boolean hasRole(Usuario user, String... roles) {
        if (user == null || user.getRol() == null) {
            return false;
        }
        return Set.of(roles).contains(user.getRol());
    }

    public static boolean canEditContent(Usuario user) {
        return hasRole(user, "ADMIN", "EDITOR", "AUTOR");
    }

    public static boolean canPublish(Usuario user) {
        return hasRole(user, "ADMIN", "EDITOR");
    }

    public static boolean canAdmin(Usuario user) {
        return hasRole(user, "ADMIN");
    }
}
