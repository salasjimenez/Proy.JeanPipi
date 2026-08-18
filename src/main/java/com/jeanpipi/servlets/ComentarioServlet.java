package com.jeanpipi.servlets;

// API de comentarios.
import com.jeanpipi.modelos.Comentario;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.servicios.ComentarioService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SecurityUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/comments/*")
public class ComentarioServlet extends BaseApiServlet {
    private final ComentarioService service = new ComentarioService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("true".equalsIgnoreCase(req.getParameter("admin"))) {
            requireRoles(req, "ADMIN", "EDITOR");
            int page = RequestUtil.intParam(req, "page", 1, 1, 100000);
            int size = RequestUtil.intParam(req, "size", 30, 1, 100);
            JsonUtil.ok(res, service.adminList(req.getParameter("status"), page, size));
            return;
        }
        Long articleId = RequestUtil.longValue(req.getParameter("articleId"));
        if (articleId == null) throw new IllegalArgumentException("Articulo requerido");
        JsonUtil.ok(res, service.approved(articleId));
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = SecurityUtil.user(req);
        Comentario item = service.create(user, JsonUtil.read(req, Comentario.class));
        audit.record(user, "CREAR_COMENTARIO", "comentario", item.getId(), RequestUtil.clientIp(req), null);
        JsonUtil.created(res, item);
    }

    @Override
    protected void handlePut(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireRoles(req, "ADMIN", "EDITOR");
        String[] p = path(req);
        Long id = p.length == 1 ? RequestUtil.longValue(p[0]) : null;
        if (id == null) throw new IllegalArgumentException("Id invalido");
        ModerationPayload payload = JsonUtil.read(req, ModerationPayload.class);
        service.moderate(id, payload.status == null ? "" : payload.status.toUpperCase());
        audit.record(actor, "MODERAR_COMENTARIO", "comentario", id, RequestUtil.clientIp(req), payload.status);
        JsonUtil.ok(res, Map.of("message", "Comentario actualizado"));
    }

    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireRoles(req, "ADMIN", "EDITOR");
        String[] p = path(req);
        Long id = p.length == 1 ? RequestUtil.longValue(p[0]) : null;
        if (id == null) throw new IllegalArgumentException("Id invalido");
        service.delete(id);
        audit.record(actor, "ELIMINAR_COMENTARIO", "comentario", id, RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("message", "Comentario eliminado"));
    }

    private static final class ModerationPayload { String status; }
}
