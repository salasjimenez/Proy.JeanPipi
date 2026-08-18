package com.jeanpipi.servicios;

// Servicio de categorias.
import com.jeanpipi.dao.CategoriaDao;
import com.jeanpipi.modelos.Categoria;
import com.jeanpipi.util.SlugUtil;

import java.sql.SQLException;
import java.util.List;

public class CategoriaService {
    private final CategoriaDao dao = new CategoriaDao();

    public List<Categoria> list(boolean includeInactive) throws SQLException { return dao.list(includeInactive); }
    public Categoria bySlug(String slug) throws SQLException { return dao.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada")); }

    public Categoria create(Categoria item) throws SQLException {
        normalize(item);
        return dao.create(item);
    }

    public Categoria update(long id, Categoria item) throws SQLException {
        normalize(item);
        return dao.update(id, item);
    }

    public void delete(long id) throws SQLException { dao.delete(id); }

    private void normalize(Categoria item) {
        if (item.getNombre() == null || item.getNombre().trim().length() < 2 || item.getNombre().length() > 100) throw new IllegalArgumentException("Nombre de categoria invalido");
        item.setNombre(item.getNombre().trim());
        item.setSlug(SlugUtil.from(item.getSlug() == null || item.getSlug().isBlank() ? item.getNombre() : item.getSlug()));
        if (item.getOrden() < 0) item.setOrden(0);
    }
}
