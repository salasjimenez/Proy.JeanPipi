package com.jeanpipi.servicios;

// Servicio editorial de articulos.
import com.jeanpipi.config.AppConfig;
import com.jeanpipi.dao.ArticuloDao;
import com.jeanpipi.modelos.Articulo;
import com.jeanpipi.modelos.PagedResult;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.HtmlUtil;
import com.jeanpipi.util.SecurityUtil;
import com.jeanpipi.util.SlugUtil;
import com.jeanpipi.util.ValidationUtil;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArticuloService {
    private static final Set<String> STATES = Set.of("BORRADOR", "EN_REVISION", "PROGRAMADO", "PUBLICADO", "ARCHIVADO");
    private final ArticuloDao dao = new ArticuloDao();

    public PagedResult<Articulo> publicList(String q, String category, String author, String order, int page, int size) throws SQLException {
        return dao.listPublic(q, category, author, order, page, size);
    }

    public PagedResult<Articulo> editorialList(Usuario actor, String q, String status, int page, int size) throws SQLException {
        if (!SecurityUtil.canEditContent(actor)) throw new SecurityException("Acceso denegado");
        Long authorId = "AUTOR".equals(actor.getRol()) ? actor.getId() : null;
        return dao.listEditorial(q, status, authorId, page, size);
    }

    public Articulo publishedBySlug(String slug) throws SQLException {
        return dao.findPublishedBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Articulo no encontrado"));
    }

    public Articulo byId(long id) throws SQLException {
        return dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Articulo no encontrado"));
    }

    public Articulo bySlug(String slug) throws SQLException {
        return dao.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Articulo no encontrado"));
    }

    public Articulo preview(Usuario actor, long id) throws SQLException {
        Articulo item = byId(id);
        requireArticleAccess(actor, item);
        return item;
    }

    public Articulo create(Usuario actor, Articulo item) throws SQLException {
        if (!SecurityUtil.canEditContent(actor)) throw new SecurityException("Acceso denegado");
        normalize(actor, item, null);
        return dao.create(item);
    }

    public Articulo update(Usuario actor, long id, Articulo item) throws SQLException {
        Articulo current = byId(id);
        requireArticleAccess(actor, current);
        normalize(actor, item, id);
        if ("AUTOR".equals(actor.getRol())) item.setAutorId(actor.getId());
        return dao.update(id, item, actor.getId());
    }

    public void archive(Usuario actor, long id) throws SQLException {
        Articulo current = byId(id);
        requireArticleAccess(actor, current);
        dao.archive(id);
    }

    public List<Map<String, Object>> versions(Usuario actor, long id) throws SQLException {
        Articulo current = byId(id);
        requireArticleAccess(actor, current);
        return dao.versions(id);
    }

    public Articulo restoreVersion(Usuario actor, long id, long versionId) throws SQLException {
        Articulo current = byId(id);
        requireArticleAccess(actor, current);
        return dao.restoreVersion(id, versionId, actor.getId());
    }

    public List<Articulo> related(long id, int limit) throws SQLException {
        Articulo current = byId(id);
        return dao.related(id, current.getCategoriaId(), limit);
    }

    public List<Articulo> featured(int limit) throws SQLException {
        return dao.featured(limit);
    }

    public List<Articulo> trending(int limit) throws SQLException {
        return dao.trending(limit);
    }

    public void view(long id) throws SQLException {
        dao.incrementView(id);
    }

    public int publishScheduled() throws SQLException {
        return dao.publishDueScheduled();
    }

    private void normalize(Usuario actor, Articulo item, Long exceptId) throws SQLException {
        List<String> errors = ValidationUtil.article(item.getTitulo(), item.getContenido());
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join(". ", errors));
        String state = item.getEstado() == null || item.getEstado().isBlank() ? "BORRADOR" : item.getEstado().toUpperCase();
        if (!STATES.contains(state)) throw new IllegalArgumentException("Estado editorial invalido");
        if ("AUTOR".equals(actor.getRol()) && !(state.equals("BORRADOR") || state.equals("EN_REVISION"))) {
            throw new SecurityException("El autor debe enviar el articulo a revision");
        }
        if (!SecurityUtil.canPublish(actor) && (state.equals("PUBLICADO") || state.equals("PROGRAMADO") || state.equals("ARCHIVADO"))) {
            throw new SecurityException("Solo un editor puede publicar o archivar");
        }
        if (state.equals("PROGRAMADO") && (item.getProgramadoAt() == null || !item.getProgramadoAt().isAfter(OffsetDateTime.now()))) {
            throw new IllegalArgumentException("La fecha programada debe ser futura");
        }
        if (state.equals("PUBLICADO") && item.getPublicadoAt() == null) item.setPublicadoAt(OffsetDateTime.now());
        if ("AUTOR".equals(actor.getRol()) || item.getAutorId() <= 0) item.setAutorId(actor.getId());
        item.setEstado(state);
        item.setTitulo(item.getTitulo().trim());
        item.setContenido(HtmlUtil.sanitize(item.getContenido()));
        if (item.getExtracto() == null || item.getExtracto().isBlank()) {
            String plain = HtmlUtil.plainText(item.getContenido());
            item.setExtracto(plain.substring(0, Math.min(plain.length(), 240)));
        } else {
            item.setExtracto(HtmlUtil.plainText(item.getExtracto()).trim());
        }
        String baseSlug = item.getSlug() == null || item.getSlug().isBlank() ? SlugUtil.from(item.getTitulo()) : SlugUtil.from(item.getSlug());
        String slug = baseSlug;
        int suffix = 2;
        while (dao.slugExists(slug, exceptId)) slug = baseSlug + "-" + suffix++;
        item.setSlug(slug);
        if (item.getSeoTitulo() == null || item.getSeoTitulo().isBlank()) item.setSeoTitulo(item.getTitulo());
        if (item.getSeoDescripcion() == null || item.getSeoDescripcion().isBlank()) item.setSeoDescripcion(item.getExtracto());
        if (item.getSeoImagen() == null || item.getSeoImagen().isBlank()) item.setSeoImagen(item.getPortadaUrl());
        if (item.getSeoCanonical() == null || item.getSeoCanonical().isBlank()) item.setSeoCanonical(AppConfig.baseUrl() + "/articulo/" + slug);
    }

    private void requireArticleAccess(Usuario actor, Articulo item) {
        if (!SecurityUtil.canEditContent(actor)) throw new SecurityException("Acceso denegado");
        if ("AUTOR".equals(actor.getRol()) && item.getAutorId() != actor.getId()) throw new SecurityException("Acceso denegado");
    }
}
