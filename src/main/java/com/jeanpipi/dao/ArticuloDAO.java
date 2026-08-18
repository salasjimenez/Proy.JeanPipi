package com.jeanpipi.dao;

// Acceso a datos de articulos.
import com.jeanpipi.config.Database;
import com.jeanpipi.modelos.Articulo;
import com.jeanpipi.modelos.PagedResult;
import com.jeanpipi.util.HtmlUtil;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

public class ArticuloDao {
    private static final String SELECT_BASE = """
        SELECT a.*, c.nombre AS categoria_nombre, c.slug AS categoria_slug,
               u.nombre AS autor_nombre, u.avatar_url AS autor_avatar_url,
               (SELECT COUNT(*) FROM favoritos f WHERE f.articulo_id=a.id) AS favoritos_count,
               (SELECT COUNT(*) FROM comentarios co WHERE co.articulo_id=a.id AND co.estado='APROBADO') AS comentarios_count,
               ((SELECT COALESCE(SUM(vd.vistas),0) FROM articulo_vistas_diarias vd WHERE vd.articulo_id=a.id AND vd.fecha>=CURRENT_DATE-6)
                 + (SELECT COUNT(*)*4 FROM favoritos f WHERE f.articulo_id=a.id)
                 + (SELECT COUNT(*)*3 FROM comentarios co WHERE co.articulo_id=a.id AND co.estado='APROBADO'))::double precision AS tendencia_score
        FROM articulos a
        LEFT JOIN categorias c ON c.id=a.categoria_id
        JOIN usuarios u ON u.id=a.autor_id
        """;

    public PagedResult<Articulo> listPublic(String query, String category, String author, String order, int page, int pageSize) throws SQLException {
        List<Object> params = new ArrayList<>();
        String where = publicWhere(query, category, author, params);
        long total = count(where, params);
        String orderBy = switch (order == null ? "latest" : order) {
            case "views" -> "a.vistas DESC, a.publicado_at DESC";
            case "favorites" -> "favoritos_count DESC, a.publicado_at DESC";
            case "trend" -> "tendencia_score DESC, a.publicado_at DESC";
            default -> "a.publicado_at DESC NULLS LAST, a.created_at DESC";
        };
        String sql = SELECT_BASE + where + " ORDER BY " + orderBy + " LIMIT ? OFFSET ?";
        List<Articulo> items = queryList(sql, params, pageSize, (page - 1) * pageSize);
        return new PagedResult<>(items, page, pageSize, total);
    }

