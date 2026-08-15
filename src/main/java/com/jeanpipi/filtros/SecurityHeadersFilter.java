package com.jeanpipi.filtros;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        httpResponse.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; "
                        + "script-src 'self' https://cdn.jsdelivr.net; "
                        + "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; "
                        + "font-src 'self' https://cdn.jsdelivr.net https://fonts.gstatic.com; "
                        + "img-src 'self' data: http: https:; "
                        + "connect-src 'self'; object-src 'none'; base-uri 'self'; "
                        + "frame-ancestors 'none'; form-action 'self'");

        chain.doFilter(request, response);
    }
}
