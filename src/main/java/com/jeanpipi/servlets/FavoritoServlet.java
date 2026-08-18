package com.jeanpipi.servlets;

// API de favoritos.
import com.jeanpipi.dao.FavoritoDao;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuditService;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet("/api/v1/favorites/*")
public class FavoritoServlet extends BaseApiServlet {
    private final FavoritoDao dao = new FavoritoDao();
    private final AuditService audit = new AuditService();

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = requireUser(req);
        String[] p = path(req);
        if (p.length == 1) {
            Long articleId = RequestUtil.longValue(p[0]);
            if (articleId == null) throw new IllegalArgumentException("Id invalido");
            JsonUtil.ok(res, Map.of("favorite", dao.isFavorite(user.getId(), articleId)));
            return;
        }
        JsonUtil.ok(res, dao.list(user.getId()));
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Usuario user = requireUser(req);
        String[] p = path(req);
        Long articleId = p.length == 1 ? RequestUtil.longValue(p[0]) : null;
        if (articleId == null) throw new IllegalArgumentException("Articulo requerido");
        boolean favorite = dao.toggle(user.getId(), articleId);
        audit.record(user, favorite ? "AGREGAR_FAVORITO" : "QUITAR_FAVORITO", "articulo", articleId, RequestUtil.clientIp(req), null);
        JsonUtil.ok(res, Map.of("favorite", favorite));
    }
}
