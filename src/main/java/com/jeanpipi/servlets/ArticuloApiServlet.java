package com.jeanpipi.servlets;

import com.google.gson.Gson;
import com.jeanpipi.dao.ArticuloDAO;
import com.jeanpipi.modelos.Articulo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/articulos")
public class ArticuloApiServlet extends HttpServlet {

    private final ArticuloDAO articuloDAO = new ArticuloDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Articulo> articulos = articuloDAO.obtenerTodos();
        String json = this.gson.toJson(articulos);

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        Articulo articulo = null;
        String contentType = request.getContentType();

        try {
            // 1. Petición en formato JSON desde fetch()
            if (contentType != null && contentType.toLowerCase().contains("application/json")) {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String body = sb.toString().trim();
                if (!body.isEmpty()) {
                    articulo = gson.fromJson(body, Articulo.class);
                }
            } 
            
            // 2. Petición en formato Formulario HTML
            if (articulo == null || articulo.getTitulo() == null) {
                articulo = new Articulo();
                articulo.setTitulo(request.getParameter("titulo"));
                articulo.setDescripcion(request.getParameter("descripcion"));
                articulo.setContenido(request.getParameter("contenido"));
                articulo.setImagen(request.getParameter("imagen"));
                
                String catStr = request.getParameter("categoriaId");
                if (catStr != null && !catStr.trim().isEmpty()) {
                    try {
                        articulo.setCategoriaId(Integer.parseInt(catStr.trim()));
                    } catch (NumberFormatException ignored) {
                        articulo.setCategoriaId(1);
                    }
                } else {
                    articulo.setCategoriaId(1);
                }
            }

            // Validar título
            if (articulo == null || articulo.getTitulo() == null || articulo.getTitulo().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"El título del artículo es obligatorio\"}");
                out.flush();
                return;
            }

            // Guardar en la base de datos
            boolean guardado = articuloDAO.guardar(articulo);
            if (guardado) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"mensaje\": \"Artículo guardado exitosamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"No se pudo guardar en PostgreSQL\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Error procesando la solicitud: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}