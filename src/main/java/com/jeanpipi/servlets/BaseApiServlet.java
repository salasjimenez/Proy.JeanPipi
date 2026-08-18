package com.jeanpipi.servlets;

// Base comun de los endpoints JSON.
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public abstract class BaseApiServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(BaseApiServlet.class);

    @Override
    protected final void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            switch (req.getMethod()) {
                case "GET" -> handleGet(req, res);
                case "POST" -> handlePost(req, res);
                case "PUT" -> handlePut(req, res);
                case "PATCH" -> handlePatch(req, res);
                case "DELETE" -> handleDelete(req, res);
                default -> JsonUtil.error(res, 405, "Metodo no permitido");
            }
        } catch (SecurityException ex) {
            JsonUtil.error(res, 403, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JsonUtil.error(res, 400, ex.getMessage());
        } catch (SQLException ex) {
            LOG.error("Error de base de datos", ex);
            JsonUtil.error(res, 500, "Error interno de datos");
        } catch (Exception ex) {
            LOG.error("Error no controlado", ex);
            JsonUtil.error(res, 500, "Error interno del servidor");
        }
    }

    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception { JsonUtil.error(res, 405, "Metodo no permitido"); }
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception { JsonUtil.error(res, 405, "Metodo no permitido"); }
    protected void handlePut(HttpServletRequest req, HttpServletResponse res) throws Exception { JsonUtil.error(res, 405, "Metodo no permitido"); }
    protected void handlePatch(HttpServletRequest req, HttpServletResponse res) throws Exception { JsonUtil.error(res, 405, "Metodo no permitido"); }
    protected void handleDelete(HttpServletRequest req, HttpServletResponse res) throws Exception { JsonUtil.error(res, 405, "Metodo no permitido"); }

    protected Usuario requireUser(HttpServletRequest req) {
        Usuario user = SecurityUtil.user(req);
        if (user == null) throw new SecurityException("Debes iniciar sesion");
        return user;
    }

    protected Usuario requireRoles(HttpServletRequest req, String... roles) {
        Usuario user = requireUser(req);
        if (!SecurityUtil.hasRole(user, roles)) throw new SecurityException("Acceso denegado");
        return user;
    }

    protected String[] path(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) return new String[0];
        return java.util.Arrays.stream(path.split("/")).filter(s -> !s.isBlank()).toArray(String[]::new);
    }
}
