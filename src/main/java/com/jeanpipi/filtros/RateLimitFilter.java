package com.jeanpipi.filtros;

// Limitacion de solicitudes sensibles.
import com.jeanpipi.util.JsonUtil;
import com.jeanpipi.util.RequestUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebFilter("/api/*")
public class RateLimitFilter implements Filter {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();
        String ip = RequestUtil.clientIp(req);
        int limit = 180;
        long windowMillis = 60_000L;
        String bucket = "general";
        if (path.endsWith("/auth/login") && "POST".equals(req.getMethod())) { limit = 10; windowMillis = 15 * 60_000L; bucket = "login"; }
        else if (path.contains("/comments") && "POST".equals(req.getMethod())) { limit = 20; windowMillis = 60 * 60_000L; bucket = "comments"; }
        String key = bucket + ":" + ip;
        Window current = windows.computeIfAbsent(key, k -> new Window());
        if (!current.allow(limit, windowMillis)) {
            res.setHeader("Retry-After", String.valueOf(Math.max(1, windowMillis / 1000)));
            JsonUtil.error(res, 429, "Demasiadas solicitudes");
            return;
        }
        if (windows.size() > 10000) {
            long cleanupMillis = windowMillis * 2;
            windows.entrySet().removeIf(e -> e.getValue().expired(cleanupMillis));
        }
        chain.doFilter(request, response);
    }

    private static final class Window {
        private long started = System.currentTimeMillis();
        private int count;

        synchronized boolean allow(int limit, long duration) {
            long now = System.currentTimeMillis();
            if (now - started >= duration) { started = now; count = 0; }
            count++;
            return count <= limit;
        }

        synchronized boolean expired(long duration) {
            return System.currentTimeMillis() - started > duration;
        }
    }
}
