package com.jeanpipi.servlets;

// API de biblioteca multimedia.
import com.jeanpipi.modelos.Medio;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.servicios.MediaService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/media/*")
@MultipartConfig(maxFileSize = 10485760L, maxRequestSize = 11534336L)
public class MediaServlet extends BaseApiServlet {
    private final MediaService service = new MediaService();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        requireRoles(req, "ADMIN", "EDITOR", "AUTOR");
        JsonUtil.ok(res, service.list());
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = requireRoles(req, "ADMIN", "EDITOR", "AUTOR");
        Medio item = service.upload(user, req.getPart("file"), req.getContextPath());
        audit.record(user, "SUBIR_MEDIO", "medio", item.getId(), RequestUtil.clientIp(req), item.getNombreArchivo());
        JsonUtil.created(res, item);
    }

    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = requireRoles(req, "ADMIN", "EDITOR");
        String[] p = path(req);
        Long id = p.length == 1 ? RequestUtil.longValue(p[0]) : null;
        if (id == null) throw new IllegalArgumentException("Id invalido");
        service.delete(id);
        audit.record(user, "ELIMINAR_MEDIO", "medio", id, RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("message", "Archivo eliminado"));
    }
}
