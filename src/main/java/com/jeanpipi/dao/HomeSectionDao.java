package com.jeanpipi.dao;

// Acceso a secciones de portada.
import com.jeanpipi.config.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeSectionDao {
    public List<Map<String, Object>> list() throws SQLException {
        List<Map<String, Object>> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM secciones_portada ORDER BY posicion"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", rs.getLong("id")); row.put("clave", rs.getString("clave")); row.put("titulo", rs.getString("titulo"));
                row.put("habilitada", rs.getBoolean("habilitada")); row.put("posicion", rs.getInt("posicion")); row.put("limiteItems", rs.getInt("limite_items"));
                items.add(row);
            }
        }
        return items;
    }

    public void update(long id, String title, boolean enabled, int position, int limit) throws SQLException {
        String sql = "UPDATE secciones_portada SET titulo=?,habilitada=?,posicion=?,limite_items=?,updated_at=NOW() WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title); ps.setBoolean(2, enabled); ps.setInt(3, position); ps.setInt(4, limit); ps.setLong(5, id); ps.executeUpdate();
        }
    }
}
