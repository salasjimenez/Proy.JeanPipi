package com.jeanpipi.servlets;

// Pagina publica de articulo por slug.
import com.jeanpipi.modelos.Articulo;
import com.jeanpipi.servicios.ArticuloService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/articulo/*")
public class ArticlePageServlet extends HttpServlet {
    private final ArticuloService service = new ArticuloService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try {
            String slug = req.getPathInfo();
            if (slug == null || slug.length() < 2) { res.sendError(404); return; }
            Articulo article = service.publishedBySlug(slug.substring(1));
            req.setAttribute("article", article);
            req.getRequestDispatcher("/articulo.jsp").forward(req, res);
        } catch (Exception ex) {
            res.sendError(404);
        }
    }
}
