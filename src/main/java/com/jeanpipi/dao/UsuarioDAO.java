package com.jeanpipi.dao;

// Acceso a datos de usuarios.
import com.jeanpipi.config.Database;
import com.jeanpipi.modelos.PagedResult;
import com.jeanpipi.modelos.Usuario;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao {
    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE LOWER(email)=LOWER(?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Usuario> findById(long id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Usuario create(String nombre, String email, String passwordHash, String rol) throws SQLException {
        String sql = "INSERT INTO usuarios(nombre,email,password_hash,rol,activo) VALUES(?,?,?,?,TRUE) RETURNING *";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, passwordHash);
            ps.setString(4, rol);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return map(rs);
            }
        }
    }

    public void updateProfile(long id, String nombre, String email, String avatarUrl, String bio, String instagram, String twitter) throws SQLException {
        String sql = "UPDATE usuarios SET nombre=?, email=?, avatar_url=?, bio=?, instagram=?, twitter=?, updated_at=NOW() WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, emptyToNull(avatarUrl));
            ps.setString(4, emptyToNull(bio));
            ps.setString(5, emptyToNull(instagram));
            ps.setString(6, emptyToNull(twitter));
            ps.setLong(7, id);
            ps.executeUpdate();
        }
    }

    public void updateAdmin(long id, String nombre, String rol, boolean activo) throws SQLException {
        String sql = "UPDATE usuarios SET nombre=?, rol=?, activo=?, updated_at=NOW() WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, rol);
            ps.setBoolean(3, activo);
            ps.setLong(4, id);
            ps.executeUpdate();
        }
    }

    public void setActive(long id, boolean active) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE usuarios SET activo=?, updated_at=NOW() WHERE id=?")) {
            ps.setBoolean(1, active);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void setPassword(long id, String passwordHash) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE usuarios SET password_hash=?, failed_attempts=0, locked_until=NULL, updated_at=NOW() WHERE id=?")) {
            ps.setString(1, passwordHash);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void recordFailure(long id) throws SQLException {
        String sql = "UPDATE usuarios SET failed_attempts=failed_attempts+1, locked_until=CASE WHEN failed_attempts+1>=5 THEN NOW()+INTERVAL '15 minutes' ELSE locked_until END, updated_at=NOW() WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void resetFailures(long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE usuarios SET failed_attempts=0, locked_until=NULL, updated_at=NOW() WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public PagedResult<Usuario> list(String query, String role, int page, int pageSize) throws SQLException {
        List<Object> params = new ArrayList<>();
        String where = where(query, role, params);
        long total;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM usuarios " + where)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); total = rs.getLong(1); }
        }
        List<Usuario> items = new ArrayList<>();
        String sql = "SELECT * FROM usuarios " + where + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bind(ps, params);
            ps.setInt(i++, pageSize);
            ps.setInt(i, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(map(rs));
            }
        }
        return new PagedResult<>(items, page, pageSize, total);
    }

    public List<Usuario> listAuthors() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE activo=TRUE AND rol IN ('ADMIN','EDITOR','AUTOR') ORDER BY nombre";
        List<Usuario> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) items.add(map(rs));
        }
        return items;
    }

    private String where(String query, String role, List<Object> params) {
        List<String> clauses = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            clauses.add("(LOWER(nombre) LIKE ? OR LOWER(email) LIKE ?)");
            String term = "%" + query.trim().toLowerCase() + "%";
            params.add(term); params.add(term);
        }
        if (role != null && !role.isBlank()) {
            clauses.add("rol=?");
            params.add(role);
        }
        return clauses.isEmpty() ? "" : "WHERE " + String.join(" AND ", clauses);
    }

    private int bind(PreparedStatement ps, List<Object> params) throws SQLException {
        int index = 1;
        for (Object value : params) ps.setObject(index++, value);
        return index;
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setBio(rs.getString("bio"));
        u.setInstagram(rs.getString("instagram"));
        u.setTwitter(rs.getString("twitter"));
        u.setFailedAttempts(rs.getInt("failed_attempts"));
        u.setLockedUntil(rs.getObject("locked_until", OffsetDateTime.class));
        u.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        u.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return u;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
