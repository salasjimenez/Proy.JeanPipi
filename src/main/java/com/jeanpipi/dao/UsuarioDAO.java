package com.jeanpipi.dao;

import com.jeanpipi.config.ConexionDB;
import com.jeanpipi.exception.DataAccessException;
import com.jeanpipi.modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UsuarioDAO {
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = """
                SELECT id, nombre, email, contrasena, rol, fecha_registro
                FROM usuarios
                WHERE LOWER(email) = LOWER(?)
                LIMIT 1
                """;

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapear(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar el usuario.", e);
        }
    }

    public Usuario registrar(Usuario usuario) {
        String sql = """
                INSERT INTO usuarios (nombre, email, contrasena, rol)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getContrasena());
            statement.setString(4, usuario.getRol());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                }
                return usuario;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo registrar el usuario.", e);
        }
    }

    public void actualizarContrasena(int usuarioId, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ?";

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nuevaContrasena);
            statement.setInt(2, usuarioId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la contrasena.", e);
        }
    }

    private Usuario mapear(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(resultSet.getInt("id"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setContrasena(resultSet.getString("contrasena"));
        usuario.setRol(resultSet.getString("rol"));
        usuario.setFechaRegistro(resultSet.getTimestamp("fecha_registro"));
        return usuario;
    }
}
