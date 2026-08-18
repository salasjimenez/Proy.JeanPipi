package com.jeanpipi.dao;

// Acceso a datos de favoritos.
import com.jeanpipi.config.Database;
import com.jeanpipi.modelos.Articulo;
import com.jeanpipi.util.HtmlUtil;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDao {
    public boolean toggle(long userId, long articleId) throws SQLException {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                boolean exists;
                try (PreparedStatement find = c.prepareStatement("SELECT 1 FROM favoritos WHERE user_id=? AND articulo_id=?")) {
                    find.setLong(1, userId); find.setLong(2, articleId);
                    try (ResultSet rs = find.executeQuery()) { exists = rs.next(); }
                }
                if (exists) {
                    try (PreparedStatement del = c.prepareStatement("DELETE FROM favoritos WHERE user_id=? AND articulo_id=?")) {
                        del.setLong(1, userId); del.setLong(2, articleId); del.executeUpdate();
                    }
                    c.commit();
                    return false;
                }
                try (PreparedStatement ins = c.prepareStatement("INSERT INTO favoritos(user_id,articulo_id) VALUES(?,?)")) {
                    ins.setLong(1, userId); ins.setLong(2, articleId); ins.executeUpdate();
                }
                c.commit();
                return true;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public boolean isFavorite(long userId, long articleId) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT 1 FROM favoritos WHERE user_id=? AND articulo_id=?")) {
            ps.setLong(1, userId); ps.setLong(2, articleId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public List<Articulo> list(long userId) throws SQLException {
        String sql = """
            SELECT a.*, c.nombre AS categoria_nombre,c.slug AS categoria_slug,u.nombre AS autor_nombre,u.avatar_url AS autor_avatar_url,
                   (SELECT COUNT(*) FROM favoritos fx WHERE fx.articulo_id=a.id) AS favoritos_count,
                   (SELECT COUNT(*) FROM comentarios co WHERE co.articulo_id=a.id AND co.estado='APROBADO') AS comentarios_count,
                   0::double precision AS tendencia_score
            FROM favoritos f JOIN articulos a ON a.id=f.articulo_id
            LEFT JOIN categorias c ON c.id=a.categoria_id JOIN usuarios u ON u.id=a.autor_id
            WHERE f.user_id=? AND a.estado='PUBLICADO' ORDER BY f.created_at DESC
            """;
        List<Articulo> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) items.add(map(rs)); }
        }
        return items;
    }

    private Articulo map(ResultSet rs) throws SQLException {
        Articulo a = new Articulo();
        a.setId(rs.getLong("id")); a.setTitulo(rs.getString("titulo")); a.setSlug(rs.getString("slug"));
        a.setExtracto(rs.getString("extracto")); a.setContenido(rs.getString("contenido")); a.setPortadaUrl(rs.getString("portada_url"));
        long categoryId = rs.getLong("categoria_id"); a.setCategoriaId(rs.wasNull() ? null : categoryId);
        a.setCategoriaNombre(rs.getString("categoria_nombre")); a.setCategoriaSlug(rs.getString("categoria_slug"));
        a.setAutorId(rs.getLong("autor_id")); a.setAutorNombre(rs.getString("autor_nombre")); a.setAutorAvatarUrl(rs.getString("autor_avatar_url"));
        a.setEstado(rs.getString("estado")); a.setPublicadoAt(rs.getObject("publicado_at", OffsetDateTime.class));
        a.setProgramadoAt(rs.getObject("programado_at", OffsetDateTime.class)); a.setDestacado(rs.getBoolean("destacado"));
        a.setSeoTitulo(rs.getString("seo_titulo")); a.setSeoDescripcion(rs.getString("seo_descripcion")); a.setSeoCanonical(rs.getString("seo_canonical")); a.setSeoImagen(rs.getString("seo_imagen"));
        a.setVistas(rs.getLong("vistas")); a.setFavoritos(rs.getLong("favoritos_count")); a.setComentarios(rs.getLong("comentarios_count")); a.setTendencia(rs.getDouble("tendencia_score"));
        a.setMinutosLectura(HtmlUtil.readingMinutes(a.getContenido())); a.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class)); a.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return a;
    }
}
