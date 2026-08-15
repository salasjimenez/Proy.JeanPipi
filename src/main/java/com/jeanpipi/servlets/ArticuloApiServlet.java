package com.jeanpipi.servlets;

import com.jeanpipi.dto.ArticuloRequest;
import com.jeanpipi.exception.ValidationException;
import com.jeanpipi.modelos.Articulo;
import com.jeanpipi.servicios.ArticuloService;
import com.jeanpipi.util.ApiErrorHandler;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/api/articulos")
public class ArticuloApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ArticuloApiServlet.class.getName());
    private transient ArticuloService articuloService;

    @Override
    public void init() {
        this.articuloService = new ArticuloService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idValue = request.getParameter("id");
            if (idValue == null || idValue.isBlank()) {
                List<Articulo> articulos = articuloService.listar();
                JsonUtil.escribir(response, HttpServletResponse.SC_OK, articulos);
                return;
            }

            int id = parseId(idValue);
            JsonUtil.escribir(response, HttpServletResponse.SC_OK, articuloService.obtener(id));
        } catch (Exception e) {
            ApiErrorHandler.responder(response, e, LOGGER);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!autorizarAdmin(request, response)) {
            return;
        }

        try {
            Articulo articulo = articuloService.crear(leerRequest(request));
            JsonUtil.escribir(response, HttpServletResponse.SC_CREATED, articulo);
        } catch (Exception e) {
            ApiErrorHandler.responder(response, e, LOGGER);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!autorizarAdmin(request, response)) {
            return;
        }

        try {
            int id = RequestUtil.parametroEntero(request, "id");
            Articulo articulo = articuloService.actualizar(id, leerRequest(request));
            JsonUtil.escribir(response, HttpServletResponse.SC_OK, articulo);
        } catch (Exception e) {
            ApiErrorHandler.responder(response, e, LOGGER);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!autorizarAdmin(request, response)) {
            return;
        }

        try {
            int id = RequestUtil.parametroEntero(request, "id");
            articuloService.eliminar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            ApiErrorHandler.responder(response, e, LOGGER);
        }
    }

    private ArticuloRequest leerRequest(HttpServletRequest request) throws IOException {
        if (RequestUtil.esJson(request)) {
            return RequestUtil.leerJson(request, ArticuloRequest.class);
        }

        ArticuloRequest articulo = new ArticuloRequest();
        articulo.setTitulo(primero(request, "titulo", "title"));
        articulo.setDescripcion(primero(request, "descripcion", "description"));
        articulo.setContenido(primero(request, "contenido", "content"));
        articulo.setImagen(primero(request, "imagen", "image"));
        articulo.setCategoriaId(parseOptionalInteger(request.getParameter("categoriaId")));
        articulo.setAutorId(parseOptionalInteger(request.getParameter("autorId")));
        return articulo;
    }

    private String primero(HttpServletRequest request, String primary, String legacy) {
        String value = request.getParameter(primary);
        return value != null ? value : request.getParameter(legacy);
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("Uno de los identificadores enviados no es valido.");
        }
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("El ID del articulo debe ser numerico.");
        }
    }

    private boolean autorizarAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.autenticado(request)) {
            JsonUtil.mensaje(response, HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesion.");
            return false;
        }
        if (!SessionUtil.esAdmin(request)) {
            JsonUtil.mensaje(response, HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para esta operacion.");
            return false;
        }
        return true;
    }
}
