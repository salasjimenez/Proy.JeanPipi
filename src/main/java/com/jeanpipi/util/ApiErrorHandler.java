package com.jeanpipi.util;

import com.jeanpipi.exception.DataAccessException;
import com.jeanpipi.exception.NotFoundException;
import com.jeanpipi.exception.ValidationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ApiErrorHandler {
    private ApiErrorHandler() {
    }

    public static void responder(HttpServletResponse response, Exception exception, Logger logger) throws IOException {
        if (response.isCommitted()) {
            logger.log(Level.SEVERE, "Error despues de enviar la respuesta HTTP.", exception);
            return;
        }

        if (exception instanceof ValidationException) {
            JsonUtil.mensaje(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
            return;
        }
        if (exception instanceof NotFoundException) {
            JsonUtil.mensaje(response, HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
            return;
        }
        if (exception instanceof DataAccessException) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
            JsonUtil.mensaje(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo completar la operacion en este momento.");
            return;
        }

        logger.log(Level.SEVERE, "Error no controlado en la API.", exception);
        JsonUtil.mensaje(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Ocurrio un error interno inesperado.");
    }
}
