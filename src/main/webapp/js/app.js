(() => {
    let todosLosArticulos = [];

    document.addEventListener("DOMContentLoaded", () => {
        enlazarEventos();
        cargarArticulos();
    });

    async function cargarArticulos() {
        try {
            const articulos = await window.JeanPipiApi.get("api/articulos");
            todosLosArticulos = Array.isArray(articulos) ? articulos : [];
            renderizarInicio(todosLosArticulos);
        } catch (error) {
            console.error("No se pudieron cargar los articulos:", error);
            todosLosArticulos = [];
            renderizarInicio([]);
        }
    }

    function enlazarEventos() {
        document.querySelector('[data-action="buscar"]')?.addEventListener("click", ejecutarBusqueda);
        document.getElementById("input-busqueda")?.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                ejecutarBusqueda();
            }
        });

        document.querySelectorAll("[data-categoria]").forEach((link) => {
            link.addEventListener("click", (event) => {
                event.preventDefault();
                filtrarCategoria(link.dataset.categoria || "");
            });
        });
    }

    function renderizarInicio(articulos) {
        const contenedorDestacados = document.getElementById("contenedor-destacados");
        const contenedorArticulos = document.getElementById("contenedor-articulos");
        const heroTitle = document.getElementById("hero-title");
        const heroDesc = document.getElementById("hero-desc");
        const heroBtn = document.getElementById("hero-btn");
        const heroImg = document.getElementById("hero-img");

        if (!articulos || articulos.length === 0) {
            if (heroTitle) heroTitle.textContent = "Bienvenido a JEANPIPI";
            if (heroDesc) heroDesc.textContent = "No hay artículos en la base de datos. Ve al Panel Admin para publicar el primero.";
            if (heroBtn) heroBtn.style.display = "none";
            if (heroImg) heroImg.src = "img/JeanPipi.png";
            contenedorDestacados?.replaceChildren();
            if (contenedorArticulos) {
                const wrapper = crearElemento("div", "col-12 text-center py-4");
                const message = crearElemento("p", "text-muted", "No hay publicaciones disponibles.");
                wrapper.append(message);
                contenedorArticulos.replaceChildren(wrapper);
            }
            return;
        }

        const principal = articulos[0];
        if (heroBtn) {
            heroBtn.style.display = "inline-block";
            heroBtn.href = `articulo.jsp?id=${encodeURIComponent(principal.id)}`;
        }
        if (heroTitle) heroTitle.textContent = principal.titulo || "JEANPIPI";
        if (heroDesc) heroDesc.textContent = principal.descripcion || "";
        if (heroImg) configurarImagen(heroImg, principal.imagen, principal.titulo || "Portada Principal");

        if (contenedorDestacados) {
            contenedorDestacados.replaceChildren(...articulos.slice(1, 4).map(crearTarjetaDestacada));
        }

        if (contenedorArticulos) {
            contenedorArticulos.replaceChildren(...articulos.map(crearTarjetaArticulo));
        }
    }

    function crearTarjetaDestacada(articulo) {
        const column = crearElemento("div", "col-md-4 mb-3");
        const card = crearElemento("div", "card card-magazine h-100 border-0");
        const image = document.createElement("img");
        image.className = "card-img-top";
        image.style.height = "200px";
        image.style.objectFit = "cover";
        configurarImagen(image, articulo.imagen, articulo.titulo || "Articulo");

        const body = crearElemento("div", "card-body px-0");
        const category = crearElemento("span", "text-uppercase text-muted small fw-semibold",
                articulo.categoriaNombre || "Tendencias");
        const title = crearElemento("h5", "card-title fw-bold mt-1 fs-6", articulo.titulo || "Sin titulo");
        const link = crearEnlaceArticulo(articulo.id, "btn btn-sm btn-editorial mt-2", "Leer Más");
        body.append(category, title, link);
        card.append(image, body);
        column.append(card);
        return column;
    }

    function crearTarjetaArticulo(articulo) {
        const column = crearElemento("div", "col-md-6 col-lg-4");
        const card = crearElemento("div", "card card-magazine h-100");
        const image = document.createElement("img");
        image.className = "card-img-top";
        image.style.height = "220px";
        image.style.objectFit = "cover";
        configurarImagen(image, articulo.imagen, articulo.titulo || "Articulo");

        const body = crearElemento("div", "card-body px-0");
        const category = crearElemento("span", "text-uppercase text-muted small fw-semibold",
                articulo.categoriaNombre || "Moda");
        const title = crearElemento("h5", "card-title fw-bold mt-1", articulo.titulo || "Sin titulo");
        const description = crearElemento("p", "card-text text-secondary small", articulo.descripcion || "");
        const link = crearEnlaceArticulo(articulo.id, "btn btn-editorial", "Leer Artículo");
        body.append(category, title, description, link);
        card.append(image, body);
        column.append(card);
        return column;
    }

    function crearElemento(tag, className, text) {
        const element = document.createElement(tag);
        element.className = className;
        if (text !== undefined) {
            element.textContent = text;
        }
        return element;
    }

    function crearEnlaceArticulo(id, className, text) {
        const link = crearElemento("a", className, text);
        link.href = `articulo.jsp?id=${encodeURIComponent(id)}`;
        return link;
    }

    function configurarImagen(image, source, alt) {
        image.src = imagenSegura(source);
        image.alt = alt;
        image.addEventListener("error", () => {
            if (!image.src.endsWith("/img/JeanPipi.png")) {
                image.src = "img/JeanPipi.png";
            }
        }, { once: true });
    }

    function imagenSegura(source) {
        if (!source || typeof source !== "string") {
            return "img/JeanPipi.png";
        }
        const value = source.trim();
        if (/^(https?:\/\/|\/|\.\/|img\/)/i.test(value)) {
            return value;
        }
        return "img/JeanPipi.png";
    }

    function filtrarCategoria(nombreCategoria) {
        const normalized = nombreCategoria.toLocaleLowerCase("es");
        const filtrados = todosLosArticulos.filter((articulo) =>
            articulo.categoriaNombre
            && articulo.categoriaNombre.toLocaleLowerCase("es") === normalized
        );
        renderizarInicio(filtrados);
    }

    function ejecutarBusqueda() {
        const input = document.getElementById("input-busqueda");
        if (!input) return;
        const query = input.value.trim().toLocaleLowerCase("es");
        const resultados = todosLosArticulos.filter((articulo) =>
            (articulo.titulo && articulo.titulo.toLocaleLowerCase("es").includes(query))
            || (articulo.descripcion && articulo.descripcion.toLocaleLowerCase("es").includes(query))
        );
        renderizarInicio(resultados);
    }
})();
