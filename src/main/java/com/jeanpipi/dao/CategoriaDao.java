package com.jeanpipi.dao;

// Acceso a datos de categorias.
import com.jeanpipi.config.Database;
import com.jeanpipi.modelos.Categoria;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDao {
    public List<Categoria> list(boolean includeInactive) throws SQLException {
        String sql = "SELECT * FROM categorias " + (includeInactive ? "" : "WHERE activa=TRUE ") + "ORDER BY orden,nombre";
        List<Categoria> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) items.add(map(rs));
        }
        return items;
    }

    public Optional<Categoria> findById(long id) throws SQLException {
        return one("SELECT * FROM categorias WHERE id=?", id);
    }

    public Optional<Categoria> findBySlug(String slug) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE slug=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    public Categoria create(Categoria item) throws SQLException {
        String sql = "INSERT INTO categorias(nombre,slug,descripcion,portada_url,activa,orden) VALUES(?,?,?,?,?,?) RETURNING *";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, item);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return map(rs); }
        }
    }

    public Categoria update(long id, Categoria item) throws SQLException {
        String sql = "UPDATE categorias SET nombre=?,slug=?,descripcion=?,portada_url=?,activa=?,orden=?,updated_at=NOW() WHERE id=? RETURNING *";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, item);
            ps.setLong(7, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Categoria no encontrada");
                return map(rs);
            }
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE categorias SET activa=FALSE,updated_at=NOW() WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Optional<Categoria> one(String sql, long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    private void bind(PreparedStatement ps, Categoria item) throws SQLException {
        ps.setString(1, item.getNombre());
        ps.setString(2, item.getSlug());
        ps.setString(3, item.getDescripcion());
        ps.setString(4, item.getPortadaUrl());
        ps.setBoolean(5, item.isActiva());
        ps.setInt(6, item.getOrden());
    }

    private Categoria map(ResultSet rs) throws SQLException {
        Categoria item = new Categoria();
        item.setId(rs.getLong("id"));
        item.setNombre(rs.getString("nombre"));
        item.setSlug(rs.getString("slug"));
        item.setDescripcion(rs.getString("descripcion"));
        item.setPortadaUrl(rs.getString("portada_url"));
        item.setActiva(rs.getBoolean("activa"));
        item.setOrden(rs.getInt("orden"));
        item.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        item.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return item;
    }
}
