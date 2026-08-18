package com.jeanpipi.filtros;

// Proteccion CSRF de la API.
import com.jeanpipi.util.CsrfUtil;
import com.jeanpipi.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class CsrfFilter implements Filter {
    private static final Set<String> MUTATING = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if (uri.contains("/api/") && MUTATING.contains(req.getMethod()) && !CsrfUtil.valid(req)) {
            JsonUtil.error(res, 403, "Token CSRF invalido");
            return;
        }
        chain.doFilter(request, response);
    }
}
