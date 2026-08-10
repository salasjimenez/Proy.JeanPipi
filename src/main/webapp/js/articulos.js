document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    if (id) {
        cargarDetalleArticulo(id);
    }
});

function cargarDetalleArticulo(id) {
    fetch(`api/articulos?id=${id}`)
        .then(res => res.json())
        .then(art => {
            document.getElementById("titulo-articulo").textContent = art.titulo;
            document.getElementById("categoria-articulo").textContent = art.categoriaNombre || 'Moda';
            document.getElementById("imagen-articulo").src = art.imagen || 'img/default.jpg';
            document.getElementById("contenido-articulo").innerHTML = art.contenido;
        })
        .catch(err => console.error("Error al cargar artículo:", err));
}