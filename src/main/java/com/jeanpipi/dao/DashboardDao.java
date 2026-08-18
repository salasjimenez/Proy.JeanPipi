package com.jeanpipi.dao;

// Consultas del dashboard administrativo.
import com.jeanpipi.config.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DashboardDao {
    public Map<String, Object> metrics() throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            data.put("usuarios", scalar(st, "SELECT COUNT(*) FROM usuarios WHERE activo=TRUE"));
            data.put("articulos", scalar(st, "SELECT COUNT(*) FROM articulos"));
            data.put("publicados", scalar(st, "SELECT COUNT(*) FROM articulos WHERE estado='PUBLICADO'"));
            data.put("revision", scalar(st, "SELECT COUNT(*) FROM articulos WHERE estado='EN_REVISION'"));
            data.put("comentariosPendientes", scalar(st, "SELECT COUNT(*) FROM comentarios WHERE estado='PENDIENTE'"));
            data.put("favoritos", scalar(st, "SELECT COUNT(*) FROM favoritos"));
            data.put("vistas", scalar(st, "SELECT COALESCE(SUM(vistas),0) FROM articulos"));
        }
        data.put("viewsDaily", viewsDaily(14));
        data.put("topArticles", topArticles(8));
        return data;
    }

    public List<Map<String, Object>> viewsDaily(int days) throws SQLException {
        String sql = "SELECT fecha,SUM(vistas) AS vistas FROM articulo_vistas_diarias WHERE fecha>=CURRENT_DATE-? GROUP BY fecha ORDER BY fecha";
        List<Map<String, Object>> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, days - 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(Map.of("fecha", rs.getObject("fecha", LocalDate.class), "vistas", rs.getLong("vistas")));
            }
        }
        return items;
    }

    public List<Map<String, Object>> topArticles(int limit) throws SQLException {
        String sql = "SELECT id,titulo,slug,vistas FROM articulos WHERE estado='PUBLICADO' ORDER BY vistas DESC LIMIT ?";
        List<Map<String, Object>> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id")); row.put("titulo", rs.getString("titulo")); row.put("slug", rs.getString("slug")); row.put("vistas", rs.getLong("vistas"));
                    items.add(row);
                }
            }
        }
        return items;
    }

    private long scalar(Statement st, String sql) throws SQLException {
        try (ResultSet rs = st.executeQuery(sql)) { rs.next(); return rs.getLong(1); }
    }
}
