(() => {
    document.addEventListener("DOMContentLoaded", () => {
        document.querySelector('[data-action="favorito"]')?.addEventListener("click", alternarFavoritoActual);
    });

    async function alternarFavoritoActual() {
        const articuloId = obtenerArticuloId();
        if (!articuloId) {
            alert("No se pudo identificar el artículo.");
            return;
        }

        try {
            const result = await window.JeanPipiApi.post("api/favoritos", { articuloId });
            alert(result.favorito
                ? "Artículo guardado en tus favoritos."
                : "Artículo eliminado de tus favoritos.");
        } catch (error) {
            if (error.status === 401) {
                alert("Inicia sesión para guardar favoritos.");
                return;
            }
            console.error("No se pudo actualizar el favorito:", error);
            alert(error.message || "No se pudo actualizar el favorito.");
        }
    }

    function obtenerArticuloId() {
        const value = new URLSearchParams(window.location.search).get("id");
        if (!value || !/^\d+$/.test(value)) {
            return null;
        }
        const id = Number.parseInt(value, 10);
        return id > 0 ? id : null;
    }
})();
