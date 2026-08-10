let todosLosArticulos = [];

document.addEventListener("DOMContentLoaded", () => {
    cargarArticulos();
});

function cargarArticulos() {
    fetch('api/articulos')
        .then(response => {
            if (!response.ok) throw new Error("Error al consultar la API de Java");
            return response.json();
        })
        .then(articulos => {
            console.log("Artículos recibidos desde PostgreSQL:", articulos);
            todosLosArticulos = articulos || [];
            renderizarInicio(todosLosArticulos);
        })
        .catch(error => console.error("Error en la petición:", error));
}

function renderizarInicio(articulos) {
    const contenedorDestacados = document.getElementById("contenedor-destacados");
    const contenedorArticulos = document.getElementById("contenedor-articulos");
    const heroTitle = document.getElementById("hero-title");
    const heroDesc = document.getElementById("hero-desc");
    const heroBtn = document.getElementById("hero-btn");

    // Limpia la portada "Conectando..." cuando la tabla está vacía
    if (!articulos || articulos.length === 0) {
        if (heroTitle) heroTitle.textContent = "Bienvenido a JEANPIPI";
        if (heroDesc) heroDesc.textContent = "No hay artículos en la base de datos. Ve al Panel Admin para publicar el primero.";
        if (heroBtn) heroBtn.style.display = "none";
        if (contenedorDestacados) contenedorDestacados.innerHTML = "";
        if (contenedorArticulos) {
            contenedorArticulos.innerHTML = `
                <div class="col-12 text-center py-4">
                    <p class="text-muted">No hay publicaciones disponibles.</p>
                </div>`;
        }
        return;
    }

    if (heroBtn) heroBtn.style.display = "inline-block";

    // 1. Vincular Artículo Destacado Principal (Hero)
    const principal = articulos[0];
    if (heroTitle) heroTitle.textContent = principal.titulo;
    if (heroDesc) heroDesc.textContent = principal.descripcion;
    if (heroBtn) heroBtn.href = `articulo.jsp?id=${principal.id}`;
    if (principal.imagen) {
        document.getElementById("hero-img").src = principal.imagen;
    }

    // 2. Vincular Tendencias (2º al 4º artículo)
    const destacados = articulos.slice(1, 4);
    if (contenedorDestacados) {
        contenedorDestacados.innerHTML = destacados.map(art => `
            <div class="col-md-4 mb-3">
                <div class="card card-magazine h-100 border-0">
                    <img src="${art.imagen || 'img/JeanPipi.png'}" class="card-img-top" style="height: 200px; object-fit: cover;" alt="${art.titulo}">
                    <div class="card-body px-0">
                        <span class="text-uppercase text-muted small fw-semibold">${art.categoriaNombre || 'Tendencias'}</span>
                        <h5 class="card-title fw-bold mt-1 fs-6">${art.titulo}</h5>
                        <a href="articulo.jsp?id=${art.id}" class="btn btn-sm btn-editorial mt-2">Leer Más</a>
                    </div>
                </div>
            </div>
        `).join('');
    }

    // 3. Renderizar Rejilla de Últimos Artículos
    if (contenedorArticulos) {
        contenedorArticulos.innerHTML = articulos.map(art => `
            <div class="col-md-6 col-lg-4">
                <div class="card card-magazine h-100">
                    <img src="${art.imagen || 'img/JeanPipi.png'}" class="card-img-top" style="height: 220px; object-fit: cover;" alt="${art.titulo}">
                    <div class="card-body px-0">
                        <span class="text-uppercase text-muted small fw-semibold">${art.categoriaNombre || 'Moda'}</span>
                        <h5 class="card-title fw-bold mt-1">${art.titulo}</h5>
                        <p class="card-text text-secondary small">${art.descripcion}</p>
                        <a href="articulo.jsp?id=${art.id}" class="btn btn-editorial">Leer Artículo</a>
                    </div>
                </div>
            </div>
        `).join('');
    }
}

// Funciones llamadas desde el menú de navegación en index.jsp
function filtrarCategoria(nombreCategoria) {
    const filtrados = todosLosArticulos.filter(art => 
        art.categoriaNombre && art.categoriaNombre.toLowerCase() === nombreCategoria.toLowerCase()
    );
    renderizarInicio(filtrados);
}

function ejecutarBusqueda() {
    const input = document.getElementById("input-busqueda");
    if (!input) return;
    const query = input.value.toLowerCase();
    const resultados = todosLosArticulos.filter(art => 
        (art.titulo && art.titulo.toLowerCase().includes(query)) || 
        (art.descripcion && art.descripcion.toLowerCase().includes(query))
    );
    renderizarInicio(resultados);
}