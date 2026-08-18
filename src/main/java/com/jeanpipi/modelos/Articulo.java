package com.jeanpipi.modelos;

// Articulo editorial.
import java.time.OffsetDateTime;

public class Articulo {
    private long id;
    private String titulo;
    private String slug;
    private String extracto;
    private String contenido;
    private String portadaUrl;
    private Long categoriaId;
    private String categoriaNombre;
    private String categoriaSlug;
    private long autorId;
    private String autorNombre;
    private String autorAvatarUrl;
    private String estado;
    private OffsetDateTime publicadoAt;
    private OffsetDateTime programadoAt;
    private boolean destacado;
    private String seoTitulo;
    private String seoDescripcion;
    private String seoCanonical;
    private String seoImagen;
    private long vistas;
    private int minutosLectura;
    private long favoritos;
    private long comentarios;
    private double tendencia;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getExtracto() { return extracto; }
    public void setExtracto(String extracto) { this.extracto = extracto; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getPortadaUrl() { return portadaUrl; }
    public void setPortadaUrl(String portadaUrl) { this.portadaUrl = portadaUrl; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public String getCategoriaSlug() { return categoriaSlug; }
    public void setCategoriaSlug(String categoriaSlug) { this.categoriaSlug = categoriaSlug; }
    public long getAutorId() { return autorId; }
    public void setAutorId(long autorId) { this.autorId = autorId; }
    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }
    public String getAutorAvatarUrl() { return autorAvatarUrl; }
    public void setAutorAvatarUrl(String autorAvatarUrl) { this.autorAvatarUrl = autorAvatarUrl; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getPublicadoAt() { return publicadoAt; }
    public void setPublicadoAt(OffsetDateTime publicadoAt) { this.publicadoAt = publicadoAt; }
    public OffsetDateTime getProgramadoAt() { return programadoAt; }
    public void setProgramadoAt(OffsetDateTime programadoAt) { this.programadoAt = programadoAt; }
    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }
    public String getSeoTitulo() { return seoTitulo; }
    public void setSeoTitulo(String seoTitulo) { this.seoTitulo = seoTitulo; }
    public String getSeoDescripcion() { return seoDescripcion; }
    public void setSeoDescripcion(String seoDescripcion) { this.seoDescripcion = seoDescripcion; }
    public String getSeoCanonical() { return seoCanonical; }
    public void setSeoCanonical(String seoCanonical) { this.seoCanonical = seoCanonical; }
    public String getSeoImagen() { return seoImagen; }
    public void setSeoImagen(String seoImagen) { this.seoImagen = seoImagen; }
    public long getVistas() { return vistas; }
    public void setVistas(long vistas) { this.vistas = vistas; }
    public int getMinutosLectura() { return minutosLectura; }
    public void setMinutosLectura(int minutosLectura) { this.minutosLectura = minutosLectura; }
    public long getFavoritos() { return favoritos; }
    public void setFavoritos(long favoritos) { this.favoritos = favoritos; }
    public long getComentarios() { return comentarios; }
    public void setComentarios(long comentarios) { this.comentarios = comentarios; }
    public double getTendencia() { return tendencia; }
    public void setTendencia(double tendencia) { this.tendencia = tendencia; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
