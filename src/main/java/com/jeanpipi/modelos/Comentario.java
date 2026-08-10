package com.jeanpipi.modelos;

import java.sql.Timestamp;

public class Comentario {
    private int id;
    private int usuarioId;
    private String usuarioNombre;
    private int articuloId;
    private String contenido;
    private Timestamp fecha;

    public Comentario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public int getArticuloId() { return articuloId; }
    public void setArticuloId(int articuloId) { this.articuloId = articuloId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}