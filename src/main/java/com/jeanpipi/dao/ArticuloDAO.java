package com.jeanpipi.dao;

import com.jeanpipi.config.ConexionDB;
import com.jeanpipi.exception.DataAccessException;
import com.jeanpipi.modelos.Articulo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticuloDAO {
    private static final String SELECT_BASE = """
            SELECT a.id, a.titulo, a.descripcion, a.contenido, a.imagen,
                   a.categoria_id, c.nombre AS categoria_nombre,
                   a.autor_id, au.nombre AS autor_nombre, a.fecha_publicacion
            FROM articulos a
            LEFT JOIN categorias c ON c.id = a.categoria_id
            LEFT JOIN autores au ON au.id = a.autor_id
            """;

    public List<Articulo> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY a.fecha_publicacion DESC, a.id DESC";
        List<Articulo> articulos = new ArrayList<>();

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                articulos.add(mapear(resultSet));
            }
            return articulos;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar los articulos.", e);
        }
    }

    public Optional<Articulo> obtenerPorId(int id) {
        String sql = SELECT_BASE + " WHERE a.id = ?";

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapear(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar el articulo.", e);
        }
    }

    public Articulo guardar(Articulo articulo) {
        String sql = """
                INSERT INTO articulos (titulo, descripcion, contenido, imagen, categoria_id, autor_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            completarStatement(statement, articulo);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new DataAccessException("La base de datos no devolvio el ID del articulo.", null);
                }
                int id = generatedKeys.getInt(1);
                return obtenerPorId(id).orElseThrow(() ->
                        new DataAccessException("No se pudo recuperar el articulo creado.", null));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear el articulo.", e);
        }
    }

    public Optional<Articulo> actualizar(int id, Articulo articulo) {
        String sql = """
                UPDATE articulos
                   SET titulo = ?, descripcion = ?, contenido = ?, imagen = ?, categoria_id = ?, autor_id = ?
                 WHERE id = ?
                """;

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            completarStatement(statement, articulo);
            statement.setInt(7, id);
            int updated = statement.executeUpdate();
            return updated == 0 ? Optional.empty() : obtenerPorId(id);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el articulo.", e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM articulos WHERE id = ?";

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el articulo.", e);
        }
    }

    public boolean categoriaExiste(int categoriaId) {
        String sql = "SELECT 1 FROM categorias WHERE id = ?";

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoriaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo validar la categoria.", e);
        }
    }

    private void completarStatement(PreparedStatement statement, Articulo articulo) throws SQLException {
        statement.setString(1, articulo.getTitulo());
        statement.setString(2, articulo.getDescripcion());
        statement.setString(3, articulo.getContenido());
        statement.setString(4, articulo.getImagen());
        statement.setInt(5, articulo.getCategoriaId());
        if (articulo.getAutorId() <= 0) {
            statement.setNull(6, Types.INTEGER);
        } else {
            statement.setInt(6, articulo.getAutorId());
        }
    }

    private Articulo mapear(ResultSet resultSet) throws SQLException {
        Articulo articulo = new Articulo();
        articulo.setId(resultSet.getInt("id"));
        articulo.setTitulo(resultSet.getString("titulo"));
        articulo.setDescripcion(resultSet.getString("descripcion"));
        articulo.setContenido(resultSet.getString("contenido"));
        articulo.setImagen(resultSet.getString("imagen"));
        articulo.setCategoriaId(resultSet.getInt("categoria_id"));
        articulo.setCategoriaNombre(resultSet.getString("categoria_nombre"));
        articulo.setAutorId(resultSet.getInt("autor_id"));
        articulo.setAutorNombre(resultSet.getString("autor_nombre"));
        articulo.setFechaPublicacion(resultSet.getTimestamp("fecha_publicacion"));
        return articulo;
    }
}
