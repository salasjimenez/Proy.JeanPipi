document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-crear-articulo");
    if (form) {
        form.addEventListener("submit", (e) => {
            e.preventDefault();

            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());

            if (data.categoriaId) {
                data.categoriaId = parseInt(data.categoriaId, 10);
            }

            fetch('api/articulos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                },
                body: JSON.stringify(data)
            })
            .then(async res => {
                const result = await res.json();
                if (res.ok) {
                    alert("Artículo guardado exitosamente");
                    window.location.href = "index.jsp";
                } else {
                    alert("Error: " + (result.error || "No se pudo guardar"));
                }
            })
            .catch(err => {
                console.error("Error al enviar:", err);
                alert("Error de conexión al guardar el artículo");
            });
        });
    }
});