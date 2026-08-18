package com.jeanpipi.servlets;

// Pagina publica de categoria por slug.
import com.jeanpipi.modelos.Categoria;
import com.jeanpipi.servicios.CategoriaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/categoria/*")
public class CategoryPageServlet extends HttpServlet {
    private final CategoriaService service = new CategoriaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try {
            String slug = req.getPathInfo();
            if (slug == null || slug.length() < 2) { res.sendError(404); return; }
            Categoria category = service.bySlug(slug.substring(1));
            if (!category.isActiva()) { res.sendError(404); return; }
            req.setAttribute("category", category);
            req.getRequestDispatcher("/categoria.jsp").forward(req, res);
        } catch (Exception ex) {
            res.sendError(404);
        }
    }
}
