package com.jeanpipi.util;

import com.google.gson.JsonParseException;
import com.jeanpipi.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class RequestUtil {
    private RequestUtil() {
    }

    public static boolean esJson(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    public static <T> T leerJson(HttpServletRequest request, Class<T> type) throws IOException {
        try {
            T value = JsonUtil.gson().fromJson(request.getReader(), type);
            if (value == null) {
                throw new ValidationException("El cuerpo de la solicitud esta vacio.");
            }
            return value;
        } catch (JsonParseException e) {
            throw new ValidationException("El JSON enviado no es valido.");
        }
    }

    public static int parametroEntero(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new ValidationException("Falta el parametro " + name + ".");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("El parametro " + name + " debe ser numerico.");
        }
    }
}
