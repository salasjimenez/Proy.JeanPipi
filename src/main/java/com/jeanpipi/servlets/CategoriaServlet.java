package com.jeanpipi.servlets;

// API de categorias.
import com.jeanpipi.modelos.Categoria;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.servicios.CategoriaService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SecurityUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/categories/*")
public class CategoriaServlet extends BaseApiServlet {
    private final CategoriaService service = new CategoriaService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 1) {
            JsonUtil.ok(res, service.bySlug(p[0]));
            return;
        }
        boolean includeInactive = "true".equalsIgnoreCase(req.getParameter("all"));
        if (includeInactive) requireRoles(req, "ADMIN", "EDITOR");
        JsonUtil.ok(res, service.list(includeInactive));
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireRoles(req, "ADMIN", "EDITOR");
        Categoria item = service.create(JsonUtil.read(req, Categoria.class));
        audit.record(actor, "CREAR_CATEGORIA", "categoria", item.getId(), RequestUtil.clientIp(req), item.getNombre());
        JsonUtil.created(res, item);
    }

    @Override
    protected void handlePut(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireRoles(req, "ADMIN", "EDITOR");
        String[] p = path(req);
        if (p.length != 1) throw new IllegalArgumentException("Id requerido");
        Long id = RequestUtil.longValue(p[0]);
        if (id == null) throw new IllegalArgumentException("Id invalido");
        Categoria item = service.update(id, JsonUtil.read(req, Categoria.class));
        audit.record(actor, "ACTUALIZAR_CATEGORIA", "categoria", id, RequestUtil.clientIp(req), item.getNombre());
        JsonUtil.ok(res, item);
    }

    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireRoles(req, "ADMIN", "EDITOR");
        String[] p = path(req);
        Long id = p.length == 1 ? RequestUtil.longValue(p[0]) : null;
        if (id == null) throw new IllegalArgumentException("Id invalido");
        service.delete(id);
        audit.record(actor, "DESACTIVAR_CATEGORIA", "categoria", id, RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("message", "Categoria desactivada"));
    }
}
