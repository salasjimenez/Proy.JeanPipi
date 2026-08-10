function alternarFavorito(articuloId) {
    fetch('api/favoritos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `articuloId=${articuloId}`
    })
    .then(res => res.json())
    .then(data => {
        if (data.exito) {
            alert("Artículo guardado en tus favoritos.");
        } else {
            alert("Inicia sesión para guardar favoritos.");
        }
    })
    .catch(err => console.error("Error:", err));
}