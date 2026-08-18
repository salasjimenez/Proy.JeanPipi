package com.jeanpipi.filtros;

// Acceso a paginas privadas.
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.SecurityUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class PageAccessFilter implements Filter {
    private static final Set<String> PRIVATE_PAGES = Set.of("/perfil.jsp", "/favoritos.jsp");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Usuario user = SecurityUtil.user(req);
        if (PRIVATE_PAGES.contains(path) && user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp?next=" + path.substring(1));
            return;
        }
        if (("/admin.jsp".equals(path) || "/preview.jsp".equals(path)) && !SecurityUtil.canEditContent(user)) {
            String next = "/preview.jsp".equals(path) ? "preview.jsp" : "admin.jsp";
            if (user == null) res.sendRedirect(req.getContextPath() + "/login.jsp?next=" + next); else res.sendError(403);
            return;
        }
        chain.doFilter(request, response);
    }
}
