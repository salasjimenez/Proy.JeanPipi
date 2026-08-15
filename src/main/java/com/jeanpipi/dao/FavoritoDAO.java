package com.jeanpipi.dao;

import com.jeanpipi.config.ConexionDB;
import com.jeanpipi.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoritoDAO {
    public boolean existe(int usuarioId, int articuloId) {
        String sql = "SELECT 1 FROM favoritos WHERE usuario_id = ? AND articulo_id = ?";

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, usuarioId);
            statement.setInt(2, articuloId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar el favorito.", e);
        }
    }

    public boolean alternar(int usuarioId, int articuloId) {
        String deleteSql = "DELETE FROM favoritos WHERE usuario_id = ? AND articulo_id = ?";
        String insertSql = """
                INSERT INTO favoritos (usuario_id, articulo_id)
                VALUES (?, ?)
                ON CONFLICT (usuario_id, articulo_id) DO NOTHING
                """;

        try (Connection connection = ConexionDB.obtenerConexion()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement delete = connection.prepareStatement(deleteSql)) {
                    delete.setInt(1, usuarioId);
                    delete.setInt(2, articuloId);
                    if (delete.executeUpdate() > 0) {
                        connection.commit();
                        return false;
                    }
                }

                try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                    insert.setInt(1, usuarioId);
                    insert.setInt(2, articuloId);
                    boolean inserted = insert.executeUpdate() > 0;
                    connection.commit();
                    return inserted;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el favorito.", e);
        }
    }
}
