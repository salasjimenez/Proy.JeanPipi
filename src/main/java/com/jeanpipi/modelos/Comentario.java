package com.jeanpipi.modelos;

// Comentario de un articulo.
import java.time.OffsetDateTime;

public class Comentario {
    private long id;
    private long articuloId;
    private Long userId;
    private String nombre;
    private String email;
    private String contenido;
    private String estado;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getArticuloId() { return articuloId; }
    public void setArticuloId(long articuloId) { this.articuloId = articuloId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
