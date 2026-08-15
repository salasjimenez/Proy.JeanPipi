package com.jeanpipi.servicios;

import com.jeanpipi.dao.ArticuloDAO;
import com.jeanpipi.dto.ArticuloRequest;
import com.jeanpipi.exception.NotFoundException;
import com.jeanpipi.exception.ValidationException;
import com.jeanpipi.modelos.Articulo;

import java.util.List;

public class ArticuloService {
    private final ArticuloDAO articuloDAO;

    public ArticuloService() {
        this(new ArticuloDAO());
    }

    ArticuloService(ArticuloDAO articuloDAO) {
        this.articuloDAO = articuloDAO;
    }

    public List<Articulo> listar() {
        return articuloDAO.obtenerTodos();
    }

    public Articulo obtener(int id) {
        validarId(id);
        return articuloDAO.obtenerPorId(id)
                .orElseThrow(() -> new NotFoundException("El articulo solicitado no existe."));
    }

    public Articulo crear(ArticuloRequest request) {
        return articuloDAO.guardar(mapearYValidar(request));
    }

    public Articulo actualizar(int id, ArticuloRequest request) {
        validarId(id);
        return articuloDAO.actualizar(id, mapearYValidar(request))
                .orElseThrow(() -> new NotFoundException("El articulo solicitado no existe."));
    }

    public void eliminar(int id) {
        validarId(id);
        if (!articuloDAO.eliminar(id)) {
            throw new NotFoundException("El articulo solicitado no existe.");
        }
    }

    private Articulo mapearYValidar(ArticuloRequest request) {
        if (request == null) {
            throw new ValidationException("Los datos del articulo son obligatorios.");
        }

        String titulo = requerido(request.getTitulo(), "titulo", 200);
        String descripcion = opcional(request.getDescripcion(), 5000);
        String contenido = requerido(request.getContenido(), "contenido", 100_000);
        String imagen = opcional(request.getImagen(), 2000);
        int categoriaId = request.getCategoriaId() == null ? 1 : request.getCategoriaId();

        if (categoriaId <= 0 || !articuloDAO.categoriaExiste(categoriaId)) {
            throw new ValidationException("La categoria indicada no es valida.");
        }

        Articulo articulo = new Articulo();
        articulo.setTitulo(titulo);
        articulo.setDescripcion(descripcion);
        articulo.setContenido(contenido);
        articulo.setImagen(imagen);
        articulo.setCategoriaId(categoriaId);
        articulo.setAutorId(request.getAutorId() == null ? 0 : request.getAutorId());
        return articulo;
    }

    private String requerido(String value, String field, int maxLength) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new ValidationException("El campo " + field + " es obligatorio.");
        }
        if (normalized.length() > maxLength) {
            throw new ValidationException("El campo " + field + " supera la longitud permitida.");
        }
        return normalized;
    }

    private String opcional(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new ValidationException("Uno de los campos supera la longitud permitida.");
        }
        return normalized;
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new ValidationException("El ID del articulo no es valido.");
        }
    }
}
