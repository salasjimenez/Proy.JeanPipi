(() => {
    document.addEventListener("DOMContentLoaded", () => {
        const form = document.getElementById("form-crear-articulo");
        form?.addEventListener("submit", guardarArticulo);
    });

    async function guardarArticulo(event) {
        event.preventDefault();
        const form = event.currentTarget;
        const formData = new FormData(form);
        const categoriaId = Number.parseInt(formData.get("categoriaId"), 10);
        const payload = {
            titulo: formData.get("titulo")?.toString().trim() || "",
            descripcion: formData.get("descripcion")?.toString().trim() || "",
            imagen: formData.get("imagen")?.toString().trim() || null,
            contenido: formData.get("contenido")?.toString().trim() || "",
            categoriaId: Number.isInteger(categoriaId) ? categoriaId : 1
        };

        try {
            await window.JeanPipiApi.post("api/articulos", payload);
            alert("Artículo guardado exitosamente");
            window.location.href = "index.jsp";
        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                window.location.href = "login.jsp";
                return;
            }
            console.error("No se pudo guardar el articulo:", error);
            alert(`Error: ${error.message || "No se pudo guardar"}`);
        }
    }
})();