    public PagedResult<Articulo> listEditorial(String query, String status, Long authorId, int page, int pageSize) throws SQLException {
        List<String> clauses = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            clauses.add("(LOWER(a.titulo) LIKE ? OR LOWER(COALESCE(a.extracto,'')) LIKE ?)");
            String term = "%" + query.trim().toLowerCase() + "%";
            params.add(term); params.add(term);
        }
        if (status != null && !status.isBlank()) {
            clauses.add("a.estado=?");
            params.add(status);
        }
        if (authorId != null) {
            clauses.add("a.autor_id=?");
            params.add(authorId);
        }
        String where = clauses.isEmpty() ? "" : "WHERE " + String.join(" AND ", clauses);
        long total = count(where, params);
        String sql = SELECT_BASE + where + " ORDER BY a.updated_at DESC LIMIT ? OFFSET ?";
        List<Articulo> items = queryList(sql, params, pageSize, (page - 1) * pageSize);
        return new PagedResult<>(items, page, pageSize, total);
    }

    public Optional<Articulo> findById(long id) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.id=?";
        return one(sql, id);
    }

    public Optional<Articulo> findBySlug(String slug) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.slug=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    public Optional<Articulo> findPublishedBySlug(String slug) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.slug=? AND a.estado='PUBLICADO' AND COALESCE(a.publicado_at,NOW())<=NOW()";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    public Articulo create(Articulo item) throws SQLException {
        String sql = """
            INSERT INTO articulos(titulo,slug,extracto,contenido,portada_url,categoria_id,autor_id,estado,publicado_at,programado_at,destacado,seo_titulo,seo_descripcion,seo_canonical,seo_imagen)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id
            """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindArticle(ps, item);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return findById(rs.getLong(1)).orElseThrow();
            }
        }
    }

    public Articulo update(long id, Articulo item, long editorId) throws SQLException {
        Optional<Articulo> existing = findById(id);
        if (existing.isEmpty()) {
            throw new SQLException("Articulo no encontrado");
        }
        saveVersion(existing.get(), editorId);
        String sql = """
            UPDATE articulos SET titulo=?,slug=?,extracto=?,contenido=?,portada_url=?,categoria_id=?,autor_id=?,estado=?,publicado_at=?,programado_at=?,destacado=?,seo_titulo=?,seo_descripcion=?,seo_canonical=?,seo_imagen=?,updated_at=NOW()
            WHERE id=?
            """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindArticle(ps, item);
            ps.setLong(16, id);
            ps.executeUpdate();
        }
        return findById(id).orElseThrow();
    }

    public void archive(long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE articulos SET estado='ARCHIVADO',destacado=FALSE,updated_at=NOW() WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> versions(long articleId) throws SQLException {
        String sql = "SELECT av.id,av.titulo,av.extracto,av.contenido,av.created_at,u.nombre AS editor FROM articulo_versiones av LEFT JOIN usuarios u ON u.id=av.editor_id WHERE av.articulo_id=? ORDER BY av.created_at DESC";
        List<Map<String, Object>> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, articleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("titulo", rs.getString("titulo"));
                    row.put("extracto", rs.getString("extracto"));
                    row.put("contenido", rs.getString("contenido"));
                    row.put("editor", rs.getString("editor"));
                    row.put("createdAt", rs.getObject("created_at", OffsetDateTime.class));
                    items.add(row);
                }
            }
        }
        return items;
    }

    public Articulo restoreVersion(long articleId, long versionId, long editorId) throws SQLException {
        Articulo current = findById(articleId).orElseThrow(() -> new SQLException("Articulo no encontrado"));
        String select = "SELECT titulo,extracto,contenido FROM articulo_versiones WHERE id=? AND articulo_id=?";
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                String title;
                String excerpt;
                String content;
                try (PreparedStatement ps = c.prepareStatement(select)) {
                    ps.setLong(1, versionId);
                    ps.setLong(2, articleId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Version no encontrada");
                        title = rs.getString("titulo");
                        excerpt = rs.getString("extracto");
                        content = rs.getString("contenido");
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO articulo_versiones(articulo_id,titulo,extracto,contenido,editor_id) VALUES(?,?,?,?,?)")) {
                    ps.setLong(1, articleId);
                    ps.setString(2, current.getTitulo());
                    ps.setString(3, current.getExtracto());
                    ps.setString(4, current.getContenido());
                    ps.setLong(5, editorId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE articulos SET titulo=?,extracto=?,contenido=?,updated_at=NOW() WHERE id=?")) {
                    ps.setString(1, title);
                    ps.setString(2, excerpt);
                    ps.setString(3, content);
                    ps.setLong(4, articleId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
        return findById(articleId).orElseThrow();
    }

    public List<Articulo> related(long articleId, Long categoryId, int limit) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.id<>? AND a.estado='PUBLICADO' AND a.publicado_at<=NOW() AND (? IS NULL OR a.categoria_id=?) ORDER BY a.publicado_at DESC LIMIT ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, articleId);
            if (categoryId == null) {
                ps.setNull(2, Types.BIGINT); ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(2, categoryId); ps.setLong(3, categoryId);
            }
            ps.setInt(4, limit);
            List<Articulo> items = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) items.add(map(rs)); }
            return items;
        }
    }

    public List<Articulo> featured(int limit) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.estado='PUBLICADO' AND a.publicado_at<=NOW() AND a.destacado=TRUE ORDER BY a.publicado_at DESC LIMIT ?";
        return queryList(sql, List.of(), limit, null);
    }

    public List<Articulo> trending(int limit) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.estado='PUBLICADO' AND a.publicado_at<=NOW() ORDER BY tendencia_score DESC,a.publicado_at DESC LIMIT ?";
        return queryList(sql, List.of(), limit, null);
    }

    public void incrementView(long articleId) throws SQLException {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement("UPDATE articulos SET vistas=vistas+1 WHERE id=? AND estado='PUBLICADO'")) {
                    ps.setLong(1, articleId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO articulo_vistas_diarias(articulo_id,fecha,vistas) VALUES(?,CURRENT_DATE,1) ON CONFLICT(articulo_id,fecha) DO UPDATE SET vistas=articulo_vistas_diarias.vistas+1")) {
                    ps.setLong(1, articleId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public int publishDueScheduled() throws SQLException {
        String sql = "UPDATE articulos SET estado='PUBLICADO',publicado_at=COALESCE(programado_at,NOW()),updated_at=NOW() WHERE estado='PROGRAMADO' AND programado_at IS NOT NULL AND programado_at<=NOW()";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    public boolean slugExists(String slug, Long exceptId) throws SQLException {
        String sql = "SELECT 1 FROM articulos WHERE slug=?" + (exceptId == null ? "" : " AND id<>?") + " LIMIT 1";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, slug);
            if (exceptId != null) ps.setLong(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private void saveVersion(Articulo item, long editorId) throws SQLException {
        String sql = "INSERT INTO articulo_versiones(articulo_id,titulo,extracto,contenido,editor_id) VALUES(?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getTitulo());
            ps.setString(3, item.getExtracto());
            ps.setString(4, item.getContenido());
            ps.setLong(5, editorId);
            ps.executeUpdate();
        }
    }

    private long count(String where, List<Object> params) throws SQLException {
        String sql = "SELECT COUNT(*) FROM articulos a LEFT JOIN categorias c ON c.id=a.categoria_id JOIN usuarios u ON u.id=a.autor_id " + where;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindObjects(ps, params, 1);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
        }
    }

    private String publicWhere(String query, String category, String author, List<Object> params) {
        List<String> clauses = new ArrayList<>();
        clauses.add("a.estado='PUBLICADO'");
        clauses.add("COALESCE(a.publicado_at,NOW())<=NOW()");
        if (query != null && !query.isBlank()) {
            clauses.add("(LOWER(a.titulo) LIKE ? OR LOWER(COALESCE(a.extracto,'')) LIKE ? OR LOWER(COALESCE(a.contenido,'')) LIKE ? OR LOWER(COALESCE(c.nombre,'')) LIKE ? OR LOWER(u.nombre) LIKE ?)");
            String term = "%" + query.trim().toLowerCase() + "%";
            for (int i = 0; i < 5; i++) params.add(term);
        }
        if (category != null && !category.isBlank()) {
            clauses.add("c.slug=?");
            params.add(category.trim());
        }
        if (author != null && !author.isBlank()) {
            clauses.add("LOWER(u.nombre) LIKE ?");
            params.add("%" + author.trim().toLowerCase() + "%");
        }
        return "WHERE " + String.join(" AND ", clauses);
    }

    private List<Articulo> queryList(String sql, List<Object> params, Integer limit, Integer offset) throws SQLException {
        List<Articulo> items = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int index = bindObjects(ps, params, 1);
            if (limit != null) ps.setInt(index++, limit);
            if (offset != null) ps.setInt(index, offset);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) items.add(map(rs)); }
        }
        return items;
    }

    private int bindObjects(PreparedStatement ps, List<Object> params, int start) throws SQLException {
        int index = start;
        for (Object param : params) ps.setObject(index++, param);
        return index;
    }

    private Optional<Articulo> one(String sql, long id) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    private void bindArticle(PreparedStatement ps, Articulo item) throws SQLException {
        ps.setString(1, item.getTitulo());
        ps.setString(2, item.getSlug());
        ps.setString(3, item.getExtracto());
        ps.setString(4, item.getContenido());
        ps.setString(5, item.getPortadaUrl());
        if (item.getCategoriaId() == null) ps.setNull(6, Types.BIGINT); else ps.setLong(6, item.getCategoriaId());
        ps.setLong(7, item.getAutorId());
        ps.setString(8, item.getEstado());
        ps.setObject(9, item.getPublicadoAt());
        ps.setObject(10, item.getProgramadoAt());
        ps.setBoolean(11, item.isDestacado());
        ps.setString(12, item.getSeoTitulo());
        ps.setString(13, item.getSeoDescripcion());
        ps.setString(14, item.getSeoCanonical());
        ps.setString(15, item.getSeoImagen());
    }

    private Articulo map(ResultSet rs) throws SQLException {
        Articulo a = new Articulo();
        a.setId(rs.getLong("id"));
        a.setTitulo(rs.getString("titulo"));
        a.setSlug(rs.getString("slug"));
        a.setExtracto(rs.getString("extracto"));
        a.setContenido(rs.getString("contenido"));
        a.setPortadaUrl(rs.getString("portada_url"));
        long categoryId = rs.getLong("categoria_id");
        a.setCategoriaId(rs.wasNull() ? null : categoryId);
        a.setCategoriaNombre(rs.getString("categoria_nombre"));
        a.setCategoriaSlug(rs.getString("categoria_slug"));
        a.setAutorId(rs.getLong("autor_id"));
        a.setAutorNombre(rs.getString("autor_nombre"));
        a.setAutorAvatarUrl(rs.getString("autor_avatar_url"));
        a.setEstado(rs.getString("estado"));
        a.setPublicadoAt(rs.getObject("publicado_at", OffsetDateTime.class));
        a.setProgramadoAt(rs.getObject("programado_at", OffsetDateTime.class));
        a.setDestacado(rs.getBoolean("destacado"));
        a.setSeoTitulo(rs.getString("seo_titulo"));
        a.setSeoDescripcion(rs.getString("seo_descripcion"));
        a.setSeoCanonical(rs.getString("seo_canonical"));
        a.setSeoImagen(rs.getString("seo_imagen"));
        a.setVistas(rs.getLong("vistas"));
        a.setFavoritos(rs.getLong("favoritos_count"));
        a.setComentarios(rs.getLong("comentarios_count"));
        a.setTendencia(rs.getDouble("tendencia_score"));
        a.setMinutosLectura(HtmlUtil.readingMinutes(a.getContenido()));
        a.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        a.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return a;
    }
}
