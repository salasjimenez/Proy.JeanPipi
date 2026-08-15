(() => {
    document.addEventListener("DOMContentLoaded", () => {
        const form = document.querySelector('form[action="api/auth"]');
        form?.addEventListener("submit", autenticar);

        if (new URLSearchParams(window.location.search).has("error")) {
            alert("Correo o contraseña incorrectos.");
        }
    });

    async function autenticar(event) {
        event.preventDefault();
        const form = event.currentTarget;
        const formData = new FormData(form);

        try {
            const result = await window.JeanPipiApi.post("api/auth", {
                email: formData.get("email")?.toString().trim() || "",
                contrasena: formData.get("contrasena")?.toString() || ""
            });
            window.location.href = result.redirect || "index.jsp";
        } catch (error) {
            if (error.status === 401) {
                alert("Correo o contraseña incorrectos.");
                return;
            }
            console.error("No se pudo iniciar sesion:", error);
            alert(error.message || "No se pudo iniciar sesión.");
        }
    }
})();
