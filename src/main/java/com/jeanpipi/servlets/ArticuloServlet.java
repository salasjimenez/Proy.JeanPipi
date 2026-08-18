package com.jeanpipi.servlets;

// API editorial de articulos.
import com.jeanpipi.modelos.Articulo;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.ArticuloService;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SecurityUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/articles/*")
public class ArticuloServlet extends BaseApiServlet {
    private final ArticuloService service = new ArticuloService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 0) {
            int page = RequestUtil.intParam(req, "page", 1, 1, 100000);
            int size = RequestUtil.intParam(req, "size", 12, 1, 100);
            if ("true".equalsIgnoreCase(req.getParameter("editorial"))) {
                Usuario actor = requireUser(req);
                JsonUtil.ok(res, service.editorialList(actor, req.getParameter("q"), req.getParameter("status"), page, size));
            } else {
                JsonUtil.ok(res, service.publicList(req.getParameter("q"), req.getParameter("category"), req.getParameter("author"), req.getParameter("order"), page, size));
            }
            return;
        }
        if (p.length == 1 && "featured".equals(p[0])) {
            JsonUtil.ok(res, service.featured(RequestUtil.intParam(req, "limit", 1, 1, 12)));
            return;
        }
        if (p.length == 1 && "trending".equals(p[0])) {
            JsonUtil.ok(res, service.trending(RequestUtil.intParam(req, "limit", 6, 1, 24)));
            return;
        }
        Long id = RequestUtil.longValue(p[0]);
        if (id == null) {
            JsonUtil.ok(res, service.publishedBySlug(p[0]));
            return;
        }
        if (p.length == 2 && "related".equals(p[1])) {
            JsonUtil.ok(res, service.related(id, RequestUtil.intParam(req, "limit", 4, 1, 12)));
            return;
        }
        if (p.length == 2 && "versions".equals(p[1])) {
            JsonUtil.ok(res, service.versions(requireUser(req), id));
            return;
        }
        if (p.length == 2 && "preview".equals(p[1])) {
            JsonUtil.ok(res, service.preview(requireUser(req), id));
            return;
        }
        Articulo item = service.byId(id);
        if (!"PUBLICADO".equals(item.getEstado())) {
            Usuario actor = SecurityUtil.user(req);
            if (actor == null || !SecurityUtil.canEditContent(actor)) throw new IllegalArgumentException("Articulo no encontrado");
            item = service.preview(actor, id);
        }
        JsonUtil.ok(res, item);
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String[] p = path(req);
        if (p.length == 2 && "view".equals(p[1])) {
            Long id = RequestUtil.longValue(p[0]);
            if (id == null) throw new IllegalArgumentException("Id invalido");
            service.view(id);
            JsonUtil.ok(res, Map.of("registered", true));
            return;
        }
        if (p.length == 4 && "versions".equals(p[1]) && "restore".equals(p[3])) {
            Long id = RequestUtil.longValue(p[0]);
            Long versionId = RequestUtil.longValue(p[2]);
            if (id == null || versionId == null) throw new IllegalArgumentException("Id invalido");
            Usuario actor = requireUser(req);
            Articulo restored = service.restoreVersion(actor, id, versionId);
            audit.record(actor, "RESTAURAR_VERSION", "articulo", id, RequestUtil.clientIp(req), String.valueOf(versionId));
            JsonUtil.ok(res, restored);
            return;
        }
        if (p.length != 0) throw new IllegalArgumentException("Ruta invalida");
        Usuario actor = requireUser(req);
        Articulo item = JsonUtil.read(req, Articulo.class);
        Articulo created = service.create(actor, item);
        audit.record(actor, "CREAR_ARTICULO", "articulo", created.getId(), RequestUtil.clientIp(req), created.getEstado());
        JsonUtil.created(res, created);
    }

    @Override
    protected void handlePut(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireUser(req);
        String[] p = path(req);
        if (p.length != 1) throw new IllegalArgumentException("Id requerido");
        Long id = RequestUtil.longValue(p[0]);
        if (id == null) throw new IllegalArgumentException("Id invalido");
        Articulo updated = service.update(actor, id, JsonUtil.read(req, Articulo.class));
        audit.record(actor, "ACTUALIZAR_ARTICULO", "articulo", id, RequestUtil.clientIp(req), updated.getEstado());
        JsonUtil.ok(res, updated);
    }

    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario actor = requireUser(req);
        String[] p = path(req);
        if (p.length != 1) throw new IllegalArgumentException("Id requerido");
        Long id = RequestUtil.longValue(p[0]);
        if (id == null) throw new IllegalArgumentException("Id invalido");
        service.archive(actor, id);
        audit.record(actor, "ARCHIVAR_ARTICULO", "articulo", id, RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("message", "Articulo archivado"));
    }
}
