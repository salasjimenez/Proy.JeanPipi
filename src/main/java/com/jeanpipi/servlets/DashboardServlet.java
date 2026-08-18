package com.jeanpipi.servlets;

// API de dashboard y portada.
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.servicios.DashboardService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/dashboard/*")
public class DashboardServlet extends BaseApiServlet {
    private final DashboardService service = new DashboardService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 1 && "sections".equals(p[0])) {
            JsonUtil.ok(res, service.sections());
            return;
        }
        requireRoles(req, "ADMIN", "EDITOR", "AUTOR");
        JsonUtil.ok(res, service.data());
    }

    @Override
    protected void handlePut(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = requireRoles(req, "ADMIN", "EDITOR");
        String[] p = path(req);
        if (p.length != 2 || !"sections".equals(p[0])) throw new IllegalArgumentException("Ruta invalida");
        Long id = RequestUtil.longValue(p[1]);
        if (id == null) throw new IllegalArgumentException("Id invalido");
        SectionPayload payload = JsonUtil.read(req, SectionPayload.class);
        service.updateSection(id, payload.title, payload.enabled, payload.position, payload.limitItems);
        audit.record(user, "ACTUALIZAR_SECCION", "seccion_portada", id, RequestUtil.clientIp(req), payload.title);
        JsonUtil.ok(res, Map.of("message", "Seccion actualizada"));
    }

    private static final class SectionPayload {
        String title;
        boolean enabled;
        int position;
        int limitItems;
    }
}
