package com.jeanpipi.servlets;

// API de usuarios y perfiles.
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.servicios.UsuarioService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SecurityUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/users/*")
public class UsuarioServlet extends BaseApiServlet {
    private final UsuarioService service = new UsuarioService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 1 && "authors".equals(p[0])) {
            requireRoles(req, "ADMIN", "EDITOR", "AUTOR");
            JsonUtil.ok(res, service.authors());
            return;
        }
        if (p.length == 1 && "me".equals(p[0])) {
            Usuario user = requireUser(req);
            JsonUtil.ok(res, service.profile(user.getId()));
            return;
        }
        requireRoles(req, "ADMIN");
        int page = RequestUtil.intParam(req, "page", 1, 1, 100000);
        int size = RequestUtil.intParam(req, "size", 20, 1, 100);
        if (p.length == 1) {
            Long id = RequestUtil.longValue(p[0]);
            if (id == null) throw new IllegalArgumentException("Id invalido");
            JsonUtil.ok(res, service.profile(id));
            return;
        }
        JsonUtil.ok(res, service.list(req.getParameter("q"), req.getParameter("role"), page, size));
    }

    @Override
    protected void handlePut(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 0) throw new IllegalArgumentException("Ruta invalida");
        if ("me".equals(p[0])) {
            Usuario user = requireUser(req);
            ProfilePayload payload = JsonUtil.read(req, ProfilePayload.class);
            if (p.length == 2 && "password".equals(p[1])) {
                service.changePassword(user.getId(), payload.currentPassword, payload.newPassword);
                audit.record(user, "CAMBIO_PASSWORD", "usuario", user.getId(), RequestUtil.clientIp(req), null);
                JsonUtil.ok(res, Map.of("message", "Contrasena actualizada"));
                return;
            }
            Usuario updated = service.updateProfile(user.getId(), payload.name, payload.email, payload.avatarUrl, payload.bio, payload.instagram, payload.twitter);
            req.getSession().setAttribute("usuario", updated);
            audit.record(updated, "ACTUALIZAR_PERFIL", "usuario", updated.getId(), RequestUtil.clientIp(req), null);
            JsonUtil.ok(res, updated);
            return;
        }
        Usuario admin = requireRoles(req, "ADMIN");
        Long id = RequestUtil.longValue(p[0]);
        if (id == null) throw new IllegalArgumentException("Id invalido");
        AdminPayload payload = JsonUtil.read(req, AdminPayload.class);
        Usuario updated = service.updateAdmin(id, payload.name, payload.role, payload.active);
        audit.record(admin, "ACTUALIZAR_USUARIO", "usuario", id, RequestUtil.clientIp(req), payload.role);
        JsonUtil.ok(res, updated);
    }

    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario admin = requireRoles(req, "ADMIN");
        String[] p = path(req);
        if (p.length != 1) throw new IllegalArgumentException("Id requerido");
        Long id = RequestUtil.longValue(p[0]);
        if (id == null || id == admin.getId()) throw new IllegalArgumentException("Usuario invalido");
        service.deactivate(id);
        audit.record(admin, "DESACTIVAR_USUARIO", "usuario", id, RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("message", "Usuario desactivado"));
    }

    private static final class ProfilePayload {
        String name;
        String email;
        String avatarUrl;
        String bio;
        String instagram;
        String twitter;
        String currentPassword;
        String newPassword;
    }

    private static final class AdminPayload {
        String name;
        String role;
        boolean active;
    }
}
