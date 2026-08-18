package com.jeanpipi.dao;

// Acceso a registros de auditoria.
import com.jeanpipi.config.Database;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuditoriaDao {
    public void write(Long userId, String action, String entity, String entityId, String ip, String detail) throws SQLException {
        String sql = "INSERT INTO auditoria(user_id,accion,entidad,entidad_id,ip,detalle) VALUES(?,?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (userId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, userId);
            ps.setString(2, action); ps.setString(3, entity); ps.setString(4, entityId); ps.setString(5, ip); ps.setString(6, detail);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> latest(int limit) throws SQLException {
        String sql = "SELECT a.*,u.nombre AS usuario FROM auditoria a LEFT JOIN usuarios u ON u.id=a.user_id ORDER BY a.created_at DESC LIMIT ?";
        List<Map<String, Object>> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id")); row.put("usuario", rs.getString("usuario")); row.put("accion", rs.getString("accion"));
                    row.put("entidad", rs.getString("entidad")); row.put("entidadId", rs.getString("entidad_id")); row.put("ip", rs.getString("ip"));
                    row.put("detalle", rs.getString("detalle")); row.put("createdAt", rs.getObject("created_at", OffsetDateTime.class));
                    items.add(row);
                }
            }
        }
        return items;
    }
}
