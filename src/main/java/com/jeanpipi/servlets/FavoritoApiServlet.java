package com.jeanpipi.servlets;

import com.jeanpipi.dto.FavoritoRequest;
import com.jeanpipi.servicios.FavoritoService;
import com.jeanpipi.util.ApiErrorHandler;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/api/favoritos")
public class FavoritoApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FavoritoApiServlet.class.getName());
    private transient FavoritoService favoritoService;

    @Override
    public void init() {
        this.favoritoService = new FavoritoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer usuarioId = validarSesion(request, response);
        if (usuarioId == null) {
            return;
        }

        try {
            int articuloId = RequestUtil.parametroEntero(request, "articuloId");
            boolean favorito = favoritoService.existe(usuarioId, articuloId);
            JsonUtil.escribir(response, HttpServletResponse.SC_OK, Map.of("favorito", favorito));
        } catch (Exception e) {
            ApiErrorHandler.responder(response, e, LOGGER);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer usuarioId = validarSesion(request, response);
        if (usuarioId == null) {
            return;
        }

        try {
            Integer articuloId;
            if (RequestUtil.esJson(request)) {
                FavoritoRequest body = RequestUtil.leerJson(request, FavoritoRequest.class);
                articuloId = body.getArticuloId();
            } else {
                articuloId = RequestUtil.parametroEntero(request, "articuloId");
            }

            if (articuloId == null) {
                JsonUtil.mensaje(response, HttpServletResponse.SC_BAD_REQUEST, "Falta el ID del articulo.");
                return;
            }

            boolean favorito = favoritoService.alternar(usuarioId, articuloId);
            JsonUtil.escribir(response, HttpServletResponse.SC_OK, Map.of("favorito", favorito));
        } catch (Exception e) {
            ApiErrorHandler.responder(response, e, LOGGER);
        }
    }

    private Integer validarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer usuarioId = SessionUtil.usuarioId(request);
        if (usuarioId == null) {
            JsonUtil.mensaje(response, HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesion.");
        }
        return usuarioId;
    }
}
