package com.jeanpipi.dao;

import com.jeanpipi.config.ConexionDB;
import com.jeanpipi.modelos.Articulo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAO {

    public List<Articulo> obtenerTodos() {
        List<Articulo> lista = new ArrayList<>();
        String sql = "SELECT a.*, c.nombre AS categoriaNombre FROM articulos a LEFT JOIN categorias c ON a.categoria_id = c.id ORDER BY a.id DESC";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Articulo art = new Articulo();
                art.setId(rs.getInt("id"));
                art.setTitulo(rs.getString("titulo"));
                art.setDescripcion(rs.getString("descripcion"));
                art.setContenido(rs.getString("contenido"));
                art.setImagen(rs.getString("imagen"));
                art.setCategoriaId(rs.getInt("categoria_id"));
                art.setCategoriaNombre(rs.getString("categoriaNombre"));
                lista.add(art);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener artículos: " + e.getMessage());
        }
        return lista;
    }

    public String guardarConDetalle(Articulo articulo) {
        String sql = "INSERT INTO articulos (titulo, descripcion, contenido, imagen, categoria_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, articulo.getTitulo());
            ps.setString(2, articulo.getDescripcion());
            ps.setString(3, articulo.getContenido());
            ps.setString(4, articulo.getImagen());
            ps.setInt(5, articulo.getCategoriaId() > 0 ? articulo.getCategoriaId() : 1);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0 ? "Artículo guardado correctamente" : "Error al guardar el artículo";
        } catch (SQLException e) {
            System.err.println("❌ ERROR SQL AL GUARDAR ARTÍCULO: " + e.getMessage());
            return e.getMessage();
        }
    }
}