package com.jeanpipi.dao;

import com.jeanpipi.config.ConexionDB;
import com.jeanpipi.modelos.Usuario;

import java.sql.*;

public class UsuarioDAO {

    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, contrasena, rol) VALUES (?, ?, ?, 'LECTOR')";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getContrasena());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario autenticar(String email, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND contrasena = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}