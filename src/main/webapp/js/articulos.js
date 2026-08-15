(() => {
    document.addEventListener("DOMContentLoaded", cargarDetalleDesdeUrl);

    async function cargarDetalleDesdeUrl() {
        const id = new URLSearchParams(window.location.search).get("id");
        if (!id || !/^\d+$/.test(id)) {
            mostrarError("El artículo solicitado no es válido.");
            return;
        }

        try {
            const articulo = await window.JeanPipiApi.get(`api/articulos?id=${encodeURIComponent(id)}`);
            renderizarArticulo(articulo);
        } catch (error) {
            console.error("No se pudo cargar el articulo:", error);
            mostrarError(error.status === 404 ? "El artículo solicitado no existe." : "No se pudo cargar el artículo.");
        }
    }

    function renderizarArticulo(articulo) {
        const title = document.getElementById("titulo-articulo");
        const category = document.getElementById("categoria-articulo");
        const image = document.getElementById("imagen-articulo");
        const content = document.getElementById("contenido-articulo");

        if (title) title.textContent = articulo.titulo || "Sin título";
        if (category) category.textContent = articulo.categoriaNombre || "Moda";
        if (content) {
            content.textContent = articulo.contenido || "";
            content.style.whiteSpace = "pre-line";
        }
        if (image) {
            image.src = imagenSegura(articulo.imagen);
            image.alt = articulo.titulo || "Imagen del Artículo";
            image.addEventListener("error", () => {
                image.src = "img/JeanPipi.png";
            }, { once: true });
        }
    }

    function mostrarError(message) {
        const title = document.getElementById("titulo-articulo");
        const content = document.getElementById("contenido-articulo");
        if (title) title.textContent = "JEANPIPI";
        if (content) content.textContent = message;
    }

    function imagenSegura(source) {
        if (typeof source === "string" && /^(https?:\/\/|\/|\.\/|img\/)/i.test(source.trim())) {
            return source.trim();
        }
        return "img/JeanPipi.png";
    }
})();
