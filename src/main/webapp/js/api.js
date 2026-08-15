(() => {
    class ApiError extends Error {
        constructor(message, status, payload) {
            super(message);
            this.name = "ApiError";
            this.status = status;
            this.payload = payload;
        }
    }

    async function request(url, options = {}) {
        const response = await fetch(url, {
            credentials: "same-origin",
            ...options,
            headers: {
                "Accept": "application/json",
                ...(options.headers || {})
            }
        });

        if (response.status === 204) {
            if (!response.ok) {
                throw new ApiError("La solicitud no pudo completarse.", response.status, null);
            }
            return null;
        }

        const contentType = response.headers.get("content-type") || "";
        let payload = null;
        if (contentType.includes("application/json")) {
            payload = await response.json().catch(() => null);
        } else {
            const text = await response.text();
            payload = text ? { mensaje: text } : null;
        }

        if (!response.ok) {
            const message = payload?.mensaje || payload?.error || `Error HTTP ${response.status}`;
            throw new ApiError(message, response.status, payload);
        }

        return payload;
    }

    function jsonOptions(method, body) {
        return {
            method,
            headers: { "Content-Type": "application/json;charset=UTF-8" },
            body: JSON.stringify(body)
        };
    }

    window.JeanPipiApi = Object.freeze({
        ApiError,
        get: (url) => request(url),
        post: (url, body) => request(url, jsonOptions("POST", body)),
        put: (url, body) => request(url, jsonOptions("PUT", body)),
        delete: (url) => request(url, { method: "DELETE" })
    });
})();
