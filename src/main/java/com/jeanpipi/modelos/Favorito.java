package com.jeanpipi.modelos;

import java.sql.Timestamp;

public class Favorito {
    private int id;
    private int usuarioId;
    private int articuloId;
    private Timestamp fecha;

    public Favorito() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getArticuloId() { return articuloId; }
    public void setArticuloId(int articuloId) { this.articuloId = articuloId; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}