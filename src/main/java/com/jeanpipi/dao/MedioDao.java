package com.jeanpipi.dao;

// Acceso a datos multimedia.
import com.jeanpipi.config.Database;
import com.jeanpipi.modelos.Medio;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedioDao {
    public Medio create(Medio item) throws SQLException {
        String sql = "INSERT INTO medios(user_id,nombre_archivo,nombre_guardado,url,mime_type,tamano) VALUES(?,?,?,?,?,?) RETURNING *";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (item.getUserId() == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, item.getUserId());
            ps.setString(2, item.getNombreArchivo()); ps.setString(3, item.getNombreGuardado()); ps.setString(4, item.getUrl());
            ps.setString(5, item.getMimeType()); ps.setLong(6, item.getTamano());
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return map(rs); }
        }
    }

    public List<Medio> list() throws SQLException {
        List<Medio> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM medios ORDER BY created_at DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) items.add(map(rs));
        }
        return items;
    }

    public Optional<Medio> find(long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM medios WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM medios WHERE id=?")) {
            ps.setLong(1, id); ps.executeUpdate();
        }
    }

    private Medio map(ResultSet rs) throws SQLException {
        Medio item = new Medio();
        item.setId(rs.getLong("id"));
        long userId = rs.getLong("user_id"); item.setUserId(rs.wasNull() ? null : userId);
        item.setNombreArchivo(rs.getString("nombre_archivo")); item.setNombreGuardado(rs.getString("nombre_guardado")); item.setUrl(rs.getString("url"));
        item.setMimeType(rs.getString("mime_type")); item.setTamano(rs.getLong("tamano")); item.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        return item;
    }
}
