package com.jeanpipi.servlets;

// API de autenticacion.
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.servicios.AuthService;
import com.jeanpipi.util.CsrfUtil;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SecurityUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/v1/auth/*")
public class AuthServlet extends BaseApiServlet {
    private final AuthService service = new AuthService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        String action = p.length == 0 ? "session" : p[0];
        if ("csrf".equals(action)) {
            JsonUtil.ok(res, Map.of("csrfToken", CsrfUtil.token(req)));
            return;
        }
        if ("session".equals(action)) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("user", SecurityUtil.user(req));
            data.put("csrfToken", CsrfUtil.token(req));
            JsonUtil.ok(res, data);
            return;
        }
        JsonUtil.error(res, 404, "Ruta no encontrada");
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 0) throw new IllegalArgumentException("Accion requerida");
        switch (p[0]) {
            case "register" -> register(req, res);
            case "login" -> login(req, res);
            case "logout" -> logout(req, res);
            case "forgot-password" -> forgot(req, res);
            case "reset-password" -> reset(req, res);
            default -> JsonUtil.error(res, 404, "Ruta no encontrada");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse res) throws Exception {
        AuthPayload payload = JsonUtil.read(req, AuthPayload.class);
        Usuario user = service.register(payload.name, payload.email, payload.password);
        req.getSession(true).setAttribute("usuario", user);
        req.changeSessionId();
        audit.record(user, "REGISTRO", "usuario", user.getId(), RequestUtil.clientIp(req), null);
        JsonUtil.created(res, Map.of("user", user, "csrfToken", CsrfUtil.token(req)));
    }

    private void login(HttpServletRequest req, HttpServletResponse res) throws Exception {
        AuthPayload payload = JsonUtil.read(req, AuthPayload.class);
        Usuario user = service.login(payload.email, payload.password);
        req.getSession(true).setAttribute("usuario", user);
        req.changeSessionId();
        audit.record(user, "LOGIN", "sesion", user.getId(), RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("user", user, "csrfToken", CsrfUtil.token(req)));
    }

    private void logout(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = SecurityUtil.user(req);
        audit.record(user, "LOGOUT", "sesion", user == null ? null : user.getId(), RequestUtil.clientIp(req), null);
        if (req.getSession(false) != null) req.getSession(false).invalidate();
        JsonUtil.ok(res, Map.of("message", "Sesion cerrada"));
    }

    private void forgot(HttpServletRequest req, HttpServletResponse res) throws Exception {
        AuthPayload payload = JsonUtil.read(req, AuthPayload.class);
        service.requestReset(payload.email);
        JsonUtil.ok(res, Map.of("message", "Si la cuenta existe, se enviaron instrucciones"));
    }

    private void reset(HttpServletRequest req, HttpServletResponse res) throws Exception {
        AuthPayload payload = JsonUtil.read(req, AuthPayload.class);
        service.resetPassword(payload.token, payload.password);
        JsonUtil.ok(res, Map.of("message", "Contrasena actualizada"));
    }

    private static final class AuthPayload {
        String name;
        String email;
        String password;
        String token;
    }
}
