package com.jeanpipi.servlets;

import com.jeanpipi.dto.AuthRequest;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.servicios.AuthService;
import com.jeanpipi.util.ApiErrorHandler;
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;
import com.jeanpipi.util.SessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private transient AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("autenticado", SessionUtil.autenticado(request));
        if (session != null) {
            result.put("nombre", session.getAttribute(SessionUtil.USER_NAME));
            result.put("rol", session.getAttribute(SessionUtil.USER_ROLE));
        }
        JsonUtil.escribir(response, HttpServletResponse.SC_OK, result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean json = RequestUtil.esJson(request);
        try {
            AuthRequest authRequest = leerRequest(request, json);
            if ("logout".equalsIgnoreCase(authRequest.getAction())) {
                cerrarSesion(request);
                responderSalida(request, response, json);
                return;
            }

            Optional<Usuario> authenticated = authService.autenticar(
                    authRequest.getEmail(), authRequest.getContrasena());

            if (authenticated.isEmpty()) {
                if (json) {
                    JsonUtil.mensaje(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Correo o contrasena incorrectos.");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
                }
                return;
            }

            Usuario usuario = authenticated.get();
            iniciarSesion(request, usuario);
            String destino = "ADMIN".equalsIgnoreCase(usuario.getRol()) ? "/admin.jsp" : "/index.jsp";

            if (json) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("ok", true);
                result.put("nombre", usuario.getNombre());
                result.put("rol", usuario.getRol());
                result.put("redirect", request.getContextPath() + destino);
                JsonUtil.escribir(response, HttpServletResponse.SC_OK, result);
            } else {
                response.sendRedirect(request.getContextPath() + destino);
            }
        } catch (Exception e) {
            if (json) {
                ApiErrorHandler.responder(response, e, LOGGER);
            } else {
                LOGGER.warning("Solicitud de autenticacion invalida: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
            }
        }
    }

    private AuthRequest leerRequest(HttpServletRequest request, boolean json) throws IOException {
        if (json) {
            return RequestUtil.leerJson(request, AuthRequest.class);
        }

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(request.getParameter("email"));
        authRequest.setContrasena(request.getParameter("contrasena"));
        authRequest.setAction(request.getParameter("action"));
        return authRequest;
    }

    private void iniciarSesion(HttpServletRequest request, Usuario usuario) {
        HttpSession current = request.getSession(false);
        if (current != null) {
            current.invalidate();
        }
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setMaxInactiveInterval(30 * 60);
        session.setAttribute(SessionUtil.USER_ID, usuario.getId());
        session.setAttribute(SessionUtil.USER_NAME, usuario.getNombre());
        session.setAttribute(SessionUtil.USER_ROLE, usuario.getRol());
    }

    private void cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private void responderSalida(HttpServletRequest request, HttpServletResponse response, boolean json) throws IOException {
        if (json) {
            JsonUtil.mensaje(response, HttpServletResponse.SC_OK, "Sesion cerrada correctamente.");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
