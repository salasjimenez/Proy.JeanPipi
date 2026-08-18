package com.jeanpipi.dao;

// Acceso a datos de comentarios.
import com.jeanpipi.config.Database;
import com.jeanpipi.modelos.Comentario;
import com.jeanpipi.modelos.PagedResult;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDao {
    public Comentario create(Comentario item) throws SQLException {
        String sql = "INSERT INTO comentarios(articulo_id,user_id,nombre,email,contenido,estado) VALUES(?,?,?,?,?,'PENDIENTE') RETURNING *";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, item.getArticuloId());
            if (item.getUserId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, item.getUserId());
            ps.setString(3, item.getNombre());
            ps.setString(4, item.getEmail());
            ps.setString(5, item.getContenido());
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return map(rs); }
        }
    }

    public List<Comentario> approvedForArticle(long articleId) throws SQLException {
        String sql = "SELECT * FROM comentarios WHERE articulo_id=? AND estado='APROBADO' ORDER BY created_at DESC";
        List<Comentario> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, articleId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) items.add(map(rs)); }
        }
        return items;
    }

    public PagedResult<Comentario> listAdmin(String status, int page, int pageSize) throws SQLException {
        String where = status == null || status.isBlank() ? "" : "WHERE estado=?";
        long total;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM comentarios " + where)) {
            if (!where.isBlank()) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); total = rs.getLong(1); }
        }
        String sql = "SELECT * FROM comentarios " + where + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Comentario> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int index = 1;
            if (!where.isBlank()) ps.setString(index++, status);
            ps.setInt(index++, pageSize);
            ps.setInt(index, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) items.add(map(rs)); }
        }
        return new PagedResult<>(items, page, pageSize, total);
    }

    public void moderate(long id, String status) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE comentarios SET estado=?,updated_at=NOW() WHERE id=?")) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM comentarios WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Comentario map(ResultSet rs) throws SQLException {
        Comentario item = new Comentario();
        item.setId(rs.getLong("id"));
        item.setArticuloId(rs.getLong("articulo_id"));
        long userId = rs.getLong("user_id");
        item.setUserId(rs.wasNull() ? null : userId);
        item.setNombre(rs.getString("nombre"));
        item.setEmail(rs.getString("email"));
        item.setContenido(rs.getString("contenido"));
        item.setEstado(rs.getString("estado"));
        item.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        item.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return item;
    }
}
