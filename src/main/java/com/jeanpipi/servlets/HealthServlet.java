package com.jeanpipi.servlets;

// Endpoint de salud de la aplicacion.
import com.jeanpipi.config.Database;
import com.jeanpipi.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@WebServlet("/api/health")
public class HealthServlet extends BaseApiServlet {
    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (var c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT 1"); ResultSet rs = ps.executeQuery()) {
            rs.next();
            JsonUtil.ok(res, Map.of("status", "UP", "database", rs.getInt(1) == 1 ? "UP" : "DOWN"));
        }
    }
}
