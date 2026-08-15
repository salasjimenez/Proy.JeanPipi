package com.jeanpipi.servicios;

import com.jeanpipi.dao.ArticuloDAO;
import com.jeanpipi.dao.FavoritoDAO;
import com.jeanpipi.exception.NotFoundException;
import com.jeanpipi.exception.ValidationException;

public class FavoritoService {
    private final FavoritoDAO favoritoDAO;
    private final ArticuloDAO articuloDAO;

    public FavoritoService() {
        this(new FavoritoDAO(), new ArticuloDAO());
    }

    FavoritoService(FavoritoDAO favoritoDAO, ArticuloDAO articuloDAO) {
        this.favoritoDAO = favoritoDAO;
        this.articuloDAO = articuloDAO;
    }

    public boolean existe(int usuarioId, int articuloId) {
        validar(usuarioId, articuloId);
        return favoritoDAO.existe(usuarioId, articuloId);
    }

    public boolean alternar(int usuarioId, int articuloId) {
        validar(usuarioId, articuloId);
        if (articuloDAO.obtenerPorId(articuloId).isEmpty()) {
            throw new NotFoundException("El articulo solicitado no existe.");
        }
        return favoritoDAO.alternar(usuarioId, articuloId);
    }

    private void validar(int usuarioId, int articuloId) {
        if (usuarioId <= 0 || articuloId <= 0) {
            throw new ValidationException("Los identificadores enviados no son validos.");
        }
    }
}
